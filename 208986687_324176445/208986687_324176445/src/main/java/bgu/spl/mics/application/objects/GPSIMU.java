package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents the robot's GPS and IMU.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private final List<Pose> poseList;

    public enum STATUS {
        UP,
        DOWN,
        ERROR
    }

    public GPSIMU() {
        this.currentTick = 0;
        this.status = STATUS.UP;
        this.poseList = new ArrayList<>();
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<Pose> getPoseList() {
        return poseList;
    }

    public Pose getLastPose() {
        return poseList.get(poseList.size() - 1);
    }

    @Override
    public String toString() {
        return "GPSIMU{" +
                "currentTick=" + currentTick +
                ", status=" + status +
                ", poseList=" + poseList +
                '}';
    }

     // Reads pose data from a JSON file and populates the pose list.
    
    public void readFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Pose>>() {}.getType();
            List<Pose> poses = gson.fromJson(reader, listType);
            synchronized (poseList) {
                poseList.clear();
                poseList.addAll(poses);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
