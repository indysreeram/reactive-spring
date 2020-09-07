package com.learnreactivespring.controllers;

import com.learnreactivespring.domains.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {


    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve(){

        return  webClient.get().uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in client Project using retrieve");


    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange(){

        return  webClient.get().uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in client Project using exchange");


    }

    @GetMapping("/client/error/retrieve")
    public Flux<Item> getError() {

        return webClient.get()
                .uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError,(clientResponse -> {
                        Mono<String> errorMono =clientResponse.bodyToMono(String.class);
                       return errorMono.flatMap((errorMessage) ->{
                            log.error("Error Message is "+ errorMessage);
                            throw new RuntimeException(errorMessage);
                        });
                }))
                .bodyToFlux(Item.class);

    }

    @GetMapping("/client/error/exchange")
    public Flux<Item> getErrorExchange() {
        return webClient.get()
                .uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError() ){
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errormessage ->{
                                    log.error("Error Message in exchange is :" + errormessage);
                                    throw new RuntimeException(errormessage);
                                });
                    } else
                    {
                        return clientResponse.bodyToFlux(Item.class);
                    }
                });
    }


    @GetMapping("/client/retrieve/singleItem")
    public Mono<Item> getItemUsingRetrieve(){

        String id = "ABC";

        return  webClient.get().uri("/v1/items/{id}",id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Items in client Project using retrieve for a Single Item");


    }

    @GetMapping("/client/exchange/singleItem")
    public Mono<Item> getItemUsingExchange(){

        String id = "ABC";

        return  webClient.get().uri("/v1/items/{id}",id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Items in client Project using exchange for a Single Item");


    }


    @PostMapping("/client/createItem")
    public Mono<Item> createItem(@RequestBody Item item){

        return  webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created new item :: ");

    }


    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> updateItem(@PathVariable("id") String id,@RequestBody Item item){

        return webClient.put().uri("/v1/items/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Item updated :: ");
    }

    @DeleteMapping("/client/deleteItem/{id}")
    public  Mono<Void> deleteItem(@PathVariable("id") String id) {

    return webClient.delete().uri("/v1/items/{id}",id)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .log("Deleted Item :: ");

    }
}
