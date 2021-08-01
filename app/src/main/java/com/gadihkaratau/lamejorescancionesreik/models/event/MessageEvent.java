package com.gadihkaratau.lamejorescancionesreik.models.event;

public class MessageEvent {

    private String name;
    private String message;
    private boolean isState;
    private int mode;

    public MessageEvent(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public MessageEvent(String name, boolean isState) {
        this.name = name;
        this.isState = isState;
    }

    public MessageEvent(String name, int mode) {
        this.name = name;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isState() {
        return isState;
    }

    public void setState(boolean state) {
        isState = state;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
