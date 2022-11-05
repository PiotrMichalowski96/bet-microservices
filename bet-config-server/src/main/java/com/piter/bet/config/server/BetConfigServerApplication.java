package com.piter.bet.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class BetConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetConfigServerApplication.class, args);
	}

}
