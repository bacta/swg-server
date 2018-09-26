package io.bacta.game.command;

import io.bacta.swg.math.Vector;
import io.bacta.swg.util.NetworkId;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public final class CommandQueueParameters {
    private static final String delimiters = " ";

    private final String parametersString;
    private final int length;

    private int position = 0;

    public CommandQueueParameters(String parametersString) {
        this.parametersString = parametersString;
        this.length = parametersString.length();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public String nextString() throws NoSuchElementException {
        if (position < length && delimiters.indexOf(parametersString.charAt(position)) >= 0) {
            while (++position < length && delimiters.indexOf(parametersString.charAt(position)) >= 0) ;
        }
        if (position < length) {
            int start = position;
            while (++position < length && delimiters.indexOf(parametersString.charAt(position)) < 0) ;

            return parametersString.substring(start, position);
        }

        //If no tokens are left, we return an empty string rather than throw an exception.
        return "";
    }

    public int nextInt() throws NoSuchElementException {
        final String token = nextString();

        if (token.isEmpty())
            return -1;

        return Integer.parseInt(token);
    }

    public long nextNetworkId() throws NoSuchElementException {
        final String token = nextString();

        if (token.isEmpty())
            return NetworkId.INVALID;

        //TODO: Resolve player name to network id if token is a player name.

        return Long.parseLong(token);
    }

    public float nextFloat() throws NoSuchElementException {
        final String token = nextString();
        return Float.parseFloat(token);
    }

    public boolean nextBoolean() throws NoSuchElementException {
        return nextInt() != 0;
    }

    public Vector nextVector() throws NoSuchElementException {
        return new Vector(nextFloat(), nextFloat(), nextFloat());
    }

    @Override
    public String toString() {
        return parametersString;
    }
}
