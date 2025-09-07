package bgu.spl.mics.application.objects;

/**
 * Represents the object that was detected by the camera.
 */
public class DetectedObject {
    private final String id;
    private final String description;

    public DetectedObject(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DetectedObject{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
