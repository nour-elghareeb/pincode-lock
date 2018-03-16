package ne.pincodelock;

/**
 * A dot model represents each pin number as dot
 */

class DotModel {
    private final int position;

    DotModel(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
