package io.bacta.swg.collision;

/**
 * Created by crush on 5/13/2016.
 */
public enum CollinearResult {
    LEFT_SIDE,// (2D) Point lies on the left (counterclockwise) side of AB
    RIGHT_SIDE, // (2D) Point lies on the right (clockwise) side of AB

    COINCIDENT_A,// Point is coincident with A
    COINCIDENT_B,// Point is coincident with B

    OVERLAP_LEFT,// Point is collinear with AB, forms line V-A-B
    OVERLAP_CENTER,// Point is collinear with AB, forms line A-V-B
    OVERLAP_RIGHT,// Point is collinear with AB, forms line A-B-V

    DISJOINT,// (3D) Point does not lie on the line
    OVERLAP// OverlapLeft || OverlapRight || OverlapCenter || CoincidentA || CoincidentB
}
