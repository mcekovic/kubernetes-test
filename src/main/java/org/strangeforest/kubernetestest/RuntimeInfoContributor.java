package org.strangeforest.kubernetestest;

import java.lang.management.*;
import java.util.*;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.actuate.info.*;
import org.springframework.context.*;
import org.springframework.r2dbc.core.*;
import org.springframework.stereotype.*;

import static org.strangeforest.kubernetestest.Util.*;

@Component
@Slf4j
public class RuntimeInfoContributor implements InfoContributor {

	@Autowired
	private DatabaseClient dbClient;

	@Override public void contribute(Info.Builder builder) {
		builder.withDetail("os", Map.of(
			"name", System.getProperty("os.name"),
			"version", System.getProperty("os.version"),
			"hostname", hostName(),
			"ip", hostAddress()
		)).withDetail("runtime", Map.of(
			"jvm.version", ManagementFactory.getRuntimeMXBean().getVmVersion(),
			"spring-boot.version", getJavaPackageVersion(SpringApplication.class),
			"spring.version", getJavaPackageVersion(ApplicationContext.class),
			"postgresql.version", getPostgreSqlVersion()
		));
	}

	private String getJavaPackageVersion(Class<?> cls) {
		try {
			return ensureVersion(cls.getPackage().getImplementationVersion());
		}
		catch (Exception ex) {
			log.error("Error getting Java Package version for class " + cls.getName(), ex);
			return UNKNOWN;
		}
	}

	private String getPostgreSqlVersion() {
		try {
			return String.valueOf(dbClient.sql("SELECT version()")
				.fetch().one()
				.blockOptional().map(r -> r.get("version"))
				.orElse(UNKNOWN));
		}
		catch (Exception ex) {
			log.error("Error getting PostgreSQL version", ex);
			return UNKNOWN;
		}
	}

	private static String ensureVersion(String version) {
		return version != null ? version : UNKNOWN;
	}
}
