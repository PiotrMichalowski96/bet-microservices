package com.piter.match.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.piter.match.management.rest")
@SpringBootApplication
public class MatchManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(MatchManagementApplication.class, args);
  }
}
