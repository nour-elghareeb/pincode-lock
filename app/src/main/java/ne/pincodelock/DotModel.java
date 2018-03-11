package ne.pincodelock;

/**
 * A dot model represents each pin number as dot
 */

class DotModel {
    private int position;
    private boolean enabled = false;

    DotModel(int position) {
        this.position = position;
    }

    boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPosition() {
        return position;
    }
}
