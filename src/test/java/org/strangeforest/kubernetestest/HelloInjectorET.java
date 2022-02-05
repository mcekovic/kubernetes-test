package org.strangeforest.kubernetestest;

import java.io.*;
import java.util.*;

import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelloInjectorET {

	private OkHttpClient client;
	private Random rnd;

	public static final int REPETITIONS = 1000;
	private static final int HELLOS = 5;

	@BeforeAll
	void setUp() {
		client = new OkHttpClient();
		rnd = new Random();
	}

	@RepeatedTest(REPETITIONS)
	@Execution(ExecutionMode.CONCURRENT)
	void sayHello() throws IOException {
		var request = new Request.Builder()
			.url("http://localhost/hello/K8s-" + rnd.nextInt(HELLOS))
			.build();

		try (var response = client.newCall(request).execute()) {
			var status = HttpStatus.valueOf(response.code());
			var body = response.body();
			var bodyStr = body != null ? body.string() : "";
			if (status.isError())
				throw new HttpServerErrorException(status, bodyStr);
			System.out.printf("%1$s: %2$s%n", status, bodyStr);
		}
	}
}
