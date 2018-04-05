package ne.pincodelock;

/**
 * A model that represents the a row of pin inputs
 */

class PinRowModel {
    private static final String TAG = PinRowModel.class.getSimpleName();
    // Left pin model
    private PinModel left;
    // Center pin model
    private PinModel right;
    // right pin model
    private PinModel center;
    // position of the row (0,1,2,3)
    private int position;
    PinRowModel(int position) {
        //set the position
        this.position = position;
        // generate pins for the row
        generatePins();
    }

    /**
     * Generate pin models for {@link #left}, {@link #center} and {@link #right} models
     */
    private void generatePins(){
        // calculate the start of the row value
        int rowStartValue = (this.position*3) + 1;
        // switch row starting value
        switch (rowStartValue){
            // any other row besides the last one
            case 1:case 4: case 7:
                left = new PinModel(rowStartValue);
                center = new PinModel(rowStartValue+1);
                right = new PinModel(rowStartValue+2);
                break;
            // the last row that holds the pincodelock_backspace_icon.
            case 10:
                left = new PinModel(-1);
                center = new PinModel(0);
                right = new PinModel(R.drawable.pincodelock_backspace_icon);
                break;
        }
    }

    /**
     * getter for the left pin model
     * @return pin model instance
     */
    PinModel getLeft() {
        return left;
    }

    /**
     * getter for the center pin model
     * @return pin model instance
     */
    PinModel getRight() {
        return right;
    }

    /**
     * getter for the right pin model
     * @return pin model instance
     */
    PinModel getCenter() {
        return center;
    }

    /**
     * A string representation for the class - for debugging usage.
     * @return string : left, center and right values..
     */
    @Override
    public String toString() {
        return String.format("left is %s\ncenter is %s\nright is %s", left.getValue(), center.getValue(), right.getValue());
    }
}
