package bacta.io.chat;

/**
 * Created by crush on 6/28/2017.
 * <p>
 * Defines how the galaxy chat service communicates with a chat server authority. A chat server authority may be a
 * remote chat server like an XMPP based server, IRC, Slack, etc. The default implementation provided with Bacta is
 * {@link LocalChatInterface} which handles all chat operations locally, and functions mainly as a dummy chat
 * server. It is recommended for local development or small-scale LAN based servers with only a single galaxy cluster.
 */
public interface ChatInterface {
    /**
     * Attempts to log the avatar into the chat server. Expected to return {@link ChatResult#SUCCESS} if the login
     * was successful.
     *
     * @param characterName The name of the character logging into the chat server.
     * @param networkId     The id of the character logging into the chat server.
     * @return {@link ChatResult#SUCCESS} if login was successful. Should not be null.
     */
    ChatResult loginAvatar(String characterName, long networkId);

    /**
     * Attempts to log the avatar out of the chat server. Expected to return {@link ChatResult#SUCCESS} if the logout
     * was successful.
     *
     * @param avatarId The avatar that is being logged out of the chat server.
     * @return {@link ChatResult#SUCCESS} if logout was successful. Should not be null.
     */
    ChatResult logoutAvatar(ChatAvatarId avatarId);

    /**
     * Attempts to destroy an avatar on the authoritative chat server.
     *
     * @param avatarId
     * @return
     */
    ChatResult destroyAvatar(ChatAvatarId avatarId);
}
