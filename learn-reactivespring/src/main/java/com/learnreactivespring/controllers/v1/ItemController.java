package com.learnreactivespring.controllers.v1;

import com.learnreactivespring.documents.Item;
import com.learnreactivespring.repositories.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactivespring.constants.ItemConstant.ITEM_END_POINT_v1;

@RestController
@Slf4j
public class ItemController {


/*    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException runtimeException){
        log.error("Exception Caught in handleRuntimeException : {}", runtimeException);
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(runtimeException.getMessage());
    }*/

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_v1)
    public Flux<Item> getAllItems() {

        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT_v1+"/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id){

        return itemReactiveRepository.findById(id)
                .map(item-> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }


    @GetMapping(ITEM_END_POINT_v1+"/runtimeException")
    public Flux<Item> getRuntimeException(){

        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("New Run time exception has occurred")));
    }

    @PostMapping(ITEM_END_POINT_v1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_v1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id){
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ITEM_END_POINT_v1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item){

       return itemReactiveRepository.findById(id)
                .flatMap(currentitem ->{
                    currentitem.setPrice(item.getPrice());
                    currentitem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentitem);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem,HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
