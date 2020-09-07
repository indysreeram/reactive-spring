package com.learnreactivespring.repositories;

import com.learnreactivespring.documents.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item,String> {

    public Mono<Item> findByDescription(String description);
}
