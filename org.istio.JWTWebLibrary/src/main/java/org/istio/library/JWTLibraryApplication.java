package org.istio.library;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.istio.library.controller.LibraryAccess;
import org.istio.library.generated.LibraryGrpc;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class JWTLibraryApplication {

	private LibraryAccess libraryAccess = new LibraryAccess();

	public static void main(String[] args) {
		SpringApplication.run(JWTLibraryApplication.class, args);
	}

	@Bean
	public LibraryAccess libraryAccess() {
		return libraryAccess;
	}

}

