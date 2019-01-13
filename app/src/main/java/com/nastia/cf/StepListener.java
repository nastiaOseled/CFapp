package com.nastia.cf;

// Will listen to step alerts
public interface StepListener {

    public void step(long timeNs);

}