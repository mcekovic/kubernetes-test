package org.strangeforest.kubernetestest;

import java.util.*;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;
import reactor.util.retry.*;

@Service
@Slf4j
public class HelloService {

	@Autowired
	private HelloRepository repository;

	public Mono<HelloCounter> hello(String name) {
		log.trace("hello(name={})", name);
		var aName = processName(name);
		return repository.findById(aName)
			.defaultIfEmpty(new HelloCounter(aName))
			.map(HelloCounter::inc)
			.flatMap(repository::save)
			.retryWhen(Retry.indefinitely().filter(ex -> ex instanceof OptimisticLockingFailureException || ex instanceof DataIntegrityViolationException));
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
