package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency; 
    private STATUS status;
    String description;
    private ArrayList<StampedDetectedObjects> recentlyDetectedObjects; 
    private ArrayList<StampedDetectedObjects> currentDetectedObjects; 
    private ArrayList<StampedDetectedObjects> objectRepository;       

    public Camera(int freq, int id, String description, String jsonFilePath) {
        this.frequency = freq;
        this.id = id;
        this.description = description;
        this.recentlyDetectedObjects = new ArrayList<>();
        this.currentDetectedObjects = new ArrayList<>();
        this.objectRepository = new ArrayList<>();
        this.status = STATUS.UP;
        importDetectedObjectsFromJson(jsonFilePath);
    }


    public ArrayList<DetectedObject> identifyObjects(int tick) {
        ArrayList<DetectedObject> currDetectedObjects = new ArrayList<>();
        if(status == STATUS.UP) {
            boolean found = false;
            for (int i = 0; i < objectRepository.size() && !found; i++) { 
                if (objectRepository.get(i).getTime() == tick - frequency) { 
                    currDetectedObjects.addAll(objectRepository.get(i).getDetectedObjects()); 
                    currentDetectedObjects.add(objectRepository.get(i)); 
                    recentlyDetectedObjects.clear(); 
                    recentlyDetectedObjects.add(objectRepository.get(i)); 
                    objectRepository.remove(i); 
                    found = true;
                }
            }
        }
        return currDetectedObjects;
    }

    public void importDetectedObjectsFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            JsonObject rootObject = gson.fromJson(reader, JsonObject.class);
            JsonArray cameraObjectsArray = rootObject.getAsJsonArray("camera" + id);
            Type listType = new TypeToken<List<StampedDetectedObjects>>() {}.getType();
            List<StampedDetectedObjects> stampedObjects = gson.fromJson(cameraObjectsArray, listType);
            objectRepository.clear(); 
            objectRepository.addAll(stampedObjects); 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerDetectedObject(StampedDetectedObjects stampedObject) {
        this.currentDetectedObjects.add(stampedObject); 
    }


    public String verifyError(int tick) {
        for (StampedDetectedObjects stampedObject : objectRepository) { 
            if (stampedObject.getTime() == tick) {
                for(DetectedObject object: stampedObject.getDetectedObjects()) {
                    if (object.getId().equals("ERROR")) {
                        return object.getDescription();
                    }
                }
                return null;
            }
        }
        return null;
    }

    public int getPendingObjects() {
        return objectRepository.size(); 
    }

    // גטרים וסטרים נשארו ללא שינוי
    public int getId() {
        return id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public ArrayList<StampedDetectedObjects> getcurrentDetectedObjects() {
        return currentDetectedObjects; 
    }

    public ArrayList<StampedDetectedObjects> getRecentlyDetectedObjects() { 
        return recentlyDetectedObjects;
    }

    public int getFrequency() {
        return frequency;
    }
}
