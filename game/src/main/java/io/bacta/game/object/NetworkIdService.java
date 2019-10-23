package io.bacta.game.object;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public final class NetworkIdService {
    private static final AtomicLong idGenerator = new AtomicLong(100000000000L);

    public long nextNetworkId() {
        return idGenerator.incrementAndGet();
    }
}
