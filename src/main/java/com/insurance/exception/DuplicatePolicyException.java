package com.insurance.exception;

public class DuplicatePolicyException extends DeskException {
    public DuplicatePolicyException(String policyNo) {
        super("Policy already exists: " + policyNo);
    }
}
