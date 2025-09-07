package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;


/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {

    private final List<StampedCloudPoints> cloudPoints;
    private static LiDarDataBase instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private LiDarDataBase() {
        cloudPoints = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @return The singleton instance of LiDarDataBase.
     */
    public static synchronized LiDarDataBase getInstance() {
        if (instance == null) {
            instance = new LiDarDataBase();
        }
        return instance;
    }

    /**
     * Adds a new cloud point entry to the database.
     *
     * @param point The StampedCloudPoints object to add.
     */
    public synchronized void addCloudPoint(StampedCloudPoints point) {
        cloudPoints.add(point);
    }

    /**
     * Retrieves all cloud points in the database.
     *
     * @return A list of all StampedCloudPoints.
     */
    public synchronized List<StampedCloudPoints> getCloudPoints() {
        return new ArrayList<>(cloudPoints);
    }

    /**
     * Clears all data in the cloudPoints list.
     */
    public synchronized void clear() {
        cloudPoints.clear();
    }

    @Override
    public synchronized String toString() {
        return "LiDarDataBase{" +
                "cloudPoints=" + cloudPoints +
                '}';
    }

    public void readFromJson(String filePath) { 
        try (FileReader fileReader = new FileReader(filePath)) { 
            Gson gson = new Gson();
            Type cloudDataType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            List<StampedCloudPoints> cloudData = gson.fromJson(fileReader, cloudDataType);
            synchronized (this) {
                cloudPoints.addAll(cloudData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}