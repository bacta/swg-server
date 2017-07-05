package bacta.io.chat;

/**
 * Created by crush on 6/29/2017.
 */
public enum PersistentMessageStatus {
    NEW,
    UNREAD,
    READ,
    TRASH,
    DELETED;

    public char asChar() {
        if (this == NEW) {
            return 'N';
        } else if (this == UNREAD) {
            return 'U';
        } else if (this == READ) {
            return 'R';
        } else if (this == TRASH) {
            return 'T';
        } else if (this == DELETED) {
            return 'D';
        }

        return 0;
    }
}
