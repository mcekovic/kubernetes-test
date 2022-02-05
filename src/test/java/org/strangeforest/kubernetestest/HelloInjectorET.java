package org.strangeforest.kubernetestest;

import java.util.*;
import java.util.concurrent.*;

import okhttp3.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelloInjectorET {

	private OkHttpClient client;
	private Random rnd;

	private static final int REPETITIONS = 10000;
	private static final int PARALLELISM = 20;
	private static final int HELLOS = 5;
	private static final int PRINT_EACH = 100;

	@BeforeAll
	void setUp() {
		client = new OkHttpClient();
		rnd = new Random();
	}

	@Test
	void sayHellos() throws Exception {
		var customThreadPool = new ForkJoinPool(PARALLELISM);
		var task = customThreadPool.submit(() ->
			rangeClosed(1, REPETITIONS)
				.parallel()
				.mapToObj(this::sayHello)
				.collect(groupingBy(Result::message))
		);
		var error = task.join().values().stream()
			.map(rl -> rl.stream().reduce(Result::add).orElseThrow())
			.sorted(comparing(Result::count).reversed())
			.peek(System.out::println)
			.filter(Result::isError)
			.findFirst();
		if (error.isPresent())
			throw error.get().exception();
	}

	private Result sayHello(int repetition) {
		var request = new Request.Builder()
			.url("http://localhost:8080/hello/K8s-" + rnd.nextInt(HELLOS))
			.build();

		try (var response = client.newCall(request).execute()) {
			var status = HttpStatus.valueOf(response.code());
			var body = response.body();
			if (repetition % PRINT_EACH == 0)
				System.out.printf("%1$s: %2$s%n", status, body != null ? body.string() : "");
			return new Result(status);
		}
		catch (Exception ex) {
			return new Result(ex);
		}
	}

	static record Result(String message, int count, Object error) {

		Result(HttpStatus status) {
			this(status.name(), 1, status.isError() ? status : null);
		}

		Result(Exception ex) {
			this(ex.getMessage(), 1, ex);
		}

		boolean isError() {
			return error != null;
		}

		Result add(Result other) {
			if (!Objects.equals(message, other.message))
				throw new IllegalArgumentException("Different results cannot be added");
			return new Result(message, count + other.count, error);
		}

		Exception exception() {
			if (error instanceof Exception ex)
				return ex;
			else if (error instanceof HttpStatus status) {
				if (status.is4xxClientError())
					return new HttpClientErrorException(status);
				else if (status.is5xxServerError())
					return new HttpServerErrorException(status);
			}
			throw new IllegalArgumentException("Unknown error: " + error);
		}
	}
}
