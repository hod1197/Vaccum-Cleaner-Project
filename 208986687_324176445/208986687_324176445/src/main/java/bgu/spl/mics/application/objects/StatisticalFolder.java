package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 * Singleton pattern ensures only one instance exists.
 */
public class StatisticalFolder {

    private static class StatisticalFolderHolder {
        private static final StatisticalFolder statisticalFolder = new StatisticalFolder();
    }

    private final AtomicInteger systemRuntime;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;
    private final AtomicBoolean error;
    private volatile String errorDescription; // Volatile as it is written and read directly
    private volatile String faultySensor;

    private StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
        this.error = new AtomicBoolean(false);
    }

    public void changeTickTime(int ticks) {
        systemRuntime.set(ticks);
    }

    public void incrementDetectedObjects(int count) {
        numDetectedObjects.addAndGet(count);
    }

    public void incrementTrackedObjects(int count) {
        numTrackedObjects.addAndGet(count);
    }

    public void incrementLandmarks(int count) {
        numLandmarks.addAndGet(count);
    }

    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.statisticalFolder;
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void setError(boolean isError) {
        error.set(isError);
    }

    public boolean isError() {
        return error.get();
    }

    public synchronized void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public synchronized String getErrorDescription() {
        return errorDescription;
    }

    public synchronized void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public synchronized String getFaultySensor() {
        return faultySensor;
    }
}