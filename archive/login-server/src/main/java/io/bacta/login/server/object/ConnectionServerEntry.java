/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.server.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
@RequiredArgsConstructor
public final class ConnectionServerEntry {
    private final int id;
    private final String clientServiceAddress;
    private final short clientServicePortPrivate;
    private final short clientServicePortPublic;
    private final short pingPort;
    @Setter
    private int numClients;

    /**
     * Used to sort connection server entries by least population. The least populated ConnectionServerEntry should
     * be the first connection server entry in a set if this comparator is used. That makes lookup of the entry
     * extremely fast.
     */
    public static final class LeastPopulationComparator implements Comparator<ConnectionServerEntry> {
        @Override
        public int compare(ConnectionServerEntry x, ConnectionServerEntry y) {
            return Integer.compare(x.numClients, y.numClients);
        }
    }
}
