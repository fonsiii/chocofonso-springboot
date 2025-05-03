package com.vs2dam.azarquiel.chocofonso_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChocofonsoSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChocofonsoSpringbootApplication.class, args);
	}

}
