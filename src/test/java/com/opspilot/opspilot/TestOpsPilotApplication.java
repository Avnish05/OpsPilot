package com.opspilot.opspilot;

import com.opspilot.OpsPilotApplication;
import org.springframework.boot.SpringApplication;

public class TestOpsPilotApplication {

	public static void main(String[] args) {
		SpringApplication.from(OpsPilotApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
