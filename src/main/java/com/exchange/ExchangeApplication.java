package com.exchange;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeApplication.class, args);
	}
	
	@Component
	class ExchangeCommandLineRunner implements CommandLineRunner{
		@Override
		public void run(String... args) throws Exception {

		}
	}
}
