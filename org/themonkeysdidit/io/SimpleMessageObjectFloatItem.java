package org.themonkeysdidit.io;

public class SimpleMessageObjectFloatItem {
    public SimpleMessageObjectFloatItem(String name, float value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public float getValue() {
        return value;
    }
    
    private String name;
    private float value;
}
