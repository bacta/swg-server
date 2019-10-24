package io.bacta.shared.container;

import lombok.Getter;

@Getter
public class ContainerTransferException extends Exception {
    private static final long serialVersionUID = -5895188777132860255L;

    private final long containerNetworkId;
    private final long itemNetworkId;
    private final ContainerErrorCode errorCode;

    public ContainerTransferException(
            final long containerNetworkId,
            final long itemNetworkId,
            final ContainerErrorCode errorCode) {

        super(String.format("Transfer of item %d to container %d failed with reason: %s",
                itemNetworkId,
                containerNetworkId,
                errorCode.name()));

        this.containerNetworkId = containerNetworkId;
        this.itemNetworkId = itemNetworkId;
        this.errorCode = errorCode;
    }
}
