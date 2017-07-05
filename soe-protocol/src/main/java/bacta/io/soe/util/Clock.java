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
