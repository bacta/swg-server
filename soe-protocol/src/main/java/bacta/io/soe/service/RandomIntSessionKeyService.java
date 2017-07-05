package bacta.io.soe.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Created by kyle on 7/2/2017.
 */

@Component
@Scope("prototype")
public class RandomIntSessionKeyService implements SessionKeyService {
    private final Random random = new Random();

    @Override
    public int getNextKey() {
        return random.nextInt();
    }

}
