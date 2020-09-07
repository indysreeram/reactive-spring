package com.learnreactivespring.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> getIntegerFlux() {

        return Flux.just(1,2,3,4)
                .delayElements(Duration.ofSeconds(1))
                .log();

    }

    @GetMapping(value = "/fluxstream",produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getIntegerFluxStream() {

        return Flux.interval(Duration.ofSeconds(1))
                     .log();

    }


    @GetMapping("/mono")
    public Mono<Integer> getIntegerMono() {

        return Mono.just(1).log();

    }
}
