package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents objects the camera detected with a timestamp.
 */
public class StampedDetectedObjects {
    private final int time;
    private final List<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    @Override
    public String toString() {
        return "StampedDetectedObjects{" +
                "time=" + time +
                ", detectedObjects=" + detectedObjects +
                '}';
    }
}
