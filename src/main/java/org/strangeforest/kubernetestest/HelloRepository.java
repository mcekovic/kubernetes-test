package org.strangeforest.kubernetestest;

import org.springframework.data.repository.reactive.*;

public interface HelloRepository extends ReactiveCrudRepository<HelloCounter, String> {}
