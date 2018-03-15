package ne.pincodelock;

/**
 * An interface that connects the pin adapter to every other class that needs the current pin
 * number
 */

public interface PinCodeLockListener {
    void onPinChange(String pin);
    void onPinReachRequired(String pin);
    void onPinReachMax(String pin);
    void onPinReAttempt();
}
