package io.bacta.swg.gcw;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public final class GcwScoreCategory {
    private final String categoryName;
    private final boolean regionDefender;
    private final boolean notifyOnPercentileChange;
    private final Set<String> scoreCategoryGroups;
}
