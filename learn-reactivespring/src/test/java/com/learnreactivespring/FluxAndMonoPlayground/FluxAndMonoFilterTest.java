package com.learnreactivespring.FluxAndMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("Adam","Anna","Jack","Jenny");

    @Test
    public void fluxFilterTest(){

        Flux<String> namesFlux = Flux.fromIterable(names)
                                .filter(s->s.startsWith("A"));

        StepVerifier.create(namesFlux.log())
                    .expectNext("Adam","Anna")
                    .verifyComplete();
    }

    @Test
    public void fluxFilterLengthTest(){

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s->s.length() > 4);

        StepVerifier.create(namesFlux.log())
                .expectNext("Jenny")
                .verifyComplete();
    }
}
