package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LiDAR worker tracker that tracks objects.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<TrackedObject> lastTrackedObjects;

    public enum STATUS {
        UP,
        DOWN,
        ERROR
    }

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.lastTrackedObjects = new ArrayList<>();
        this.status = STATUS.UP;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public TrackedObject trackObject(DetectedObject detectedObject, int tick, LiDarDataBase liDarDataBase ) {
        List<StampedCloudPoints> cloudPoints = liDarDataBase.getCloudPoints();
        for (StampedCloudPoints stampedCloudPoints : cloudPoints) {
            if (stampedCloudPoints.getTime() == tick) {
                 ArrayList<CloudPoint> arr = new ArrayList<CloudPoint>();
                for (int i = 0; i < arr.size(); i++) {
                    arr.add(stampedCloudPoints.getCloudPoints().get(i));
                }
                TrackedObject trackedObject = new TrackedObject(
                    detectedObject.getId(), 
                    tick, 
                    detectedObject.getDescription(), 
                    arr
                );
                this.lastTrackedObjects.add(trackedObject);
                return trackedObject;
            }
        }

        return null;
    }
    
    @Override
    public String toString() {
        return "LiDarWorkerTracker{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", status=" + status +
                ", lastTrackedObjects=" + lastTrackedObjects +
                '}';
    }
}

