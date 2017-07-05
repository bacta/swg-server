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

package bacta.io.soe.util;

/**
 * Created by kyle on 5/30/2016.
 */
public final class Clock {

    private static long currentCorrection = 0;
    private static long lastStamp = 0;

    public static int now() {

        long cs = System.currentTimeMillis();
        cs += currentCorrection;
        if (cs <lastStamp)
        {
            // clock moved backwards (somebody changed it), don't ever let this happen
            // if clock moves forward, there is no way we can recognize it, code will just
            // have to deal with it.  In the case of the UdpLibrary, it will likely result
            // in a ton of pending packets thinking they have gotten lost and being sent, fairly harmless.
            currentCorrection += (lastStamp - cs);
            cs = lastStamp;
        }
        lastStamp = cs;
        return (int)cs;
    }

}
