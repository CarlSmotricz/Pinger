package com.smotricz.pinger;

public class PingResponse {

    public final long timestamp;
    public final int duration;

    public PingResponse(long timestamp, int duration) {
        this.timestamp = timestamp;
        this.duration = duration;
    }

}
