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

package io.bacta.session.client;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import io.bacta.session.message.Session;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by crush on 7/7/2017.
 */
abstract class SessionRequestAsync extends FiberAsync<Session, SessionRequestException> implements SessionRequestCompletion {
    @Override
    public void success(Session session) {
        asyncCompleted(session);
    }

    @Override
    public void failure(SessionRequestException exception) {
        asyncFailed(exception);
    }

    @Override
    @Suspendable
    public Session run() throws SessionRequestException, InterruptedException {
        try {
            return super.run();
        } catch (SuspendExecution ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    @Suspendable
    public Session run(long timeout, TimeUnit unit) throws SessionRequestException, InterruptedException, TimeoutException {
        try {
            return super.run(timeout, unit);
        } catch (SuspendExecution ex) {
            throw new AssertionError(ex);
        }
    }
}
