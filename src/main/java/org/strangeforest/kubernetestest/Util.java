package org.strangeforest.kubernetestest;

import java.net.*;

import lombok.extern.slf4j.*;

@Slf4j
abstract class Util {

	static final String UNKNOWN = "Unknown";

	static String hostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException ex) {
			log.error("Error getting hostname", ex);
			return UNKNOWN;
		}
	}

	static String hostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException ex) {
			log.error("Error getting host address", ex);
			return UNKNOWN;
		}
	}
}
