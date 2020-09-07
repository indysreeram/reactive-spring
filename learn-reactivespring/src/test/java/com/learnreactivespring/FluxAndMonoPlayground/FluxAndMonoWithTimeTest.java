package com.learnreactivespring.FluxAndMonoPlayground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    @DisplayName("Example to show no blocking infinite sequence")
    public void infiniteSequence() {
        Flux<Long> inifinteFlux = Flux.interval(Duration.ofMillis(100))
                                    .log();  //long value start at 0 ->......


        inifinteFlux.subscribe(element-> System.out.println("Value is :"+element));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void infiniteSequenceTest() {
        Flux<Long> inifinteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();  //long value start at 0 ->......


        StepVerifier.create(inifinteFlux)
                    .expectSubscription()
                    .expectNext(0l,1l,2l)
                    .verifyComplete();

    }

    @Test
    public void infiniteSequenceMap() {
        Flux<Integer> inifinteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> l.intValue())
                .take(3)
                .log();  //long value start at 0 ->......


        StepVerifier.create(inifinteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();

    }


    @Test
    public void infiniteSequenceMapWithDelay() {
        Flux<Integer> inifinteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> l.intValue())
                .take(3)
                .log();  //long value start at 0 ->......


        StepVerifier.create(inifinteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();

    }


}
