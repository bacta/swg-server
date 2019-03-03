package io.bacta.shared.portal;

import io.bacta.shared.foundation.CrcString;
import io.bacta.shared.foundation.PersistentCrcString;
import io.bacta.shared.foundation.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 5/15/2016.
 */
public class PortalPropertyTemplate {
    public static final int TAG_CRC = Tag.convertStringToTag("CRC ");
    public static final int TAG_CELL = Tag.convertStringToTag("CELL");
    public static final int TAG_CELS = Tag.convertStringToTag("CELS");
    public static final int TAG_LGHT = Tag.convertStringToTag("LGHT");
    public static final int TAG_PRTL = Tag.convertStringToTag("PRTL");
    public static final int TAG_PRTO = Tag.convertStringToTag("PRTO");
    public static final int TAG_PRTS = Tag.convertStringToTag("PRTS");
    public static final int TAG_PGRF = Tag.convertStringToTag("PGRF");

    private PersistentCrcString name;
    private PersistentCrcString shortName;
    //private List<IndexedTriangleList> portalGeometryList;
    private List<PortalOwners> portalOwnersList;
    //private List<Cell> cellList;
    private List<String> cellNameList;
    //private int crc;
    //private BaseClass pathGraph;
    //private List<Vector> radarPortalGeometry;

    public PortalPropertyTemplate(final CrcString name) {
        this.name = new PersistentCrcString(name);
        this.portalOwnersList = new ArrayList<>();

    }


    public static final class PortalOwners {

    }
}
