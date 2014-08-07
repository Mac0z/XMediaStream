package org.themonkeysdidit.io;

public class SimpleMessageObjectByteArrayItem {
    public SimpleMessageObjectByteArrayItem(String name, byte[] value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public byte[] getValue() {
        return value;
    }
    
    private String name;
    private byte[] value;
}
