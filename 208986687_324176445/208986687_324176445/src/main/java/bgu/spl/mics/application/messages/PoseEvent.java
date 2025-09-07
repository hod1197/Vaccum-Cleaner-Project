package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseEvent represents an event where the robot's current pose is sent
 * by the PoseService to the Fusion-SLAM for further calculations.
 */
public class PoseEvent implements Event<Void> {

    private final Pose pose;
    private final int timestamp;

    /**
     * Constructor to initialize the event with the robot's pose and timestamp.
     *
     * @param pose      The robot's current pose.
     * @param timestamp The time when the pose was recorded.
     */
    public PoseEvent(Pose pose, int timestamp) {
        this.pose = pose;
        this.timestamp = timestamp;
    }

    /**
     * Gets the robot's current pose.
     *
     * @return The current pose.
     */
    public Pose getPose() {
        return pose;
    }

    /**
     * Gets the timestamp of the pose.
     *
     * @return The timestamp.
     */
    public int getTimestamp() {
        return timestamp;
    }
}
