package org.themonkeysdidit.io;

public class SimpleMessageObjectDoubleItem {
    public SimpleMessageObjectDoubleItem(String name, double value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public double getValue() {
        return value;
    }
    
    private String name;
    private double value;
}
