package bgu.spl.mics.application.objects;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
// Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
// Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
public class FusionSlam {
    
    private final List<LandMark> landMarks;
    private final List<Pose> poses;      
    private final List<TrackedObject> pendingTrackedObjects; 
    public boolean pendingObjectsUpdated;

    // Singleton instance holder
    private static class FusionSlamHolder { 
        private static final FusionSlam instance = new FusionSlam();
    }

    // Private constructor to prevent external instantiation
    private FusionSlam() {
        landMarks = new ArrayList<>(); 
        poses = new ArrayList<>(); 
        pendingTrackedObjects = new ArrayList<>();
    }

     // Retrieves the singleton instance of FusionSlam.
    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    // Processes a tracked object by updating landmarks based on the current pose.
    // If the corresponding pose isn't available, the object is added to the pending list.
    public synchronized void handleTrackedObject(TrackedObject object) {
    Pose currentPose = getPoseByTimestamp(object.getTime());
    if (currentPose == null) {
        pendingTrackedObjects.add(object);
        pendingObjectsUpdated = true;  // עדכון הדגל כאשר אובייקט נוסף ל-pendingTrackedObjects
    } else {
        LandMark existingMark = findLandMarkById(object.getId());
        if (existingMark == null) {
            LandMark newMark = new LandMark(object.getId(), object.getDescription(), applyTransformation(object.getCoordinates(), currentPose));
            landMarks.add(newMark);
            StatisticalFolder.getInstance().incrementLandmarks(1);
        } else {
            List<CloudPoint> averagedCoords = averageCoordinates(existingMark.getCoordinates(), applyTransformation(object.getCoordinates(), currentPose));
            existingMark.setCoordinates(averagedCoords);
        }
    }
}


    // Adds a new pose to the poses list.
    public synchronized void addPose(Pose pose) {
        poses.add(pose);
    }

    // Retrieves the most recent pose.
    public synchronized Pose getLatestPose() {
        if (poses.isEmpty()) {
            return null;
        }
        return poses.get(poses.size() - 1);
    }

     // Retrieves the list of objects pending tracking.
     public List<TrackedObject> retrievePendingObjects() {
        return pendingTrackedObjects;
    }

    // Provides a copy of all poses.
    public synchronized List<Pose> getAllPoses() {
        return new ArrayList<>(poses);
    }

    // Handles periodic updates, processing any pending tracked objects if their corresponding poses are available.
    
public synchronized void processPendingObjects() {
    if(!pendingTrackedObjects.isEmpty()) {
        Iterator<TrackedObject> iterator = pendingTrackedObjects.iterator();
        while (iterator.hasNext()) {
            TrackedObject obj = iterator.next();
            if (getPoseByTimestamp(obj.getTime()) != null) {
                handleTrackedObject(obj);
                iterator.remove(); // הסרת האובייקט בצורה בטוחה
                pendingObjectsUpdated = false;  // Reset the flag after processing
            }
        }
    }
}

    // Searches for a landmark by its unique identifier.
    private LandMark findLandMarkById(String id) {
        for (LandMark mark : landMarks) {
            if (mark.getId().equals(id)) {
                return mark;
            }
        }
        return null;
    }

    // Applies a transformation to a list of cloud points based on the given pose.
    private List<CloudPoint> applyTransformation(List<CloudPoint> coordinates, Pose pose) {
        List<CloudPoint> transformedPoints = new ArrayList<>();
        double yawRadians = Math.toRadians(pose.getYaw());
        for (CloudPoint point : coordinates) {
            // 2D rotation matrix formula
            double transformedY = pose.getY() + (point.getX() * Math.sin(yawRadians)) + (point.getY() * Math.cos(yawRadians));
            double transformedX = pose.getX() + (point.getX() * Math.cos(yawRadians)) - (point.getY() * Math.sin(yawRadians));
            transformedPoints.add(new CloudPoint(transformedX, transformedY));
        }
        return transformedPoints;
    }

     // Retrieves a pose based on the provided timestamp.
     private Pose getPoseByTimestamp(int timestamp) {
        for (Pose pose : poses) {
            if (pose.getTimestamp() == timestamp) {
                return pose;
            }
        }
        return null;
    }

    // Computes the average of two lists of cloud points.
    // If the lists differ in size, they are padded with default points.
    private List<CloudPoint> averageCoordinates(List<CloudPoint> existingCoords, List<CloudPoint> newCoords) {
        List<CloudPoint> averagedList = new ArrayList<>();
        if(existingCoords.size() != newCoords.size()) {
            padCoordinates(existingCoords, newCoords);
        }
        for (int i = 0; i < existingCoords.size(); i++) {
            CloudPoint point1 = existingCoords.get(i);
            CloudPoint point2 = newCoords.get(i);
            double avgX = (point1.getX() + point2.getX()) / 2;
            double avgY = (point1.getY() + point2.getY()) / 2;
            averagedList.add(new CloudPoint(avgX, avgY));
        }
        return averagedList;
    }
    
    // Pads the shorter of two coordinate lists with default points to match their sizes.
    private void padCoordinates(List<CloudPoint> existingCoords, List<CloudPoint> newCoords) {
        int existingSize = existingCoords.size();
        int newSize = newCoords.size();
        if (existingSize > newSize) {  
            for (int i = 0; i < existingSize - newSize; i++)  {
                newCoords.add(new CloudPoint(0, 0));
            }
        }
        else {
            for (int i = 0; i < newSize - existingSize; i++)  {
                existingCoords.add(new CloudPoint(0, 0));
            }
        }
    }

    // Provides a copy of all landmarks.
    public synchronized List<LandMark> getAllLandMarks() {
        return new ArrayList<>(landMarks);
    }

    // Serializes the current state to a JSON file.
    public synchronized void exportToJson(String outputPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StatisticalFolder stats = StatisticalFolder.getInstance();
        Map<String, Object> jsonOutput = new LinkedHashMap<>();
        jsonOutput.put("systemRuntime", stats.getSystemRuntime());
        jsonOutput.put("numTrackedObjects", stats.getNumTrackedObjects());
        jsonOutput.put("numDetectedObjects", stats.getNumDetectedObjects());
        jsonOutput.put("numLandmarks", stats.getNumLandmarks());
        jsonOutput.put("landMarks", landMarks);

        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(jsonOutput, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Serializes error information and the last frames to a JSON file.
    public synchronized void exportErrorToJson(String outputPath, Map<String, Object> lastFrameData) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StatisticalFolder stats = StatisticalFolder.getInstance();

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("systemRuntime", stats.getSystemRuntime());
        statistics.put("numDetectedObjects", stats.getNumDetectedObjects());
        statistics.put("numTrackedObjects", stats.getNumTrackedObjects());
        statistics.put("numLandmarks", stats.getNumLandmarks());

        Map<String, Object> errorOutput = new LinkedHashMap<>();
        errorOutput.put("error", stats.getErrorDescription());
        errorOutput.put("faultySensor", stats.getFaultySensor());
        errorOutput.put("lastFrames", lastFrameData);
        errorOutput.put("poses", poses);
        errorOutput.put("statistics", statistics);
        errorOutput.put("landMarks", landMarks);

        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(errorOutput, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pose getClosestPose(int timestamp) {
    Pose closestPose = null;
    int minTimeDiff = Integer.MAX_VALUE;
    for (Pose pose : poses) {
        int timeDiff = Math.abs(pose.getTimestamp() - timestamp);
        if (timeDiff < minTimeDiff) {
            minTimeDiff = timeDiff;
            closestPose = pose;
        }
    }
    return closestPose;
}
public void reset() {
    landMarks.clear();
    poses.clear();
    pendingTrackedObjects.clear();
    pendingObjectsUpdated = false;
}



    

}
