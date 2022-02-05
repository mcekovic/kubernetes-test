package org.strangeforest.kubernetestest;

import java.io.*;

import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.springframework.http.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelloInjectorET {

	private OkHttpClient client;

	@BeforeAll
	void setUp() {
		client = new OkHttpClient();
	}

	@RepeatedTest(100)
	@Execution(ExecutionMode.CONCURRENT)
	void sayHello(RepetitionInfo repetitionInfo) throws IOException {
		extracted("K8s-" + repetitionInfo.getCurrentRepetition() % 10);
	}

	private void extracted(final String name) throws IOException {
		var request = new Request.Builder()
			.url("http://localhost/hello/" + name)
			.build();

		try (var response = client.newCall(request).execute()) {
			var status = HttpStatus.valueOf(response.code());
			var body = response.body();
			System.out.printf("%1$s: %2$s%n", status, body != null ? body.string() : "");
		}
	}
}
