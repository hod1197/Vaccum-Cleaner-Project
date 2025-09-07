package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.Sensors;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.LiDarWorkerTracker.STATUS;


/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker tracker;
    private final LiDarDataBase liDarDataBase;
    private final List<DetectObjectsEvent> pending;
     private final StatisticalFolder statisticalFolder;
     private int currentTick;
    
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker tracker) {

        super("LiDarService-" + tracker.getId());
        this.tracker = tracker;
        this.liDarDataBase = LiDarDataBase.getInstance();
        this.statisticalFolder = StatisticalFolder.getInstance();
        this.pending = new ArrayList<>();
        this.currentTick = 0;
        Sensors.getInstance().addSensor("LiDarWorkerTracker " + tracker.getId());
    }
    

    // Initializes the LiDarService.
    @Override
    protected void initialize() {

        super.subscribeEvent(DetectObjectsEvent.class, obj -> {
            this.pending.add(obj);
        });

        super.subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick += 1;

            if(tracker.getStatus() == STATUS.ERROR ) {
                StatisticalFolder.getInstance().setError(true);
                StatisticalFolder.getInstance().setErrorDescription("Lidar " + tracker.getId() + " Disconnected");
                StatisticalFolder.getInstance().setFaultySensor("LidarWorkerTracker");
                sendBroadcast(new CrashedBroadcast("Lidar " + tracker.getId() + " Disconnected"));
            }

            else if (tracker.getStatus() == STATUS.DOWN) {
                Sensors.getInstance().removeSensor("LiDarWorkerTracker " + tracker.getId());
                terminate();
            }

            else{
                List<TrackedObject> trackedObjects = new LinkedList<>();
                for (int i = 0; i < this.pending.size(); i++) {
                    DetectObjectsEvent temp = this.pending.get(i);
                    if (temp.getTimestamp() + this.tracker.getFrequency() == tick.getTick()) {
                        this.pending.remove(i);
                        i--;
                        for (DetectedObject detectObject : temp.getDetectedObject()) {
                            TrackedObject trackedObject = this.tracker.trackObject(detectObject, temp.getTimestamp(),liDarDataBase);
                            if (trackedObject != null) {
                                trackedObjects.add(trackedObject);
                            }
                        }
                    }
                }

                if (trackedObjects.size() > 0) {
                    statisticalFolder.incrementTrackedObjects(trackedObjects.size());
                    super.sendEvent(new TrackedObjectsEvent(trackedObjects, tick.getTick()));
                    
                }
            }

        });

        subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast -> {
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast -> {
            terminate();
        });

    }

}
