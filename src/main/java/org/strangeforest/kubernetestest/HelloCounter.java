package org.strangeforest.kubernetestest;

import lombok.*;
import org.springframework.data.annotation.*;

@Data
public class HelloCounter {

	@Id private String name;
	private long count;
	@Version private long version;

	public HelloCounter(String name) {
		this.name = name;
	}

	public HelloCounter inc() {
		++count;
		return this;
	}
}
