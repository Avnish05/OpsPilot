package com.opspilot.opspilot;

import org.springframework.boot.SpringApplication;

public class TestOpspilotApplication {

	public static void main(String[] args) {
		SpringApplication.from(OpspilotApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
