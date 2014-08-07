package org.themonkeysdidit.io;

public class SimpleMessageObjectLongItem {
    public SimpleMessageObjectLongItem(String name, long value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public long getValue() {
        return value;
    }
    
    private String name;
    private long value;

}
