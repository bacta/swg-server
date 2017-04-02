package com.ocdsoft.bacta.soe.protocol.protocol;

import java.util.Random;

/**
 * Created by Kyle on 8/16/2014.
 */
public class DistanceBenchmark {


    public static void main(String[] args) {

        int loop = 1000000000;

        for(int x = 0; x < 5; ++x) {

            float dx = new Random().nextInt(100);
            float dy = new Random().nextInt(100);

            System.out.println("Iterations: " + loop);
            System.out.println("Seed: dx: " + dx + ", dy: " + dy);


            long start = System.currentTimeMillis();
            long total = 0;
            for (int i = 0; i < loop; ++i) {
                total += approx_distance((int) dx, (int) dy);
            }
            System.out.println("Approx: " + (System.currentTimeMillis() - start) + "ms - " + total / loop);

            total = 0;
            start = System.currentTimeMillis();
            for (int i = 0; i < loop; ++i) {
                total += approx_distance2((int) dx, (int) dy);
            }
            System.out.println("Approx2: " + (System.currentTimeMillis() - start) + "ms - " + total / loop);


            total = 0;
            start = System.currentTimeMillis();
            for (int i = 0; i < loop; ++i) {
                total += distance((int) dx, (int) dy);
            }
            System.out.println("Accurate: " + (System.currentTimeMillis() - start) + "ms - " + total / loop);

            total = 0;
            int total2 = 0;
            start = System.currentTimeMillis();
            for (int i = 0; i < loop; ++i) {
                total += distanceSquared(dx, dy);
                total2 += ((i % 5) + 2) * ((i % 5) + 2);
            }
            System.out.println("Accurate Squared: " + (System.currentTimeMillis() - start) + "ms - " + (int) Math.sqrt(total / loop));
            System.out.println();
            System.out.println();
        }
    }

    private static int approx_distance( int dx, int dy )
    {
        int min, max;

        if ( dx < 0 ) dx = -dx;
        if ( dy < 0 ) dy = -dy;

        if ( dx < dy )
        {
            min = dx;
            max = dy;
        } else {
            min = dy;
            max = dx;
        }

        // coefficients equivalent to ( 123/128 * max ) and ( 51/128 * min )
        return ((( max << 8 ) + ( max << 3 ) - ( max << 4 ) - ( max << 1 ) +
                ( min << 7 ) - ( min << 5 ) + ( min << 3 ) - ( min << 1 )) >> 8 );
    }

    private static int approx_distance2( int dx, int dy )
    {
        int min, max, approx;

        if ( dx < 0 ) dx = -dx;
        if ( dy < 0 ) dy = -dy;

        if ( dx < dy )
        {
            min = dx;
            max = dy;
        } else {
            min = dy;
            max = dx;
        }

        approx = ( max * 1007 ) + ( min * 441 );
        if ( max < ( min << 4 ))
            approx -= ( max * 40 );

        // add 512 for proper rounding
        return (( approx + 512 ) >> 10 );
    }

    private static double distance( int dx, int dy ) {
        return  Math.sqrt((dx * dx) + (dy * dy));
    }

    private static double distanceSquared( double dx, double dy ) {
        return  (dx * dx) + (dy * dy);
    }
}
