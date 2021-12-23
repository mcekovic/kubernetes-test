package org.strangeforest.kubernetestest;

import org.flywaydb.core.*;
import org.springframework.boot.autoconfigure.flyway.*;
import org.springframework.context.annotation.*;

import static org.flywaydb.core.Flyway.*;

@Configuration
public class FlywayConfiguration {

	@Bean
	public FlywayProperties flywayProperties() {
		return new FlywayProperties();
	}

	@Bean(initMethod = "migrate")
	public Flyway flyway(FlywayProperties props) {
		return new Flyway(configure()
			.dataSource(props.getUrl(), props.getUser(), props.getPassword())
			.cleanOnValidationError(props.isCleanOnValidationError())
		);
	}
}