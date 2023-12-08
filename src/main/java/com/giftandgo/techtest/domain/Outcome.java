package com.giftandgo.techtest.domain;

public class Outcome {

    private final String name;
    private final String transport;
    private final Float topSpeed;

    public Outcome(String name, String transport, Float topSpeed) {
        this.name = name;
        this.transport = transport;
        this.topSpeed = topSpeed;
    }

    public String getName() {
        return name;
    }

    public String getTransport() {
        return transport;
    }

    public Float getTopSpeed() {
        return topSpeed;
    }
}
