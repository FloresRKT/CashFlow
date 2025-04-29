package com.CS22S4.hehe.entity;

public class Customer {
    private int requestedAmount;
    private float serviceTime;  // Time taken to serve this customer
    private long timestamp;     // When this customer was served

    public Customer(int requestedAmount) {
        this.requestedAmount = requestedAmount;
        this.timestamp = System.currentTimeMillis();
    }

    public void completeRequest(float serviceTime) {
        this.serviceTime = serviceTime;
    }

    // Getters for statistics
    public int getRequestedAmount() { return requestedAmount; }
    public float getServiceTime() { return serviceTime; }
    public long getTimestamp() { return timestamp; }
}
