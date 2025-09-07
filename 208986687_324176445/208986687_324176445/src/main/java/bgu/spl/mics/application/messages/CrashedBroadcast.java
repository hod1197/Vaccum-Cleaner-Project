package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * CrashedBroadcast is used to notify other services that the broadcasting service
 * has encountered a crash.
 */
public class CrashedBroadcast implements Broadcast {

    private final String serviceName;


    public CrashedBroadcast(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
