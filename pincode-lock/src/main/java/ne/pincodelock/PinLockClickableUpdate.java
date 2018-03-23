package ne.pincodelock;

/**
 * Used to connect PinRecycler and PinLayout because pinLayout (container) is the one controls
 * disabling/enabling input.
 */

interface PinLockClickableUpdate {
    void isClickableUpdated(boolean isClickable);

}
