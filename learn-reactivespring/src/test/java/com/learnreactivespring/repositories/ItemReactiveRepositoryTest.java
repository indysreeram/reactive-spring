package com.learnreactivespring.repositories;

import com.learnreactivespring.documents.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null,"Smasung TV",400.0),
                                        new Item(null,"LG TV",450.0),
                                        new Item(null,"Apple Watch",299.99),
                                        new Item(null,"Beats Headphones",149.99),
                                        new Item("ABC","Bose Headphones",149.99));

    @Before
    public void setUp()  {

        itemReactiveRepository.deleteAll()
                    .thenMany(Flux.fromIterable(itemList))
                    .flatMap(itemReactiveRepository::save)
                    .doOnNext(item ->{
                        System.out.println("Inserted item is = " + item);
                    })
                    .blockLast();


    }

    @Test
    public void getAllItems() {

        StepVerifier.create(itemReactiveRepository.findAll().log())
        .expectSubscription()
        .expectNextCount(5)
        .verifyComplete();
    }

    @Test
    public void getItemById() {

        StepVerifier.create(itemReactiveRepository.findById("ABC").log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescriptionTest() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {

        Item newItem= new Item(null,"Google Mini",30.00);

       Mono<Item> savedItem = itemReactiveRepository.save(newItem);

       StepVerifier.create(savedItem.log())
                    .expectSubscription()
                    .expectNextMatches(item1 -> (item1.getId()!=null) && (item1.getDescription().equals("Google Mini")))
                    .verifyComplete();


    }

    @Test
    public void updateItem() {

        Double newPrice =520.00;

       Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                return item;
                }
                )
                .flatMap(item -> {return itemReactiveRepository.save(item);});

       StepVerifier.create(updatedItem.log())
                .expectSubscription()
               .expectNextMatches(item -> item.getPrice() == 520.00)
               .verifyComplete();


    }


    @Test
    public void deleteItemById(){

        Mono<Void> deleteItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> {
                    return  itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deleteItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List: "))
                    .expectNextCount(4)
                    .verifyComplete();

    }

    @Test
    public void deleteItemBy(){

        Mono<Void> deleteItem = itemReactiveRepository.findByDescription("LG TV")
                                                    .flatMap((item) -> {
                                                     return  itemReactiveRepository.delete(item);
                });

        StepVerifier.create(deleteItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List: "))
                .expectNextCount(4)
                .verifyComplete();

    }



}
