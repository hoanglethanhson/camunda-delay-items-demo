package com.javanibble.camunda.examples.schedule;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanibble.camunda.examples.constant.Constant;
import com.javanibble.camunda.examples.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@Slf4j
public class DelayItemsHandlingSchedule {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${app.delay-items.delay}")
    private Long delayDuration;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedRate = 1000L)
    public void handleDelayItems() {
        Objects.requireNonNull(redisTemplate.keys(Constant.DELAY_ITEM_KEY_PREFIX + ":*")).forEach(key -> {
            String msisdn = StringUtils.getMsisdn(key);
            String execution;
            try {
                execution = getExecution(key);
            } catch (JsonProcessingException e) {
                log.error("Error while getting execution for msisdn: {}", msisdn, e);
                throw new RuntimeException(e);
            }
            if (!Constant.DELAY_ITEMS_NOT_READY_STATUS.equals(execution)) {
                log.info("Current time: {}, {}", System.currentTimeMillis(), LocalDateTime.now());
                log.info("Item value {} is ready for msisdn: {}", redisTemplate.opsForValue().get(key), msisdn);
                executeRemainingItems(msisdn, execution);
                redisTemplate.delete(key);
            }
        });
    }

    /**
     * Get the next activity id from the key
     * @param key
     * @return next activity id if the delay time is passed, else return "NOTE_READY"
     */
    private String getExecution(String key) throws JsonProcessingException {
        String nextActivityJson = (String) redisTemplate.opsForValue().get(key);
        HashMap<String, Long> nextActivityMap = objectMapper.readValue(nextActivityJson, HashMap.class);
        String nextActivityId = nextActivityMap.keySet().iterator().next();
        Long delayTimestamp = nextActivityMap.get(nextActivityId);
        if (delayTimestamp == null) {
            log.info("delayTimestamp is null for key: {}", key);
            return nextActivityId;
        }
        return (delayTimestamp + delayDuration <= System.currentTimeMillis()) ? nextActivityId
                : Constant.DELAY_ITEMS_NOT_READY_STATUS;
    }

    //TODO: input variables through map
    private void executeRemainingItems(String msisdn, String nextActivityId) {
        log.info("handling delay item for {}", msisdn);
        runtimeService.createProcessInstanceByKey("order-coffee-subprocess")
                .startBeforeActivity(nextActivityId)
                .setVariable("order", "Latte")
                .setVariable("msisdn", msisdn)
                .execute();
    }

}
