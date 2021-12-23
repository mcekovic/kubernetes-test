package org.strangeforest.kubernetestest;

import java.util.*;

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
	private HelloRepository repository;

	private String defaultName = "World";

	@GetMapping
	private Mono<String> hello() {
		return hello(defaultName);
	}

	@GetMapping("/{name}")
	private Mono<String> hello(@PathVariable String name) {
		return repository.findById(name)
			.defaultIfEmpty(new HelloCounter(name))
			.map(HelloCounter::inc)
			.flatMap(repository::save)
			.map(this::message);
	}

	private String message(HelloCounter counter) {
		return format("Hello %1$s from %2$s! [%3$d]", name(counter.getName()), hostName(), counter.getCount());
	}

	private static String name(String name) {
		return switch (name) {
			default -> name;
			case "oom" -> oom();
			case "burn" -> burn();
		};
	}

	private static String burn() {
		var seed = 0.5;
		for (int i = 0; i < 100000; i++)
			seed = Math.sin(1.0 + seed);
		return String.valueOf(seed);
	}

	private static String oom() {
		return Arrays.toString(new byte[1024 * 1024 * 1024]);
	}
}
