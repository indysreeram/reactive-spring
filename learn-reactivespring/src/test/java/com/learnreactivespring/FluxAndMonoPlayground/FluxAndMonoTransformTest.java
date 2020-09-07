package com.learnreactivespring.FluxAndMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("Adam","Anna","Jack","Jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s-> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM","ANNA","JACK","JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingMapLength() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s-> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMapLengthRepeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s-> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMapFilter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s->s.length()>4)
                .map(s-> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                                .flatMap(s->{

                                    return Flux.fromIterable(convertToList(s));
                                })
                                .log();


        StepVerifier.create(stringFlux)
                    .expectNextCount(12)
                    .verifyComplete();
    }
    @Test
    public void transformUsingFlatMapParallel(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2)
                .flatMap((s)->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s->Flux.fromIterable(s))
                .log();


        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallelMaintainOrder(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2)
        /*        .concatMap((s)->
                        s.map(this::convertToList).subscribeOn(parallel()))*/
                .flatMapSequential((s)->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s->Flux.fromIterable(s))
                .log();


        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s)  {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s,"newValue");

    }
}
