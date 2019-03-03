package io.bacta.archive;

import io.bacta.shared.object.GameObject;

/**
 * Created by crush on 5/8/2016.
 */
public class OnDirtyCallback<T extends GameObject> implements OnDirtyCallbackBase {
    private T owner;
    private Runnable callback;

    public void set(final T owner, final Runnable callback) {
        this.owner = owner;
        this.callback = callback;
    }

    @Override
    public void onDirty() {
        if (owner != null && callback != null)
            callback.run();
    }
}
