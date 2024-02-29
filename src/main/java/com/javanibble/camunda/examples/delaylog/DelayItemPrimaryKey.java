package com.javanibble.camunda.examples.delaylog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DelayItemPrimaryKey implements Serializable {
    private String msisdn;
    private String activityKey;
    // standard constructors, getters, and setters
    // hashcode and equals implementation
}
