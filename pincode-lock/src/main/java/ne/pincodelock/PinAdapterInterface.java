package ne.pincodelock;

/**
 * An interface connects the PinViewAdapter with PinViewHolders
 */

interface PinAdapterInterface {
    /**
     * Called when user clicks on any number on the PIN layout
     * @param number String:  clicked number
     */
    void onNumberAction(String number);

    /**
     * Called whenever a backspace firing..
     */
    void onBackspaceAction();

    /**
     * Called when user lift their finger off the backspace
     */
    void onBackspaceUp();

    /**
     * Called when user touches the backspace
     */
    void onBackspaceDown();

    /**
     * called to check if pin length reaches max value
     * @return true when reaches max value, false otherwise
     */
    boolean hasReachedMaxLength();

    /**
     * called to check if pin length > 0
     * @return true if pin length is > 0, false otherwise
     */
    boolean isBackspaceAllowed();

    /**
     * calls to check if user input is allowed
     * @return true if clickable, false otherwise
     */
    boolean isClickable();
}
