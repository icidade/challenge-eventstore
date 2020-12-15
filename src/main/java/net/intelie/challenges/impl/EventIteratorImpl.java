package net.intelie.challenges.impl;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * This implementation expose the basic methods for the list of Events. MoveNext, get current Event from the cursor
 * position, remove and close.
 */
public class EventIteratorImpl implements EventIterator {

    private List<Event> event;
    private final int start;
    private int end;
    private int cursor = -1;
    private Lock lock;

    /**
     *
     * @param event The event list
     * @param start the start position of the interval
     * @param end the end position of the interval
     * @param lock the register lock
     */
    EventIteratorImpl(List<Event> event, int start, int end, Lock lock) {
        this.event = event;
        this.start = Math.max(start,0);//inclusive
        this.end = Math.min(end, event.size());//exclusive
        this.lock = lock;
    }

    @Override
    public boolean moveNext() {
        if (cursor == -1) {
            cursor = start;
        } else {
            cursor++;
        }
        return cursor < end;
    }

    @Override
    public Event current() {
        if(cursor == -1 || cursor == end) {
            throw new IllegalStateException("Error: Invalid cursor");
        }
        return event.get(cursor);
    }

    @Override
    public void remove() {
        if(cursor == -1 || cursor == end) {
            throw new IllegalStateException("Error: Invalid cursor");
        }
        event.remove(cursor);
        cursor--;
        end--;
    }

    @Override
    public void close() throws Exception {
        if(lock != null) {
            lock.unlock(); // cleans the lock
            lock = null;
        }
        event = null;
    }
}
