package bgu.spl.mics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * The {@link MessageBusImpl} class is the implementation of the MessageBus interface.
 * This implementation uses thread-safe data structures to ensure thread safety.
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static class SingletonHolder {
        private static final MessageBusImpl INSTANCE = new MessageBusImpl();
    }

    private final Map<MicroService, LinkedBlockingQueue<Message>> serviceQueues;
    private final Map<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> eventSubscriptions;
    private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscriptions;
    private final Map<Event<?>, Future<?>> futures;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private MessageBusImpl() {
        serviceQueues = new ConcurrentHashMap<>();
        eventSubscriptions = new ConcurrentHashMap<>();
        broadcastSubscriptions = new ConcurrentHashMap<>();
        futures = new ConcurrentHashMap<>();
    }

    // Retrieves the singleton instance of MessageBusImpl.
    public static MessageBusImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // Registers a MicroService by creating its message queue.
    @Override
    public void register(MicroService m) {
        serviceQueues.computeIfAbsent(m, new Function<MicroService, LinkedBlockingQueue<Message>>() {
            @Override
            public LinkedBlockingQueue<Message> apply(MicroService key) {
                return new LinkedBlockingQueue<>();
            }
        });
    }

    // Subscribes a MicroService to a specific type of Event.
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        eventSubscriptions.computeIfAbsent(type, new Function<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>>() {
            @Override
            public LinkedBlockingQueue<MicroService> apply(Class<? extends Event<?>> key) {
                return new LinkedBlockingQueue<>();
            }
        }).add(m);
    }

    // Subscribes a MicroService to a specific type of Broadcast.
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        broadcastSubscriptions.computeIfAbsent(type, new Function<Class<? extends Broadcast>, List<MicroService>>() {
            @Override
            public List<MicroService> apply(Class<? extends Broadcast> key) {
                return new CopyOnWriteArrayList<>();
            }
        }).add(m);
    }

    // Sends a Broadcast message to all subscribed MicroServices.
    @Override
    public void sendBroadcast(Broadcast b) {
        List<MicroService> subscribers = broadcastSubscriptions.get(b.getClass()); 
        if (subscribers != null) {
            for (MicroService ms : subscribers) {
                LinkedBlockingQueue<Message> queue = serviceQueues.get(ms);
                if (queue != null) {
                    try {
                        queue.put(b); // Blocking add if the queue is full
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); 
                    }
                }
            }
        }
    }

    // Sends an Event message to one of the subscribed MicroServices in a round-robin fashion.
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        LinkedBlockingQueue<MicroService> subscribersQueue = eventSubscriptions.get(e.getClass());
        if (subscribersQueue == null || subscribersQueue.isEmpty()) {
            return null; 
        }
        synchronized (subscribersQueue) {
            MicroService handler = subscribersQueue.poll();
            if (handler != null) {
                LinkedBlockingQueue<Message> queue = serviceQueues.get(handler);
                if (queue != null) {
                    try {
                        queue.put(e); 
                    } catch (InterruptedException err) {
                        Thread.currentThread().interrupt(); 
                    }
                }
                subscribersQueue.add(handler); // Round-robin
            }
            subscribersQueue.notifyAll();
        }
        Future<T> future = new Future<>(); 
        futures.put(e, future);
        return future;
    }

    // Completes an Event by resolving its associated Future with the provided result.
    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = (Future<T>) futures.get(e);
        if (future != null) {
            future.resolve(result);
            futures.remove(e);
        }
    }

    // Unregisters a MicroService by removing its message queue and cleaning references.
    @Override
    public synchronized void unregister(MicroService m) {
        eventSubscriptions.values().forEach(queue -> queue.remove(m));
        broadcastSubscriptions.values().forEach(list -> list.remove(m));
        serviceQueues.remove(m);
    }

    // Retrieves and removes the next message for the specified MicroService, blocking if necessary.
    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> myQueue = serviceQueues.get(m); 
        if (myQueue == null) {
            throw new IllegalStateException("MicroService " + m.getName() + " is not registered.");
        }
        return myQueue.take();
    }

}
