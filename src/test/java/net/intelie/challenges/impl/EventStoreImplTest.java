package net.intelie.challenges.impl;

import junit.framework.TestCase;
import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EventStoreImplTest extends TestCase {

    private final Integer NUMBER_OF_ELEMENTS = 10; //the size of the list to test
    private EventStoreImpl eventStoreTest;  // our target object to test


    public void setUp() throws Exception {
        super.setUp();
        eventStoreTest = setList(NUMBER_OF_ELEMENTS);
    }


    public void testInsert() {
        assertTrue(testIteration());
    }


    public void testInsertZero() {
        try {
            insertError(0L);
        }catch(IllegalStateException e){
            assertTrue(true);
        }
    }

    public void testInsertBelowZero() {
        try {
            insertError(-1L);
        }catch(IllegalStateException e){
            assertTrue(true);
        }
    }

    private void insertError(long value) {
        EventStoreImpl eventStoreNegativeTest = new EventStoreImpl(new HashMap<>(), new HashMap<>());
        eventStoreNegativeTest.insert(new Event("negativeTest", value));
    }


    public void testQuery() {
        for(int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
            try (EventIterator it = eventStoreTest.query(String.valueOf(i),0,10)) {
                while(it.moveNext()){
                    assertEquals(i+1, it.current().timestamp());
                }
            } catch (Exception e) {
                assertFalse(true);
            }
        }
    }

    public void testQueryNegative() {

        try (EventIterator it = eventStoreTest.query(String.valueOf(0),-1,0)) {
            //will fail
        } catch (Exception e) {
            assertEquals("There are errors on params", e.getMessage());
        }

    }


    public void testThreadSafe() throws InterruptedException{
        final EventStoreImpl store = new EventStoreImpl(new HashMap<>(),new HashMap<>());
        for(long i = 0; i < 100000; i++) {
            store.insert(new Event("a", i+1));
        }
        final StringBuilder trace = new StringBuilder();

        final Thread writer = new Thread(() -> {
            for(long i = 0; i < 50000; i++) {
                store.insert(new Event("a", i+1));
                assertEquals(store.getEventMap().get("a").size(), i+1);
            }
            trace.append(5);
        });

        final Thread remover = new Thread(() -> {
            try(EventIterator it = store.query("a", 0L, 100001)) {
                trace.append(3);
                writer.start();
                int count = 0;
                while(it.moveNext()) {
                    it.remove();
                    count++;
                }
                trace.append(4);
                assertEquals(100000, count);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });

        Thread reader = new Thread(() -> {
            try(EventIterator it = store.query("a", 0L, 100001)) {
                trace.append(1);
                remover.start();
                Set<Long> elements = new HashSet<>();
                while(it.moveNext()) {
                    if(!elements.add(it.current().timestamp())) {
                        fail("The same element was read twice");
                    }
                }
                trace.append(2);
                assertEquals(100000, elements.size());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
        reader.start();

        reader.join();
        remover.join();
        writer.join();

        assertEquals("12345", trace.toString());
    }


    public void testRemoveAll() {
        EventStoreImpl eventStoreToRemove = setList(NUMBER_OF_ELEMENTS);
        for(int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
            eventStoreToRemove.removeAll(String.valueOf(i));
        }
        assertEquals(0, eventStoreToRemove.getEventMap().size());

    }

    private boolean testIteration(){
        Map<String, List<Event>> eventMapTest = eventStoreTest.getEventMap();
        for(int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
            if(i+1 != eventMapTest.get(String.valueOf(i)).get(0).timestamp()){
                return false;
            }
        }
        return true;
    }

    private EventStoreImpl setList(int size) {
        EventStoreImpl eventStore = new EventStoreImpl(new HashMap<>(), new HashMap<>());

        for(int i = 0; i < size; i++) {
            eventStore.insert(new Event(String.valueOf(i),i+1)); // timestamp cannot be 0
        }

        return eventStore;
    }
}