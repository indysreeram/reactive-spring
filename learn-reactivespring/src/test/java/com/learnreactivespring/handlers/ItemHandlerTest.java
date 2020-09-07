package com.learnreactivespring.handlers;

import com.learnreactivespring.documents.Item;
import com.learnreactivespring.repositories.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.constants.ItemConstant.ITEM_FUNCTIONAL_END_POINT_v1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> data(){
        return Arrays.asList(new Item(null,"Samsung TV",399.99),
                new Item(null,"LG TV",329.99),
                new Item(null,"Apple Watch",349.99),
                new Item("ABC","Beats Headphone",19.99)
        );
    }


    @Before
    public void setup(){

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data())
                        .flatMap(itemReactiveRepository::save)
                        .doOnNext(item ->{
                            System.out.println("Inserted item is :: " + item);
                        })).blockLast();

    }


    @Test
    public void getAllItems() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_v1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);

    }


    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_v1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith((response) ->{
                            List<Item> items = response.getResponseBody();

                            items.forEach(item ->
                                    assertTrue(item.getId()!=null));
                        }
                );

    }

    @Test
    public void getAllItems_approach3() {
        Flux<Item> itemFlux = webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_v1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

    }


    @Test
    public void getItemById() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_v1+"/ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",19.99);
    }

    @Test
    public void getItemByIdNotFound() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_v1+"/ABC1")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItemTest(){

        Item itemToCreate = new Item( null,"Samsung Monitor",129.99);

        webTestClient.post().uri(ITEM_FUNCTIONAL_END_POINT_v1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToCreate),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.price",129.99);

    }

    @Test
    public void deleteItemTest() {

        webTestClient.delete().uri(ITEM_FUNCTIONAL_END_POINT_v1+"/ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemTest() {

        Item itemToUpdate = new Item( null,"Samsung Monitor",129.99);

        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_v1+"/ABC")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToUpdate),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",129.99);


    }

    @Test
    public void updateItemNotFoundTest() {

        Item itemToUpdate = new Item( null,"Samsung Monitor",129.99);

        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_v1+"/ABC1")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToUpdate),Item.class)
                .exchange()
                .expectStatus().isNotFound();


    }

    @Test
    public void runTimeExceptionTest() {

        webTestClient.get().uri("/fun/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message","A New runtime exception occurred !!!");
    }

}
