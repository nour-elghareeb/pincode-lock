package ne.pincodelock;

/**
 * PinLockView modes..
 */

public enum PinLockMode {
    /**
     * Denotes a mode for inserting new pin. Disables all input restriction and starts a process flow
     * That change the extra pin layout until the pin is confirmed
     */
    CREATE(R.drawable.check_icon, R.color.pincodelock_extra_icon_pin_color__create_mode),
    /**
     * Denotes a mode for confirming the new pin.
     */
    CONFIRM(R.drawable.reset_icon, R.color.pincodelock_extra_icon_pin_color__confirm_mode),
    /**
     * Denotes a mode when the user confirms the chosen PIN successfully
     */
    CONFIRMED(R.drawable.reset_icon, R.color.pincodelock_extra_icon_pin_color__confirmed_mode),
    /**
     * Denotes a verification mode in which all the default input restrictions are applied
     */
    VERIFY(0, R.color.pincodelock_extra_icon_pin_color__verify_mode);

    private int drawable;
    private int color;

    PinLockMode(int drawable, int color){
        this.drawable = drawable;
        this.color = color;
    }

    void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    int getDrawable() {
        return drawable;
    }

    void setColor(int color) {
        this.color = color;
    }

    int getColor() {
        return color;
    }
}
