package com.christian.medicos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.christian.medicos", "com.christian.commons"})
public class MedicosMsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicosMsvApplication.class, args);
	}

}
