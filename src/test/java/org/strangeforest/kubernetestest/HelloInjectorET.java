package org.strangeforest.kubernetestest;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import okhttp3.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

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
	void sayHello() {
		var customThreadPool = new ForkJoinPool(PARALLELISM);
		var task = customThreadPool.submit(() ->
			rangeClosed(1, REPETITIONS)
				.parallel()
				.forEach(this::doSayHello)
		);
		task.join();
	}

	private void doSayHello(int repetition) {
		var request = new Request.Builder()
			.url("http://localhost:8080/hello/K8s-" + rnd.nextInt(HELLOS))
			.build();

		try (var response = client.newCall(request).execute()) {
			var status = HttpStatus.valueOf(response.code());
			var body = response.body();
			var bodyStr = body != null ? body.string() : "";
			if (status.isError())
				throw new HttpServerErrorException(status, bodyStr);
			if (repetition % PRINT_EACH == 0)
				System.out.printf("%1$s: %2$s%n", status, bodyStr);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
