package com.toma.codex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.toma.codex")
@ConfigurationPropertiesScan(basePackages = "com.toma.codex")
public class CodexMngApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodexMngApplication.class, args);
	}
}
