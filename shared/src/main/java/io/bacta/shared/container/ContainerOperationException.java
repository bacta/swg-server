package io.bacta.shared.container;

import lombok.Getter;

@Getter
public class ContainerOperationException extends Exception {
    private static final long serialVersionUID = 4204996713208497307L;

    private final long containerNetworkId;
    private final ContainerErrorCode errorCode;

    public ContainerOperationException(long containerNetworkId, ContainerErrorCode errorCode) {
        super(String.format("Operation on container %d failed with reason: %s", containerNetworkId, errorCode.name()));

        this.containerNetworkId = containerNetworkId;
        this.errorCode = errorCode;
    }
}
