package com.insurance.exception;

public class PolicyNotFoundException extends DeskException {
    public PolicyNotFoundException(String policyNo) {
        super("Policy not found: " + policyNo);
    }
}
