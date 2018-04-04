package ne.pincodelock;

import android.support.annotation.UiThread;

/**
 * Used to connect PinRecycler and PinLockView (container)
 */

interface PinLockInternalListener {
    /**
     * Don't use this directly, use {@link PinLockView#setClickable(boolean)} instead
     * @param isClickable true to set it clickable, false otherwise
     */
    @UiThread
    void updateClickableStatus(boolean isClickable);

    /**
     * Update the layout of extra pin
     * @param isVisible true to visible, false otherwise
     * @param resourceId drawable resource id, 0 to leave it as it is
     * @param colorId drawable tint, 0 to leave it as it is.
     */
    @UiThread
    void updateExtraPinLayout(boolean isVisible, int resourceId, int colorId);
}
