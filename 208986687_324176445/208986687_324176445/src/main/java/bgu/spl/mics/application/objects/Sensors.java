package bgu.spl.mics.application.objects;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// this class represents sensor monitor, and keeps track of all active sensors. used for stopping when all sensors are finished.
public class Sensors {
    private final Set<String> activeSensors = ConcurrentHashMap.newKeySet();

    // Singleton instance holder
    private static class SensorsHolder {  
        private static final Sensors instance = new Sensors();
    }

    // Private constructor to prevent external instantiation
    private Sensors() {}

    // Retrieves the singleton instance of SensorMonitor
    public static Sensors getInstance() {
        return SensorsHolder.instance;
    }

    //Adds a sensor to the active sensors set.
    public synchronized void addSensor(String sensorId) {
        activeSensors.add(sensorId);
    }

    // Removes a sensor from the active sensors set.
    public synchronized void removeSensor(String sensorId) {
        activeSensors.remove(sensorId);
    }

    // Checks if all sensors have finished their operations.
    public synchronized boolean areAllSensorsFinished() {
        return activeSensors.isEmpty();
    }
}
