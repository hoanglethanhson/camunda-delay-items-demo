package com.javanibble.camunda.examples;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasicServiceTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(BasicServiceTaskApplication.class);
  }

}