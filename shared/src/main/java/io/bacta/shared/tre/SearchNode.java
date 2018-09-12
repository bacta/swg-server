package io.bacta.shared.tre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 3/19/14.
 */
abstract class SearchNode implements Comparable<SearchNode> {
    protected static final Logger logger = LoggerFactory.getLogger(SearchNode.class);

    private final int searchPriority;

    public final int getSearchPriority() {
        return searchPriority;
    }

    public SearchNode(int searchPriority) {
        this.searchPriority = searchPriority;
    }

    @Override
    public int compareTo(SearchNode o) {
        return Integer.compare(searchPriority, o.searchPriority) * -1; //Descending order.
    }

    public abstract byte[] open(String filePath);

    public abstract boolean exists(String filePath);
}