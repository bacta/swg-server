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

package io.bacta.chat.persistent;

import java.util.List;

/**
 * Created by crush on 6/7/2017.
 */
public interface PersistentMessageRepository {
    PersistentMessage create(PersistentMessage message);

    PersistentMessage update(PersistentMessage message);

    int delete(int messageId);

    /**
     * Deletes all the messages for a given network id.
     * @param networkId The network id to which the messages belong.
     */
    void deleteAll(long networkId);

    /**
     * Gets all persistent messages that belong to the given network id.
     * @param networkId The id to which the persistent messages belong.
     * @return A list of network ids, or an empty list if none exist.
     * @throws ArrayIndexOutOfBoundsException If the network id is unknown.
     */
    List<PersistentMessage> getAll(long networkId);

    /**
     * Gets a single persistent message with the corresponding message id.
     * @param messageId The id of the message to get.
     * @return The persistent message if it exists. Otherwise, null.
     */
    PersistentMessage get(int messageId);
}
