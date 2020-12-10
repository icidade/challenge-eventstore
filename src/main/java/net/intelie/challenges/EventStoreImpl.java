package net.intelie.challenges;

/**
 * Implementation of the {@link EventStore} interface
 * <p><br>TODO: write the documentation including the decisions made.</p>
 * @author Irlan Cidade (icidade@gmail.com)
 */
public class EventStoreImpl implements EventStore{
    /**
     *
     * @param event The event to insert
     */
    @Override
    public void insert(Event event) {
        //TODO: implement
    }

    /**
     *
     * @param type
     */
    @Override
    public void removeAll(String type) {
        //TODO: implement
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
        return null;  //TODO: implement
    }
}
