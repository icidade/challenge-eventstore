package net.intelie.challenges.impl;

import junit.framework.TestCase;
import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

public class EventIteratorImplTest extends TestCase {

    private final Integer NUMBER_OF_ELEMENTS = 10; //the size of the list to test
    List<Event> events = new ArrayList<>();

    public void testMoveNext() throws Exception {
        events = setList(NUMBER_OF_ELEMENTS); // this test was wrote to run with 10 elements
        try(EventIteratorImpl it = new EventIteratorImpl(events, 0, 10, null)){
            it.moveNext();
            it.moveNext();
            it.remove();
            assertEquals(1L, it.current().timestamp());
            it.moveNext();
            assertEquals(3L, it.current().timestamp());
            it.moveNext();
            it.moveNext();
            it.moveNext();
            it.moveNext();
            it.moveNext();
            it.moveNext();
            it.moveNext();
            assertFalse(it.moveNext());
        }
    }

    public void testCurrent() throws Exception {
        events = setList(NUMBER_OF_ELEMENTS); // this test was wrote to run with 10 elements
        try(EventIteratorImpl it = new EventIteratorImpl(events, 0, 10, null)){
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertEquals(3L, it.current().timestamp());
            assertTrue(it.moveNext());
            assertEquals(4L, it.current().timestamp());
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertTrue(it.moveNext());
            assertFalse(it.moveNext());
        }
    }

    public void testRemove() throws Exception {
        events = setList(NUMBER_OF_ELEMENTS); // this test was wrote to run with 10 elements
        try(EventIteratorImpl it = new EventIteratorImpl(events, 0, 10, null)){
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertTrue(it.moveNext());
            it.remove();
            assertFalse(it.moveNext());
        }
    }

    public void testClose() throws Exception{
        final Lock lock = new ReentrantLock();
        lock.lock();

        try (EventIteratorImpl it = new EventIteratorImpl(new ArrayList<>(), 0, 1, lock)) {
            Thread lockChecker = new Thread(() -> {
                assertFalse(lock.tryLock());
            });
            lockChecker.start();
            lockChecker.join();
        }

        Thread lockChecker = new Thread(() -> {
            assertTrue(lock.tryLock());
        });
        lockChecker.start();
        lockChecker.join();
    }

    private List<Event> setList(int size){
        List<Event> list = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_ELEMENTS; i++){
            list.add(new Event(String.valueOf(i),i+1));
        }
        return list;
    }

}