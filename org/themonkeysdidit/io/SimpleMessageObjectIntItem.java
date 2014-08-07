package org.themonkeysdidit.io;

public class SimpleMessageObjectIntItem {
    public SimpleMessageObjectIntItem(String name, int value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public int getValue() {
        return value;
    }
    
    private String name;
    private int value;

}
