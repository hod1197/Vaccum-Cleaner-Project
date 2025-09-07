package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

/**
 * TrackedObjectsEvent represents an event where a list of objects tracked by a LiDar worker
 * is sent to the Fusion-SLAM for further processing.
 */
public class TrackedObjectsEvent implements Event<Void> {

    private final List<TrackedObject> trackedObjects;
    private final int timestamp;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjects, int timestamp) {

        this.trackedObjects = trackedObjects;
        this.timestamp = timestamp;
        
    }

    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    public int getTimestamp() {
        return timestamp;
    }

}
