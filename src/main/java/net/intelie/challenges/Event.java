package net.intelie.challenges;

import java.util.Objects;

/**
 * This is just an event stub, feel free to expand it if needed.
 * We implement comparable for sort reasons.
 */
public class Event implements Comparable<Event> {
    private final String type;
    private final long timestamp;

    public Event(String type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public String type() {
        return type;
    }

    public long timestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Event event) {
        return this.timestamp < event.timestamp ? -1 : 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, timestamp);
    }
}
