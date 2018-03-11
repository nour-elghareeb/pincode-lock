package ne.pincodelock;

import android.view.View;

/**
 * An interface implemented by each Recycler's adapter to make it possible for the view holder
 * instance to update each and every input (edittext, imageview, etc..) according to the passed
 * attrs set to PinLockView, PinDotView
 */

interface ApplyAttrInterface {
    /**
     * Called from the ViewHolder instance to pass the views to the adapter to apply the attrs set
     * included in xml..
     * @param view ViewHolder input list.
     */
    void apply(View... view);
}
