package io.bacta.game.container;

import io.bacta.shared.container.ContainerErrorCode;
import lombok.Getter;

@Getter
public class ContainerTransferFailedException extends Exception {
    private static final long serialVersionUID = -6169173590276631871L;

    private final long itemNetworkId;
    private final long destinationNetworkId;
    private final ContainerErrorCode errorCode;

    public ContainerTransferFailedException(
            final long itemNetworkId,
            final long destinationNetworkId,
            final ContainerErrorCode errorCode) {

        super(String.format("Failed to move object %d to container %d because %s.",
                itemNetworkId,
                destinationNetworkId,
                errorCode.toString()));

        this.itemNetworkId = itemNetworkId;
        this.destinationNetworkId = destinationNetworkId;
        this.errorCode = errorCode;
    }
}
