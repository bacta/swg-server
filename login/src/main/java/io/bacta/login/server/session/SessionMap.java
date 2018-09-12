package io.bacta.login.server.session;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class SessionMap implements Iterable<Session> {
    private final Map<String, Session> bySessionId;
    private final Multimap<Integer, Session> byAccountId;

    public SessionMap() {
        this.bySessionId = new HashMap<>();
        this.byAccountId = ArrayListMultimap.create();
    }

    @NotNull
    @Override
    public Iterator<Session> iterator() {
        return bySessionId.values().iterator();
    }

    public void add(final Session session) {
        bySessionId.put(session.getKey(), session);
        byAccountId.put(session.getAccountId(), session);
    }

    public Collection<Session> getByAccountId(final int accountId) {
        return byAccountId.get(accountId);
    }

    public Session getBySessionId(final String sessionId) {
        return bySessionId.get(sessionId);
    }

    public void removeBySessionId(final String sessionId) {
        final Session session = bySessionId.remove(sessionId);

        if (session != null)
            byAccountId.remove(session.getAccountId(), session);
    }

    public void removeByAccountId(final int accountId) {
        final Collection<Session> sessions = byAccountId.removeAll(accountId);

        if (sessions != null && sessions.size() > 0) {
            final Session session = sessions.iterator().next();
            bySessionId.remove(session.getKey());
        }
    }

    public void remove(final Session session) {
        removeBySessionId(session.getKey());
    }

    public int size() {
        return bySessionId.size();
    }

    public void clear() {
        bySessionId.clear();
        byAccountId.clear();
    }
}
