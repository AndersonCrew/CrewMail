package com.dazone.crewemail.event;

/**
 * Created by tunglam on 12/14/16.
 */

public class PinEvent {
    private String pin;
    private String title;

    public PinEvent(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
