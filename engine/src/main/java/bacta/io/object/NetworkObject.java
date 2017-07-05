package bacta.io.object;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kburkhardt on 2/23/14.
 */

public abstract class NetworkObject implements Comparable<NetworkObject> {
    public static final long INVALID = 0;

    @Getter @Setter
    protected long networkId;

    @Getter @Setter
    protected transient boolean dirty = false;

    @Override
    public final int compareTo(NetworkObject o) {
        if(o.networkId == networkId) {
            return 0;
        }

        if(networkId > o.networkId) {
            return -1;
        }

        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkObject)) return false;

        NetworkObject that = (NetworkObject) o;

        if (networkId != that.networkId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (networkId ^ (networkId >>> 32));
    }
}
