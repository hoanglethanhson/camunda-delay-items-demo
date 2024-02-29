package com.javanibble.camunda.examples;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BasicServiceTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(BasicServiceTaskApplication.class);
  }

}