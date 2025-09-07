package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.Sensors;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private final int tickTime;
    private final int duration;
    private int tickCounter;
    private final Sensors sensors;
    private final StatisticalFolder statFolder;

    public TimeService(int tickTime, int duration) {
        super("TimeService");
        this.tickTime = tickTime * 1000; // Convert seconds to milliseconds
        this.duration = duration;
        this.tickCounter = 1; // Initialize tick counter
        this.sensors = Sensors.getInstance(); // Initialize SensorMonitor
        this.statFolder = StatisticalFolder.getInstance(); // Initialize StatisticalFolder
    }

    /**
     * Initializes the TimeService.
     * Subscribes to CrashedBroadcast and TickBroadcast,
     * broadcasts TickBroadcast messages at regular intervals,
     * and terminates the simulation based on duration or sensor statuses.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast -> {
            System.out.println("TimeService received CrashedBroadcast: Terminating simulation.");
            statFolder.changeTickTime(tickCounter - 1);
            sendBroadcast(new TerminatedBroadcast());
            terminate();
        });

        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            tickCounter++;

            if (tickCounter <= duration) {
                if (sensors.areAllSensorsFinished()) {
                    System.out.println("All sensors finished. Terminating early.");
                    statFolder.changeTickTime(tickCounter - 1);
                    sendBroadcast(new TerminatedBroadcast());
                    terminate();

                } else {
                    System.out.println("\nTick " + tickCounter);
                    sendBroadcast(new TickBroadcast(tickCounter));

                    try {
                        Thread.sleep(tickTime);
                    } catch (Exception e) {}
                }
            } else {
                System.out.println("Simulation duration reached. Terminating.");
                statFolder.changeTickTime(tickCounter - 1);
                sendBroadcast(new TerminatedBroadcast());
                terminate();
            }
        });

        // Send the initial TickBroadcast
        sendBroadcast(new TickBroadcast(tickCounter));
    }
}
