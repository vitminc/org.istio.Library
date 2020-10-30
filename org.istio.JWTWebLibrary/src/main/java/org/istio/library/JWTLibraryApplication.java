package org.istio.library;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.istio.library.controller.LibraryAccess;
import org.istio.library.generated.LibraryGrpc;
import org.istio.library.ssl.SSLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@Configuration
public class JWTLibraryApplication implements ApplicationRunner {
	private static final Logger logger = Logger.getLogger(JWTLibraryApplication.class.getName());

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(JWTLibraryApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// For test only, should be removed in production system
		if ("true".equalsIgnoreCase(environment.getProperty("library.no-ssl"))) {
			try {
				SSLUtil.turnOffSslChecking();
				logger.log(Level.WARNING, "Run with SSL ignore mode");
			} catch (NoSuchAlgorithmException | KeyManagementException e) {
				logger.log(Level.WARNING, "SSL ignore error", e);
			}
		}
	}

	@Bean
	public LibraryAccess libraryAccess() {
		// Defined in the application.properties
		String host = environment.getProperty("library.backend-host");
		int port = Integer.parseInt(environment.getProperty("library.backend-port"));
		logger.log(Level.WARNING, "gRPC Host:" + host+", Port:"+port);

		return new LibraryAccess(host, port);
	}

}

