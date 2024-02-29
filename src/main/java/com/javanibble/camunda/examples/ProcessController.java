package com.javanibble.camunda.examples;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProcessController.class.getName());

    @Autowired
    private RuntimeService runtimeService;

    @PostMapping(value = "/process-key/start/{key}")
    public void start(@PathVariable String key) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        LOGGER.info("isSuspended=" + processInstance.isSuspended());
    }

}
