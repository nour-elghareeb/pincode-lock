package ne.pincodelock;

import android.support.annotation.Nullable;

/**
 * An interface that connects the pin adapter to every other class that needs the current pin
 * number
 */

public interface PinLockListener {
    /**
     * Called when a pin length change occur
     * @param pin String: current pin
     */
    void onPinChange(String pin);

    /**
     * Called when pin code reaches pre-set required length to apply custom pin-verification
     * @param pin String: current pin
     * @see  PinLockRecycler#setRequiredLength(int)
     */
    boolean onPinReachRequiredLength(String pin);

    /**
     * Called when pin code's length reaches pre-set maximum value.

     * @param pin String: current pin
     * @return false to clear pin and start default error animation,
     * True to consume event in the listener
     * @see PinLockRecycler#setMaxLength(int)
     */
    boolean onPinReachMaxLength(String pin);
    /**
     * <p>Called when user re-attempt to enter PIN code and still has attempts left.</p>
     * <p>Attempts increase by either reaching the max pin length or hitting the pincodelock_backspace_icon the
     * pre-set max limit of consecutive touches</p>
     * @param fromClear true when the attempt was caused by max-length clear, false when a
     *                  consecutive pincodelock_backspace_icon limit was reached
     * @param attemptNumber current number of attempts
     * @return false to start default error animation, true to consume event in the listener
     */
    boolean onPinReAttempt(boolean fromClear, int attemptNumber);

    /**
     * Called when user reach the pre-set limit of attempts. Default is 30 seconds
     * @param attemptLimitReachedCount the number of times user reached limit.
     * @return false to disable input for the pre-set freeze duration.
     * @see PinLockRecycler#setFreezeDuration(int)
     */
    boolean onPinAttemptReachLimit(int attemptLimitReachedCount);

    /**
     * Called when user click on the extra pin (bottom-left)
     * @param currentMode current view mode
     * @param currentPin current pin
     * @param pinToBeConfirmed pin user chose that need to be confirmed (always null unless the mode
     *                         is {@link PinLockMode#CONFIRM})
     * @return false to apply default action, true to consume event in the listener
     */
    boolean onExtraPinClick(PinLockMode currentMode, String currentPin, @Nullable String pinToBeConfirmed);
    /**
     * Called whenever app user input is enabled/disabled when attempts run out.
     * @param isEnabled true if enabled, false if disabled
     */
    void onPinClickableStateChanged(boolean isEnabled);

    /**
     * Called whenever PinLockView mode changed internally or externally
     * @param mode current mode.
     * @return false to apply default action, true to consume event in the listener
     */
    boolean onPinModeChanged(PinLockMode mode);

    /**
     * Called when user clicks on any pin while the view is not clickable..
     */
    void onDisabledPinClick();

}
