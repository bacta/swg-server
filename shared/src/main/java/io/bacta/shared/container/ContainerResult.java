package io.bacta.shared.container;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by crush on 5/17/2016.
 * <p>
 * Encapsulates the ContainerErrorCode so that it can be passed by reference to methods.
 */
@Getter
@Setter
public final class ContainerResult {
    private ContainerErrorCode error = ContainerErrorCode.SUCCESS;
}
