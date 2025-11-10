package com.example.SkillSwap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkillSwapApplication {
	public static void main(String[] args) {
		SpringApplication.run(SkillSwapApplication.class, args);
	}
}
