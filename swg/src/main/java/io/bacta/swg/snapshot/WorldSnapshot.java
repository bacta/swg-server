package io.bacta.swg.snapshot;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WorldSnapshot {
    protected final List<WorldSnapshotNode> nodeList = new ArrayList<>();
    protected final List<String> objectTemplateNames = new ArrayList<>();
    protected final TIntIntMap objectTemplateCrcToNameIndexMap = new TIntIntHashMap();
    protected final TLongObjectMap<WorldSnapshotNode> networkIdNodeMap = new TLongObjectHashMap<>();

    public void clear() {
        this.nodeList.clear();
        this.objectTemplateNames.clear();
        this.objectTemplateCrcToNameIndexMap.clear();
        this.networkIdNodeMap.clear();
    }
}
