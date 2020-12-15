package net.intelie.challenges;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventTest {
    @Test
    public void thisIsAWarning() throws Exception {
        Event event = new Event("some_type", 123L);

        //THIS IS A WARNING:
        //Some of us (not everyone) are coverage freaks.
        assertEquals(123L, event.timestamp());
        assertEquals("some_type", event.type());
    }

    @Test
    public void compareToTest(){
        //Second element Higher
        Event event1 = new Event("event1", 1L);
        Event event2 = new Event("event2", 2L);
        assertTrue(event1.compareTo(event2) < 0);

        //Second element lower
        event1 = new Event("event1", 2L);
        event2 = new Event("event2", 1L);
        assertTrue(event1.compareTo(event2) > 0);

        //Two equals elements
        event1 = new Event("event", 0L);
        event2 = new Event("event", 0L);
        assertTrue(event1.compareTo(event2) != 0);
    }

}