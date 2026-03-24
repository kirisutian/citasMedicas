package com.christian.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.christian.citas", "com.christian.commons"})
public class CitasMsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasMsvApplication.class, args);
	}

}
