package org.strangeforest.kubernetestest;

import lombok.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

import static java.lang.String.*;

@SpringBootApplication
public class KubernetesTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesTestApplication.class, args);
	}

	@RestController
	@RequestMapping("/hello")
	@ConfigurationProperties("hello")
	@Getter @Setter
	public static class HelloController {

		private String defaultName = "World";

		@GetMapping
		private Mono<String> hello() {
			return hello(defaultName);
		}

		@GetMapping("/{name}")
		private Mono<String> hello(@PathVariable String name) {
			return Mono.just(format("Hello %1$s!", name));
		}
	}
}
