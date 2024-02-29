package com.javanibble.camunda.examples.schedule;


import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class DelayItemsHandlingSchedule {

    @Autowired
    RuntimeService runtimeService;

    @Scheduled(fixedRateString = "${app.delay-items.delay}")
    public void handleDelayItems() {

    }

    private void executeRemainingItems() {
        runtimeService.createProcessInstanceByKey("app.delay-items.delay")

                .execute();
    }
}
