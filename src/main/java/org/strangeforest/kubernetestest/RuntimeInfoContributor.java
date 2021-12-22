package org.strangeforest.kubernetestest;

import java.lang.management.*;
import java.net.*;
import java.util.*;

import lombok.extern.slf4j.*;
import org.springframework.boot.*;
import org.springframework.boot.actuate.info.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;

@Component
@Slf4j
public class RuntimeInfoContributor implements InfoContributor {

	private static final String UNKNOWN = "Unknown";

	@Override public void contribute(Info.Builder builder) {
		builder.withDetail("os", Map.of(
			"name", System.getProperty("os.name"),
			"version", System.getProperty("os.version"),
			"hostname", getHostName(),
			"ip", getHostAddress()
		)).withDetail("runtime", Map.of(
			"jvm.version", ManagementFactory.getRuntimeMXBean().getVmVersion(),
			"spring-boot.version", getJavaPackageVersion(SpringApplication.class),
			"spring.version", getJavaPackageVersion(ApplicationContext.class)
		));
	}

	private String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException ex) {
			log.error("Error getting hostname", ex);
			return UNKNOWN;
		}
	}

	private String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException ex) {
			log.error("Error getting host address", ex);
			return UNKNOWN;
		}
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

	private static String ensureVersion(String version) {
		return version != null ? version : UNKNOWN;
	}
}
