package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.Sensors;

import java.util.ArrayList;
import java.util.List;


/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    private final Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) { 
        super("CameraService-" + camera.getId());
        this.camera = camera;
        Sensors.getInstance().addSensor("Camera " + camera.getId());
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            handleObjectDetections(tick.getTick());
            if (camera.getStatus() == STATUS.UP) {
                List<StampedDetectedObjects> stampedObjectsList = camera.getcurrentDetectedObjects();
                List<DetectedObject> detectedObjectsToSend = new ArrayList<>();

                // Iterate through each stamped object to check if it's time to send
                for (StampedDetectedObjects stampedObject : stampedObjectsList) {
                    // Check if the time of the object is exactly tick - frequency
                    if (stampedObject.getTime() == tick.getTick() - camera.getFrequency()) {
                        detectedObjectsToSend.addAll(stampedObject.getDetectedObjects());
                    }
                }

                // If there are any detected objects, send them in a new DetectObjectsEvent
                if (!detectedObjectsToSend.isEmpty()) {
                    StatisticalFolder.getInstance().incrementDetectedObjects(detectedObjectsToSend.size());
                    sendEvent(new DetectObjectsEvent(detectedObjectsToSend, tick.getTick()));
                }
            }
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate(); 
        });
        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
                terminate();
        });
        
    }

    private void handleObjectDetections(int tick) {
        String errorMSG = camera.verifyError(tick);
        if (errorMSG != null) {
            camera.setStatus(STATUS.ERROR);          
            StatisticalFolder.getInstance().setError(true);
            StatisticalFolder.getInstance().setErrorDescription(errorMSG);
            StatisticalFolder.getInstance().setFaultySensor("Camera");
            sendBroadcast(new CrashedBroadcast(errorMSG));
        }
        else {
            ArrayList<DetectedObject> detections = camera.identifyObjects(tick);
            if (detections.size() > 0) {
                sendEvent(new DetectObjectsEvent(detections, tick - camera.getFrequency()));
                StatisticalFolder.getInstance().incrementDetectedObjects(detections.size());
            } 
        }
    }
}
