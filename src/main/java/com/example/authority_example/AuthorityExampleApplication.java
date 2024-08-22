package com.example.authority_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AuthorityExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorityExampleApplication.class, args);
	}

}
