package bacta.io.chat;

import bacta.io.buffer.BufferUtil;
import bacta.io.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 * Created by crush on 12/30/2014.
 * <p>
 * ChatAvatarId is the object used by SOE to represent a chat participant's identity on their servers. These identities
 * can be specified locally without required prefixes, but after passing through the server, these prefixes will be
 * assumed by whatever the game server has specified. For example, if a local ChatAvatarId is specified only as "crush",
 * and it resides on the "bacta" galaxy of the game "swg", then it should be assumed that the prefixes
 * "swg.bacta.crush" shall be appended before it is passed along to the associated chat server.
 *
 * We have renamed this object simply ChatAvatarId since it is in the `chat` package already.
 */
public final class ChatAvatarId implements ByteBufferWritable {
    /**
     * Represents an avatar with no game code, cluster, or name specified.
     */
    public static final ChatAvatarId EMPTY = new ChatAvatarId("", "", "");

    private final String gameCode;
    private final String cluster;
    private final String name;

    public ChatAvatarId(final String gameCode, final String cluster, final String name) {
        this.gameCode = gameCode;
        this.cluster = cluster;
        this.name = name;
    }

    public ChatAvatarId(final String cluster, final String name) {
        this.gameCode = "";
        this.cluster = cluster;
        this.name = name;
    }


    /**
     * Turns a string into a ChatAvatarId object. Attempts to parse blocks separated by a '.' as the three components
     * of the ChatAvatarId: gameCode.cluster.name. Works in reverse order. The following should be expected:
     * <code>
     * "com.swg.bacta.crush" => { gameCode: "com.swg", cluster: "bacta", name: "crush" }
     * "swg.bacta.crush" => { gameCode: "swg", cluster: "bacta", name: "crush" }
     * "bacta.crush" => { gameCode: "", cluster: "bacta", name: "crush" }
     * "crush" => { gameCode: "", cluster: "", name: "crush" }
     * </code>
     *
     * @param name The name used to create this ChatAvatarId.
     */
    public ChatAvatarId(final String name) {
        int index = name.lastIndexOf('.');

        if (index != -1) {
            this.name = name.substring(index + 1);

            index = name.lastIndexOf('.', index - 1);

            if (index != -1) {
                this.cluster = name.substring(index + 1, name.length() - this.name.length() - 1);
                this.gameCode = name.substring(0, index);
            } else {
                this.cluster = name.substring(0, name.length() - this.name.length() - 1);
                this.gameCode = "";
            }
        } else {
            this.name = name;
            this.cluster = "";
            this.gameCode = "";
        }
    }

    public ChatAvatarId(final ByteBuffer buffer) {
        this.gameCode = BufferUtil.getAscii(buffer);
        this.cluster = BufferUtil.getAscii(buffer);
        this.name = BufferUtil.getAscii(buffer);
    }

    public String getGameCode() {
        return gameCode;
    }

    public String getCluster() {
        return cluster;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the name exactly as it was specified. If prefixes are missing, then they will be omitted from the returned
     * string.
     *
     * @return String representation fo the ChatAvatarId with any existing prefixes attached.
     * @see #getNameWithNecessaryPrefix(String, String)
     */
    public String getFullName() {
        final StringBuilder stringBuilder = new StringBuilder(gameCode.length() + cluster.length() + name.length());

        if (gameCode.length() > 0)
            stringBuilder.append(gameCode).append('.');

        if (cluster.length() > 0)
            stringBuilder.append(cluster).append('.');

        stringBuilder.append(name);

        return stringBuilder.toString();
    }

    /**
     * Gets only the necessary parts of the ChatAvatarId for the local game and cluster. If it's on the same game and
     * cluster, then it will return just the name. If its on the same game, but a different cluster, then it will
     * return the name and cluster. If its a different game and cluster, then it will return the game, cluster, and name.
     *
     * @param localGameCode The local game server's game code.
     * @param localCluster  The local game server's cluster name.
     * @return The name with only the necessary prefixes appended.
     */
    public final String getNameWithNecessaryPrefix(final String localGameCode, final String localCluster) {
        if (!gameCode.equalsIgnoreCase(localGameCode)) {
            return String.format("%s.%s.%s", gameCode, cluster, name);
        } else if (!cluster.equalsIgnoreCase(localCluster)) {
            return String.format("%s.%s", cluster, name);
        } else {
            return name;
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, gameCode);
        BufferUtil.putAscii(buffer, cluster);
        BufferUtil.putAscii(buffer, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ChatAvatarId avatarId = (ChatAvatarId) o;

        if (getGameCode() != null ? !getGameCode().equals(avatarId.getGameCode()) : avatarId.getGameCode() != null)
            return false;
        if (getCluster() != null ? !getCluster().equals(avatarId.getCluster()) : avatarId.getCluster() != null)
            return false;
        return getName() != null ? getName().equals(avatarId.getName()) : avatarId.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getGameCode() != null ? getGameCode().hashCode() : 0;
        result = 31 * result + (getCluster() != null ? getCluster().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
