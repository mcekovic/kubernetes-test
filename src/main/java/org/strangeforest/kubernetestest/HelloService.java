package org.strangeforest.kubernetestest;

import java.util.*;

import dev.failsafe.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Service
@Slf4j
public class HelloService {

	@Autowired
	private HelloRepository repository;

	private static final RetryPolicy<Object> RETRY_POLICY = RetryPolicy.builder()
		.handle(OptimisticLockingFailureException.class)
		.withMaxRetries(Integer.MAX_VALUE)
		.onFailedAttempt(e -> log.warn(e.getLastFailure().getMessage()))
		.build();

	public Mono<HelloCounter> hello(String name) {
		var aName = processName(name);
		return Failsafe.with(RETRY_POLICY).get(() -> doHello(aName));
	}

	private Mono<HelloCounter> doHello(String name) {
		return repository.findById(name)
			.defaultIfEmpty(new HelloCounter(name))
			.map(HelloCounter::inc)
			.flatMap(repository::save);
	}

	private static String processName(String name) {
		return switch (name) {
			default -> name;
			case "oom" -> oom();
			case "burn" -> burn();
		};
	}

	private static String burn() {
		var seed = new Random().nextDouble(1.0);
		for (int i = 0; i < 10_000_000; i++)
			seed = Math.sin(1.0 + seed);
		return String.valueOf(seed);
	}

	private static String oom() {
		return Arrays.toString(new byte[1024 * 1024 * 1024]);
	}
}
