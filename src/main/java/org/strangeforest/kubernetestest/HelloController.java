package org.strangeforest.kubernetestest;

import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.properties.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

import static java.lang.String.*;
import static org.strangeforest.kubernetestest.Util.*;

@RestController
@RequestMapping("/hello")
@ConfigurationProperties("hello")
@Getter @Setter
public class HelloController {

	@Autowired
	private HelloService service;

	private String defaultName = "World";

	@GetMapping
	private Mono<String> hello() {
		return hello(defaultName);
	}

	@GetMapping("/{name}")
	private Mono<String> hello(@PathVariable String name) {
		return service.hello(name).map(this::message);
	}

	private String message(HelloCounter counter) {
		return format("Hello %1$s from %2$s! [%3$d]", counter.getName(), hostName(), counter.getCount());
	}
}
