package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

/**
 * DetectObjectsEvent represents an event where objects detected by a camera
 * at a specific time are sent for further processing by a LiDar worker.
 */
public class DetectObjectsEvent implements Event<Boolean> {

    private final List<DetectedObject> detectedObjects;
    private final int timestamp;

    public DetectObjectsEvent(List<DetectedObject> detectedObjects, int timestamp) {
        this.detectedObjects = detectedObjects;
        this.timestamp = timestamp;
    }

    public List<DetectedObject> getDetectedObject() {
        return detectedObjects;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
