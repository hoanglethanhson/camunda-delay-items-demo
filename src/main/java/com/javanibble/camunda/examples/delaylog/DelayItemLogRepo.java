package com.javanibble.camunda.examples.delaylog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DelayItemLogRepo extends JpaRepository<DelayItemLog, String> {
}
