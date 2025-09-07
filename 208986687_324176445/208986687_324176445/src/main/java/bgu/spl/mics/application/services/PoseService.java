package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    // Original: private final GPSIMU gpsimu;
    private final GPSIMU sensorModule;
    
    // Original: Pose currentPose;
    private Pose latestPose;

    // Constructor for PoseService.
    public PoseService(GPSIMU gpsimu) {
        super("GPSIMU-MicroService");
        // Original: this.gpsimu = gpsimu;
        this.sensorModule = gpsimu;
    }

    
     //Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {

            sensorModule.setCurrentTick(tick.getTick());
            latestPose = sensorModule.getLastPose();
            
            if (latestPose != null){
                sendEvent(new PoseEvent(latestPose, tick.getTick()));
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> terminate());
        subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast -> terminate());
    }
}
