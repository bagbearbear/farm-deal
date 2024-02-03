package com.farmdeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FarmDealApplication {
	public static void main(String[] args) {
		SpringApplication.run(FarmDealApplication.class, args);
	}

}
