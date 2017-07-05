package bacta.io.lang;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by kyle on 4/13/2017.
 */
@Slf4j
@Component
public class StringToInetAddress implements Converter<String, InetAddress> {
    @Override
    public InetAddress convert(String s) {
        try {
            return InetAddress.getByName(s);
        } catch (UnknownHostException e) {
            LOGGER.error("Unable to parse InetAddress '{}'", s);
            return null;
        }
    }
}
