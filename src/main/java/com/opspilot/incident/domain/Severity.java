package com.opspilot.incident.domain;
public enum Severity { INFO, WARNING, HIGH, CRITICAL;
    public static Severity highest(Severity left, Severity right) { return left.ordinal() >= right.ordinal() ? left : right; }
}
