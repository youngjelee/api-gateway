package org.ljy.apigateway.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BasicController {

    @GetMapping("/")
    public Mono<String> basic() {
        return Mono.just("OK");
    }

     @GetMapping("/health-check")
    public Mono<String> healthCheck() {
        return Mono.just("OK");
    }
}