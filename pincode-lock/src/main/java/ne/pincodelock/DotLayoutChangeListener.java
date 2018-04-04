package ne.pincodelock;

/**
 * interface to connect PinRecycler and DotAdapter to reflect pin changes on the dots..
 */

interface DotLayoutChangeListener {
    /**
     * Called from PinRecycler whenever pin length changes
     * @param newLength new pin length
     */
    void onPinLengthChange(int newLength);
}
