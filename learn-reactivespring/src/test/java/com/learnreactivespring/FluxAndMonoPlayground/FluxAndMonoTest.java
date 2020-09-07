package com.learnreactivespring.FluxAndMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxElementsTest() {

        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
//                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                       .concatWith(Flux.just("After Error"))
                                        .log();

        stringFlux
                .subscribe(System.out::println,
                        (e) ->System.err.println("Exception is "+ e),
                        ()->System.out.println("Completed")
                        );

    }


    @Test
    public void fluxElementsTestWithoutError(){

        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                                    .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Spring")
                    .expectNext("Spring Boot")
                   .expectNext("Reactive Spring")
                    .verifyComplete();


    }

    @Test
    public void fluxElementsTestWithError(){

        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
              //  .expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();


    }

    @Test
    public void fluxElementsTestWithError1(){

        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring","Spring Boot","Reactive Spring")
                .expectErrorMessage("Exception Occurred")
                .verify();


    }

    @Test
    public void fluxElementsCountTestWithError(){

        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                    .expectNextCount(3)
                //  .expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();


    }


    @Test
    public void monoElementTest() {

        Mono<String> stringMono = Mono.just("Spring")
                                    .log();

        StepVerifier.create(stringMono)
                    .expectNext("Spring")
                    .verifyComplete();
    }


    @Test
    public void monoElementTestWithError() {

        Mono<Object> stringMono = Mono.error(new RuntimeException("Exception Occurred")).log();

        StepVerifier.create(stringMono)
                .expectError(RuntimeException.class)
                .verify();
    }

}
