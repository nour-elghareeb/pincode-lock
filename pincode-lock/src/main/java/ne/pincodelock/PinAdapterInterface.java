package ne.pincodelock;

/**
 * An interface connects the PinViewAdapter with PinViewHolders
 */

interface PinAdapterInterface {
    void onNumberAction(String number);
    void onBackspaceAction();
    void onBackspaceUp();
    void onBackspaceDown();
    boolean hasReachedMaxLength();
    boolean isBackspaceAllowed();

    boolean isClickable();
}
