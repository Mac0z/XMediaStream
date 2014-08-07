package org.themonkeysdidit.io;

public class SimpleMessageObjectStringItem {
    public SimpleMessageObjectStringItem(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    private String name;
    private String value;
}
