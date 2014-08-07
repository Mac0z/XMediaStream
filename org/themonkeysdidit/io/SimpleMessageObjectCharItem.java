package org.themonkeysdidit.io;

public class SimpleMessageObjectCharItem {
    public SimpleMessageObjectCharItem(String name, char value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public char getValue() {
        return value;
    }
    
    private String name;
    private char value;
}
