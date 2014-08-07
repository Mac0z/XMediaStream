/*
 ******************************************************************************
 *
 * Copyright 2009 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */

package org.themonkeysdidit.util;


/**
******************************************************************************
**
** A SimpleQueue is very simple implementation of a standard queue. New items
** are added to the queue with a call to add(). A item is removed from the
** queue with a call to get(). The SimpleQueue should be thread safe although
** once an item has been read, it is removed from the queue so multiple readers
** will not be able to receive the same queue items.
** <br>
** The SimpleQueue uses as an array as it's underlying data structure (with a
** length of 32 by default). This array will grow automatically should the queue
** become full. It will not shrink back in size oce it is emptied.
**
** @author Oliver Wardell
** @version 1.0
**
******************************************************************************
*/
public class SimpleQueue {
    
    /**
     * Creates a new SimpleQueue of the default size (32)
     */
    public SimpleQueue() {
        initQueue(DEFAULT_SIZE);
        WRITE_PTR = 0;
        READ_PTR = 0;
        LAST_READ_TIME = now();
        LAST_WRITE_TIME = now();
    }
    
    /**
     * Creates a new queue capable of holding <i>size<i> number of elements.
     * 
     * @param size The size of the queue to create.
     */
    public SimpleQueue(int size) {
        initQueue(size);
        WRITE_PTR = 0;
        READ_PTR = 0;
    }
  
    /**
     * Retreive the next item from the queue. If the queue is currently locked
     * (because it is being written to or it is re-sizing), a cal to get() will
     * block until the queue becomes unlocked.
     *
     * @return The next item in the queue. This will need to be cast to a suitable
     *         object type.
     */
    public Object get() {
        while(isLocked() ) {
            try {
                Thread.sleep(10);
            }
            catch(InterruptedException ie) {
                // do nothing
            }
        }
        
        Object retVal = getHead();
        LAST_READ_TIME = now();
        return retVal;
    }
    
    /**
     * Add a new item to the queue. If the queue is currently locked
     * (because it is being written to or it is re-sizing), a cal to get() will
     * block until the queue becomes unlocked.
     *
     * @param o The object to add the queue.
     */
    public void add(Object o) {
        Logger.log("DEBUG", "Data to add to queue: " + o.toString());
        while(isLocked()) {
            try {
                Thread.sleep(10);
            }
            catch(InterruptedException ie) {
                // do nothing
            }
        }
        addToTail(o);
        LAST_WRITE_TIME = now();
    }
    
    /**
     * Get the time (millisconds since midnight, January 1st, 1970) that an object
     * was last removed (read) from the queue.
     *
     * @return The number of milliseconds since midnight, January 1st, 1970 that
     *         the queue was last read.
     */
    public long getLastReadTime() {
        return LAST_READ_TIME;
    }
    
    /**
     * Get the time (millisconds since midnight, January 1st, 1970) that an object
     * was last added (written) to the queue.
     *
     * @return The number of milliseconds since midnight, January 1st, 1970 that
     *         the queue was last added to..
     */
    public long getLastWriteTime() {
        return LAST_WRITE_TIME;
    }
    
    private void addToTail(Object o) {
        
        if(needToGrow()) {
            growQueue();
        }
        
        Logger.log("DEBUG", "Adding new object to Queue: " + o.toString());
        lockQueue();
        QUEUE[WRITE_PTR] = o;
        WRITE_PTR++;
        
        if(WRITE_PTR == QUEUE.length) {
            WRITE_PTR = 0;
        }
        unlockQueue();
    }
    
    private boolean needToGrow() {
        // We need to grow the queue if WRITE_PTR is not pointing
        // at a null position
        if(QUEUE[WRITE_PTR] != null) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private void growQueue() {
        
        // Grow the queue by 50%
        int newSize = (int)(QUEUE.length * 1.5f);
        
        Logger.log("INFO", "Queue is full, growing to new size: " + Integer.toString(newSize));
        
        Object[] newQueue = new Object[newSize];
        lockQueue();
        copyQueue(newQueue);
        unlockQueue();
    }
    
    private void lockQueue() {
        Logger.log("DEBUG", "Locking queue");
        QUEUE_LOCKED = true;
    }
    
    private void unlockQueue() {
        Logger.log("DEBUG", "Unlocking queue");
        QUEUE_LOCKED = false;
    }
    
    private boolean isLocked() {
        return QUEUE_LOCKED;
    }
    
    private void copyQueue(Object[] newQueue) {

        int count = 0;
        for(int i = 0 ; i < QUEUE.length ; i++) {
            Logger.log("DEBUG", "Queue copy: position: " + Integer.toString(READ_PTR) + " to " + Integer.toString(i));
            newQueue[i] = QUEUE[READ_PTR];
            READ_PTR++;
            if(READ_PTR == QUEUE.length) {
                READ_PTR = 0;
            }
            count = i;
        }
        
        // Queue is coppied, now just need to sort out
        // WRITE_PTR and READ_PTR;
        READ_PTR = 0;
        WRITE_PTR = count+1;
        QUEUE = newQueue;
        Logger.log("DEBUG", "Following queue copy, READ_PTR=" + Integer.toString(READ_PTR) + " WRITE_PTR=" + Integer.toString(WRITE_PTR));
    }
    
    private Object getHead() {
        while(QUEUE[READ_PTR] == null) {
            // Nothing in the queue, lets wait until something is added
            try {
            Thread.sleep(10);
            }
            catch(InterruptedException ie) {
                // do nothing
            }
        }
        lockQueue();
        Object retVal = QUEUE[READ_PTR];
        
        QUEUE[READ_PTR] = null;
        READ_PTR++;
        if(READ_PTR == QUEUE.length) {
            READ_PTR = 0;
        }
        unlockQueue();
        return retVal;
    }
    
    private void initQueue(int size) {
        Logger.log("INFO", "Creating new queue of size: " + Integer.toString(size));
        QUEUE = new Object[size];
        QUEUE_LOCKED = false;
    }
    
    private long now() {
        return System.currentTimeMillis();
    }
    
    private Object[] QUEUE;
    private final int DEFAULT_SIZE = 32;
    private boolean QUEUE_LOCKED;
    private int WRITE_PTR;
    private int READ_PTR;
    private long LAST_READ_TIME;
    private long LAST_WRITE_TIME;
}
