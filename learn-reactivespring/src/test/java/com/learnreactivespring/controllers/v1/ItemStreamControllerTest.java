package com.learnreactivespring.controllers.v1;

import com.learnreactivespring.constants.ItemConstant;
import com.learnreactivespring.documents.ItemCapped;
import com.learnreactivespring.repositories.ItemReactiveCappedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    WebTestClient webTestClient;
    @Before
    public void setUp(){

        reactiveMongoOperations.dropCollection(ItemCapped.class);
        reactiveMongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20).size(50000).capped()).block();


        Flux<ItemCapped> itemCappedFlux=  Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item "+i,100.00+i))
                .take(5);

        itemReactiveCappedRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted Item is " +itemCapped);
                }).blockLast();

    }

    @Test
    public void testStreamAllItems(){

       Flux<ItemCapped> itemCappedFlux =webTestClient.get().uri(ItemConstant.ITEM_STREAM_END_POINT_v1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);


        StepVerifier.create(itemCappedFlux)
                    .expectNextCount(5)
                    .thenCancel()
                    .verify();
    }
}
