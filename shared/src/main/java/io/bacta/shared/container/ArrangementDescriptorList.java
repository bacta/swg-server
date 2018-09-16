package io.bacta.shared.container;

import io.bacta.shared.foundation.ConstCharCrcLowerString;
import io.bacta.shared.foundation.CrcLowerString;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by crush on 4/22/2016.
 */
@Slf4j
@Service
public class ArrangementDescriptorList {
    private static final ConstCharCrcLowerString defaultFilename = new ConstCharCrcLowerString("arrangement/arrangement_none.iff");

    private final Map<CrcLowerString, ArrangementDescriptor> descriptors = new HashMap<>();
    private final TreeFile treeFile;
    private final SlotIdManager slotIdManager;

    @Inject
    public ArrangementDescriptorList(final TreeFile treeFile,
                                     final SlotIdManager slotIdManager) {
        this.treeFile = treeFile;
        this.slotIdManager = slotIdManager;
    }

    public ArrangementDescriptor fetch(final CrcLowerString filename) {
        ArrangementDescriptor descriptor = descriptors.get(filename);

        if (descriptor != null)
            return descriptor;

        final String filenameString = filename.getString();

        final byte[] fileBytes = treeFile.open(filenameString);

        if (fileBytes == null) {
            if (filename != defaultFilename) {
                LOGGER.warn("ArrangementDescriptor file [{}] does not exist, using default.", filenameString);
                return fetch(defaultFilename);
            } else {
                LOGGER.warn("default ArrangementDescriptor [{}] could not be loaded, returning NULL ArrangementDescriptor.",
                        filenameString);
                return null;
            }
        }

        final Iff iff = new Iff(filenameString, fileBytes);

        descriptor = new ArrangementDescriptor(slotIdManager, iff, filename);

        descriptors.put(descriptor.getName(), descriptor);

        descriptor.fetch(); //bump up reference count.

        return descriptor;
    }

    public ArrangementDescriptor fetch(final String filename) {
        return fetch(new CrcLowerString(filename));
    }

    void stopTracking(final ArrangementDescriptor descriptor) {
        final CrcLowerString filename = descriptor.getName();
        descriptors.remove(filename);
    }
}
