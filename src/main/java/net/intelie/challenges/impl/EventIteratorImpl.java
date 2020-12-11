package net.intelie.challenges.impl;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class EventIteratorImpl implements EventIterator {

    private List<Event> event;
    private final int start;
    private int end;
    private int cursor = -1;
    private Lock lock;

    EventIteratorImpl(List<Event> event, int start, int end, Lock lock) {
        this.event = event;
        this.start = Math.max(start,0);//inclusive
        this.end = Math.min(end, event.size());//exclusive
        this.lock = lock;
    }

    @Override
    public boolean moveNext() {
        return false;
    }

    @Override
    public Event current() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public void close() throws Exception {

    }
}
