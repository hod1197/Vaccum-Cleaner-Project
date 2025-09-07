package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) at a specific timestamp.
 */
public class Pose {
    private final double x;
    private final double y;
    private final double yaw; // Orientation angle in degrees
    private final int timestamp;

    public Pose(double x, double y, double yaw, int timestamp) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.timestamp = timestamp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getYaw() {
        return yaw;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Pose{" +
                "x=" + x +
                ", y=" + y +
                ", yaw=" + yaw +
                ", timestamp=" + timestamp +
                '}';
    }
}
