package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Represents the object that was tracked by the LiDAR.
 */
public class TrackedObject {
    private final String id;
    private final int time;
    private final String description;
    private final ArrayList<CloudPoint> coordinates;

    public TrackedObject(String id, int time, String description, ArrayList<CloudPoint>  coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<CloudPoint> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "TrackedObject{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
