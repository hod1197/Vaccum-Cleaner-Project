package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast represents a timing message sent by the TimeService.
 * It is used to synchronize the system components and track the current tick.
 */
public class TickBroadcast implements Broadcast {

    private final int tick;

    public TickBroadcast(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }
}
