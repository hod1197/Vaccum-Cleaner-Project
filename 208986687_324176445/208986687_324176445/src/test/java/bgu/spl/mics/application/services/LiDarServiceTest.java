package bgu.spl.mics.application.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.LiDarWorkerTracker;

class LiDarServiceTest {

    @Test
    void testLiDarServiceInitialization() {
        // יצירת LiDarWorkerTracker עם מזהה ותדר
        LiDarWorkerTracker workerTracker = new LiDarWorkerTracker(1, 10);

        // יצירת LiDarService עם LiDarWorkerTracker
        LiDarService liDarService = new LiDarService(workerTracker);

        // בדיקת התקנת השירות
        assertNotNull(liDarService, "LiDarService should be initialized successfully.");
    }
}
