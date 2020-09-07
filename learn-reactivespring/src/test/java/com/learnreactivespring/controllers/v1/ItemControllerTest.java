package com.learnreactivespring.controllers.v1;

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

import static com.learnreactivespring.constants.ItemConstant.ITEM_END_POINT_v1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

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
        webTestClient.get().uri(ITEM_END_POINT_v1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);

    }


    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_END_POINT_v1)
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
       Flux<Item> itemFlux = webTestClient.get().uri(ITEM_END_POINT_v1)
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
        webTestClient.get().uri(ITEM_END_POINT_v1+"/ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",19.99);
    }

    @Test
    public void getItemByIdNotFound() {
        webTestClient.get().uri(ITEM_END_POINT_v1+"/ABC1")
                .exchange()
                .expectStatus().isNotFound();

    }


    @Test
    public void createItem() {

        Item newItem = new Item(null,"Iphone X",999.99);

        webTestClient.post().uri(ITEM_END_POINT_v1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(newItem),Item.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.description").isEqualTo("Iphone X")
                    .jsonPath("$.price").isEqualTo(999.99);




    }


    @Test
    public void deleteItem() {

        webTestClient.delete().uri(ITEM_END_POINT_v1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }


    @Test
    public void updatedItem() {

        Double newPrice = 16.99;
      Item updateItem= new Item(null,"Beats Headphone",newPrice);

        webTestClient.put().uri(ITEM_END_POINT_v1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateItem),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",16.99);
    }

    @Test
    public void updatedItemNotFound() {

        Double newPrice = 16.99;
        Item updateItem= new Item(null,"Beats Headphone",newPrice);

        webTestClient.put().uri(ITEM_END_POINT_v1.concat("/{id}"),"DEF")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateItem),Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void runtimeException(){

        webTestClient.get().uri(ITEM_END_POINT_v1+"/runtimeException")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody(String.class)
                    .isEqualTo("New Run time exception has occurred");


    }

}
