package com.learnreactivespring.FluxAndMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("Adam","Anna","Jack","Jenny");

    @Test
    public void fluxUsingIterable(){
        Flux<String> stringFlux =Flux.fromIterable(names)
                                    .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Adam","Anna","Jack","Jenny")
                    .verifyComplete();
    }


    @Test
    public void fluxUsingArray(){

        String[] names = new String[]{"Adam","Anna","Jack","Jenny"};

        Flux<String> stringFlux = Flux.fromArray(names).log();

        StepVerifier.create(stringFlux)
                .expectNext("Adam","Anna","Jack","Jenny")
                .verifyComplete();

    }

    @Test
    public void fluxUsingStream(){

        Flux<String> namesFlux =Flux.fromStream(names.stream()).log();

        StepVerifier.create(namesFlux)
                    .expectNext("Adam","Anna","Jack","Jenny")
                    .verifyComplete();
    }


    @Test
    public void monoUsingJustOrEmpty() {

        Mono<String> stringMono = Mono.justOrEmpty(null);

        StepVerifier.create(stringMono.log())
                .verifyComplete();



    }



    @Test
    public void monoUsingSupplier(){

        Supplier<String> stringSupplier = () -> "Adam";

        Mono<String> supplierMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(supplierMono.log())
                    .expectNext("Adam")
                    .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){

        Flux<Integer> integerFlux = Flux.range(1,5);

        StepVerifier.create(integerFlux.log())
                    .expectNext(1,2,3,4,5)
                    .verifyComplete();
    }
}
