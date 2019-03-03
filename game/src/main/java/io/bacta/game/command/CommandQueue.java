package io.bacta.game.command;

import io.bacta.game.object.ServerObject;
import io.bacta.shared.command.Command;
import io.bacta.shared.property.Property;

/**
 * Created by crush on 5/18/2016.
 * <p>
 * This class is the spinal cord of the game server.
 */
public class CommandQueue extends Property {
    public static int getClassPropertyId() {
        return 889825674;
    }


    public CommandQueue(final ServerObject owner) {
        super(getClassPropertyId(), owner);
    }

    /**
     * Adds a command to the command queue.
     *
     * @param command
     * @param targetId
     * @param params
     * @param sequenceId
     * @param clearable
     * @param priority
     */
    public void enqueue(final Command command, final long targetId, final String params, final int sequenceId, final boolean clearable, final Command.Priority priority) {

    }

    /**
     * Remove a specific command from the command queue.
     *
     * @param sequenceId The id of the command.
     */
    public void remove(final int sequenceId) {

    }

    /**
     * Called from the owning object. Used to update the command queue. Should be called once for each queue at each
     * server loop.
     *
     * @param time
     */
    public void update(final float time) {

    }

    /**
     * Temporary instance value used during command execution.
     */
    Command.ErrorCode status;


    public static final class State {
        public static final int INVALID = -1;
        /**
         * The command queue is waiting for a command to start warming up.
         * The command queue may be empty in this state, or the current
         * command is waiting for a cooldown timer to end.
         */
        public static final int WAITING = 0;
        /**
         * The current command is warming up.
         */
        public static final int WARMUP = 1;
        /**
         * The current command is executing.
         */
        public static final int EXECUTE = 2;
    }
}
