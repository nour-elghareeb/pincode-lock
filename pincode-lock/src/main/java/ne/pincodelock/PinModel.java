package ne.pincodelock;

/**
 * A model that represents a pin input
 */

class PinModel {
    // value of the input
    private final String value;

    PinModel(int value){
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
}
