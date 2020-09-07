package com.learnreactivespring.handlers;

import com.learnreactivespring.documents.Item;
import com.learnreactivespring.documents.ItemCapped;
import com.learnreactivespring.repositories.ItemReactiveCappedRepository;
import com.learnreactivespring.repositories.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);

    }

    public Mono<ServerResponse> getItem(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");

        Mono<Item> retriedItem = itemReactiveRepository.findById(id);



        return retriedItem.flatMap(item ->
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue(item)))
                    .switchIfEmpty(notFound);


    }


    public Mono<ServerResponse> createItem(ServerRequest serverRequest){

        Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);

        return itemToBeInserted.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item),Item.class)
                );
    }


    public  Mono<ServerResponse> deleteItem(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.deleteById(id),Void.class);


    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");
        Mono<Item> itemToBeUpdated = serverRequest.bodyToMono(Item.class);

        Mono<Item> updatedItem = itemToBeUpdated.flatMap(item -> {
                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);
                            });
                    return itemMono;
                }
        );


        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item)))
                        .switchIfEmpty(notFound);

    }


public Mono<ServerResponse> itemException(ServerRequest serverRequest) {

     throw new RuntimeException("A New runtime exception occurred !!!");

}

public Mono<ServerResponse> itemsStream(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);

}

}
