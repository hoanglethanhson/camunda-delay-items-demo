package com.javanibble.camunda.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanibble.camunda.examples.util.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class RetrieveCoffeeOrderDelegate implements JavaDelegate {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(RetrieveCoffeeOrderDelegate.class.getName());

    public void execute(DelegateExecution execution) throws Exception {
        String coffeeOrder = (String) execution.getVariable("order");
        String msisdn = (String) execution.getVariable("msisdn");
        String currentActivityId = execution.getCurrentActivityId();
        String redisKey = StringUtils.getRedisKey(msisdn, currentActivityId);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            LOGGER.info("Redis Key: " + redisKey + " exists in retrieve-coffee-order-delegate");
            runtimeService.deleteProcessInstance(execution.getProcessInstanceId(), "delay_item_caught");
            return;
        }
        LOGGER.info("Order Coffee Process: " + execution.getCurrentActivityName() + " - " + coffeeOrder);

        //get next activity id
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(execution.getProcessDefinitionId())
                .singleResult();
        LOGGER.info("Next activity id: " + getNextActivityId(processDefinition, currentActivityId));

        //end current process instance
        runtimeService.deleteProcessInstance(execution.getProcessInstanceId(), "delay_item_caught");

        HashMap<String, Long> nextActivityMap = new HashMap<>();
        nextActivityMap.put(getNextActivityId(processDefinition, currentActivityId), System.currentTimeMillis());

        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(nextActivityMap));
        LOGGER.info("Key " + redisKey + " is set" + " at " + LocalDateTime.now());


    }

    private String getNextActivityId(ProcessDefinition processDefinition, String currentActivityId) {
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinition.getId());
        FlowNode currentFlowNode = bpmnModelInstance.getModelElementById(currentActivityId);
        return currentFlowNode.getOutgoing().iterator().next().getTarget().getId();
    }

}
