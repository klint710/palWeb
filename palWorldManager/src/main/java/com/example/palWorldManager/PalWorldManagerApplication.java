package com.example.palWorldManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.palWorldManager.controller"})
public class PalWorldManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PalWorldManagerApplication.class, args);
	}

}
