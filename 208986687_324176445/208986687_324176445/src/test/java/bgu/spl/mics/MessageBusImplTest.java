package bgu.spl.mics;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

public class MessageBusImplTest {

    @Test
    public void testBasicFunctionality() {
        // יצירת אינסטנס של MessageBusImpl
        MessageBusImpl messageBus = MessageBusImpl.getInstance();

        // יצירת מיקרו-שירות פשוט
        MicroService testService = new MicroService("TestService") {
            @Override
            protected void initialize() {
            }
        };

        // בדיקה שהרשמה לא זורקת חריגה
        assertDoesNotThrow(() -> messageBus.register(testService));

        // בדיקה שהסרת שירות לא זורקת חריגה
        assertDoesNotThrow(() -> messageBus.unregister(testService));
    }

    @Test
    public void testSubscribeEvent() {
    // Preconditions: יצירת MessageBus ומיקרו-שירות
    MessageBusImpl messageBus = MessageBusImpl.getInstance();
    MicroService testService = new MicroService("TestService") {
        @Override
        protected void initialize() {}
    };
    
    // Subscribe to event
    messageBus.register(testService);
    messageBus.subscribeEvent(DetectObjectsEvent.class, testService);
    
    // Post conditions: ה-MessageBus צריך להכיל את המיקרו-שירות ברשימת המנויים לאירוע.
    assertDoesNotThrow(() -> messageBus.subscribeEvent(DetectObjectsEvent.class, testService));
}

@Test
public void testSendEventAndComplete() {
    // Preconditions: יצירת MessageBus, מיקרו-שירות, ושליחת Event
    MessageBusImpl messageBus = MessageBusImpl.getInstance();
    MicroService testService = new MicroService("TestService") {
        @Override
        protected void initialize() {}
    };
    
    messageBus.register(testService);
    DetectObjectsEvent event = new DetectObjectsEvent(new ArrayList<>(), 5);
    messageBus.subscribeEvent(DetectObjectsEvent.class, testService);
    
    // Send event and complete it
    Future<Boolean> future = messageBus.sendEvent(event);
    messageBus.complete(event, true);
    
    // Post conditions: ה-Event הושלם כראוי
    assertTrue(future.isDone());
    assertEquals(true, future.get());
}

@Test
public void testSendBroadcast() {
    // Preconditions: יצירת MessageBus ומיקרו-שירותים
    MessageBusImpl messageBus = MessageBusImpl.getInstance();
    MicroService testService1 = new MicroService("TestService1") {
        @Override
        protected void initialize() {}
    };
    MicroService testService2 = new MicroService("TestService2") {
        @Override
        protected void initialize() {}
    };
    
    messageBus.register(testService1);
    messageBus.register(testService2);
    messageBus.subscribeBroadcast(TickBroadcast.class, testService1);
    messageBus.subscribeBroadcast(TickBroadcast.class, testService2);
    
    // Create and send broadcast
    TickBroadcast broadcast = new TickBroadcast(1);
    messageBus.sendBroadcast(broadcast);
    
    // Post conditions: כל המיקרו-שירותים צריכים לקבל את ה-Broadcast
    assertDoesNotThrow(() -> messageBus.awaitMessage(testService1));
    assertDoesNotThrow(() -> messageBus.awaitMessage(testService2));
}



}
