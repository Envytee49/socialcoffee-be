package com.example.socialcoffee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SocialcoffeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialcoffeeApplication.class, args);
	}

}
