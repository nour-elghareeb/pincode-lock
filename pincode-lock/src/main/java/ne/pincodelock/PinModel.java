package ne.pincodelock;

/**
 * A model that represents a pin input
 */

class PinModel {
    // set an arbitrary id for the backspace
    private static int BACK_SPACE_BUTTON = -2;
    // value of the input
    private final String value;
    // input id
    private int id;

    PinModel(int value){
        // if value is bigger than *, then this is the backspace
        if (value > 9) {
            id = BACK_SPACE_BUTTON;
        }
        // less than 9, then set id = value
        else{
            this.id = value;
        }
        // set the input value
        this.value = String.valueOf(value);
    }

    /**
     * getter for the value
     * @return String input value
     */
    String getValue() {
        return value;
    }

    /**
     * getter for input id
     * @return int: input Id
     */
    public int getId() {
        return id;
    }
}
