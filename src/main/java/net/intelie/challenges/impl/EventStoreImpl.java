package net.intelie.challenges.impl;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.EventStore;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of the {@link EventStore} interface
 *
 * <p><br>For this challenge we will use a {@link HashMap}. This collection can be accessed in O(1) time.
 * prioritizing performance and focusing a thread-safe operation we will synchronize operations acquiring locks
 * and we must assure them to be fast. Having in mind the code will have massive insert and remove
 * operations that will make it run on O(n) influenced by resorting operation in the collection caused by all add/remove
 * elements. All in all this approach is more efficient than the use of a {@link java.util.LinkedList} or {@link Stack}.</p>
 *
 * @author Irlan Cidade (icidade@gmail.com)
 */
public class EventStoreImpl implements EventStore{

    private final Map<String, List<Event>> eventMap;
    private final Map<String, Lock> lockMap;

    /**
     * <p>The constructor for our EventStore implementation.</p>
     * @param eventMap Even knowing HashMap is not synchronized, it's very fast for our objective so we will ensure synchronicity in the code
     * @param lockMap Hold the locks for operations in the eventMap
     */
    public EventStoreImpl(Map<String, List<Event>> eventMap, Map<String, Lock> lockMap) {
        this.eventMap = eventMap;
        this.lockMap = lockMap;
    }

    /**
     * Accessor method
     * @return
     */
    Map<String, List<Event>> getEventMap() {
        return eventMap;
    }


    /**
     *
     * @param event The event to insert. Synchronized on the type to be thread-safe
     */
    @Override
    public void insert(Event event) {
        if(isvalid(event)){
            Lock lock;
            synchronized(this) {
                lock = lockMap.computeIfAbsent(event.type(), t -> new ReentrantLock());
            }
            lock.lock();
            //once locked we perform the operation and release the lock ASAP
            try{
                List<Event> events = eventMap.computeIfAbsent(event.type(), e -> new ArrayList<>());
                int index = -(Collections.binarySearch(events, event))-1;
                events.add(index,event);
            }finally {
                lock.unlock();
            }
        }else{
            throw new IllegalStateException("There was an error on this event");
        }
    }

    /**
     * Simply validate data on the event
     * @param event the event to be evaluated
     * @return
     */
    private boolean isvalid(Event event) {
        return ((null != event.type()) && (event.timestamp() > 0));
    }

    /**
     * Reset all events of a certain type
     * @param type
     */
    @Override
    public void removeAll(String type) {
        eventMap.remove(type);
        lockMap.remove(type);
    }

    /**
     *
     * @param type      The type we are querying for.
     * @param startTime Start timestamp (inclusive).
     * @param endTime   End timestamp (exclusive).
     * @return
     */
    @Override
    public EventIterator query(String type, long startTime, long endTime) {
        if(areValidParams(type, startTime, endTime)){
            List<Event> events = eventMap.get(type);
            Lock lock = lockMap.get(type);
            lock.lock(); // acquiring lock
            Interval interval = intervalSearch(events, startTime, endTime);
            return new EventIteratorImpl(events,interval.min, interval.max, lock);
        }else{
            throw new IllegalStateException("There are errors on params");
        }
    }

    private boolean areValidParams(String type, long startTime, long endTime) {
        return (null!=type && startTime >= 0 && endTime > 0);
    }

    /**
     * Performs a binary search through a given interval of values
     * @param event The event itself
     * @param element1 first timestamp for comparison
     * @param element2 second element for comparison
     * @return
     */
    private static Interval intervalSearch(List<Event> event, long element1, long element2) {

        int nextMin = 0;
        int min = nextMin;
        int nextMax = event.size()-1;
        int max = nextMax;
        Interval interval = new Interval();

        while (min <= max) {

            int midIndex = (min + max) >>> 1;
            long midVal = event.get(midIndex).timestamp();

            if(element1 <= midVal) {
                max = midIndex - 1;
                if(element2 <= midVal) {
                    nextMax = max;
                }
            } else {
                min = midIndex + 1;
                if(element2 > midVal) {
                    nextMin = min;
                }
            }
        }
        
        nextMin = Math.max(nextMin, min);
        interval.min = min;

        while (nextMin <= nextMax) {
            int midIndex = (nextMin + nextMax) >>> 1;
            long midValue = event.get(midIndex).timestamp();

            if(element2 <= midValue) {
                nextMax = midIndex - 1;
            } else {
                nextMin = midIndex + 1;
            }
        }
        interval.max = nextMin;

        return interval;
    }


    private static class Interval {
        int min;
        int max;
    }
}
