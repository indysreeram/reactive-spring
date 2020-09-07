package com.learnreactivespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LearnReactivespringApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnReactivespringApplication.class, args);
		System.out.println("Available virtual processors for this application is "+Runtime.getRuntime().availableProcessors());
	}

}
