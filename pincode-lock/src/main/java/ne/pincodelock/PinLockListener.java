package ne.pincodelock;

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
    void onPinReachRequiredLength(String pin);

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
     * <p>Attempts increase by either reaching the max pin length or hitting the backspace the
     * pre-set max limit of consecutive touches</p>
     * @param fromClear true when the attempt was caused by max-length clear, false when a
     *                  consecutive backspace limit was reached
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
     * Called whenever app user input is enabled/disabled when attempts run out.
     * @param isEnabled true if enabled, false if disabled
     */
    void onPinFreezeStateChanged(boolean isEnabled);
}
