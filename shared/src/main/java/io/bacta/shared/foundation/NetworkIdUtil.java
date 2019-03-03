package io.bacta.shared.foundation;

/**
 * Created by crush on 5/27/2016.
 * <p>
 * Encapsulates operations for manipulating a 64 bit NetworkId. For example, packing the clusterId into the bits 53-61
 * when a NetworkId is being translated across clusters.
 */
public final class NetworkIdUtil {
    /**
     * Represents an invalid NetworkId.
     */
    public static final long INVALID = 0;
    /**
     * The max NetworkId without a ClusterId that may be generated. If this number is ever exceeded, either there
     * is a serious problem, or the sun has already exploded.
     */
    private static final long MAX_NETWORK_ID_WITHOUT_CLUSTER_ID = 0x3FFFFFFFFFFFFFL;
    /**
     * Used for getting just the NetworkId from a NetworkId that also has a ClusterId.
     */
    private static final long NETWORK_ID_WITHOUT_CLUSTER_ID_MASK = 0x403FFFFFFFFFFFFFL;
    /**
     * Used for getting just the ClusterId from a NetworkId.
     */
    private static final long CLUSTER_ID_MASK = 0x3FC0000000000000L;

    /**
     * Gets a NetworkId with the given ClusterId appended to it in the reserved range.
     *
     * @param clusterId The ClusterId to append to the NetworkId
     * @param networkId The NetworkId to which to append the ClusterId.
     * @return The new NetworkId with the ClusterId appended in the bits 53-62.
     */
    public static long getNetworkIdWithClusterId(final byte clusterId, final long networkId) {
        if (clusterId == 0 || networkId == 0)
            return networkId;

        return ((long) clusterId << 54) | networkId;
    }

    /**
     * Gets a NetworkId without the ClusterId appended to it. If no ClusterId information is encoded
     * then it will just return the NetworkId.
     *
     * @param networkId The NetworkId from which to remove the ClusterId.
     * @return The NetworkId without the ClusterId encoded in it.
     */
    public static long getNetworkIdWithoutClusterId(final long networkId) {
        if (networkId <= MAX_NETWORK_ID_WITHOUT_CLUSTER_ID)
            return networkId; //It doesn't have cluster id encoded.

        return NETWORK_ID_WITHOUT_CLUSTER_ID_MASK & networkId;
    }

    /**
     * Gets the ClusterId out of a NetworkId that has been encoded with the ClusterId.
     * @param networkId The NetworkId that has been encoded with the ClusterId.
     * @return The ClusterId.
     */
    public static byte getClusterId(final long networkId) {
        if (networkId <= MAX_NETWORK_ID_WITHOUT_CLUSTER_ID)
            return 0; //It doesn't have cluster id encoded.

        return (byte) ((CLUSTER_ID_MASK & networkId) >> 54);
    }

}
