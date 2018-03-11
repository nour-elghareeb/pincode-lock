package ne.pincodelock;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An adapter for the PinLockView recycler
 */

public class PinAdapter extends RecyclerView.Adapter implements PinAdapterInterface, ApplyAttrInterface {
    // A string representation for logging
    private static final String TAG = PinAdapter.class.getSimpleName();
    // A list that holds Pin rows
    private final PinRowModel[] models;
    // Max length for the pincode
    private int maxLength;
    // String builder for the pincode
    private StringBuilder pin;
    private boolean wasMaximumLengthBeforeTouchDown = false;
    // Array that holds xml attributes.
    private SparseArray<Object> attrValues;

    /**
     * <p>A constructor for the pin adapter</p>
     * Initialize the models - 4 rows.
     */
    PinAdapter() {
        models = new PinRowModel[]{
                new PinRowModel(0),
                new PinRowModel(1),
                new PinRowModel(2),
                new PinRowModel(3),
        };
        pin = new StringBuilder();
    }

    /**
     * Set the max length of the required pin code.
     *
     * @param maxLength int: max length required
     */
    void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * A pin code listener that reports back to the view..
     */
    private PinCodeLockListener listener;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the view based on the view type
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        // if the viewtype was a generic row
        if (viewType == R.layout.pin_row)
            return new PinRowViewHolder(view, this);
        // if the type was last row
        else if (viewType == R.layout.pin_last_row)
                return new PinLastRowViewHolder(view, this);
        // unreachable state..
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // if position is 3; ie, the last row
        if (position == 3) {
            ((PinLastRowViewHolder) holder).bindData(models[position]);
        }
        // any other row.
        else{
            ((PinRowViewHolder) holder).bindData(models[position]);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public int getItemViewType(final int position) {
        // return the view of any other generic row.
        if (position < 3){
            return R.layout.pin_row;
        }
        // return the view of the last row
        else{
            return R.layout.pin_last_row;
        }
    }

    /**
     * Setter for the outside listener
     * @param listener Any class that implements PinCodeLockListener
     */
    void setListener(PinCodeLockListener listener) {
        this.listener = listener;
    }

    /**
     * This gets called from within the ViewHolder instance to indicate a number is pressed
     * <p>Appends the pin with passed number and notify listener</p>
     * @param number String: the number user pressed
     */
    @Override
    public void onNumberAction(String number) {
        // make sure the pin hasn't reached max
        if (pin.length() < maxLength) {
            // get the backspace-allow state before adding the number
            boolean backspaceAllowedBefore = isBackspaceAllowed();
            // append the pin with the newly added number
            pin.append(number);
            // if the backspace-allow state was disabled before, notify the adapter to redraw the last
            // row to reflect that it is allowed now.
            if (!backspaceAllowedBefore) _notifyLastRowUpdated();
            // if pin reaches max now, notify adapter to redraw all pins to reflect that user input
            // is disabled now.
            else if(hasReachedMaxLength()) _notifyDataSetChanged();
            // notify outside listener that pin code changed
            listener.onPinChange(pin.toString());
        }
    }

    /**
     * <p>This gets called from within the ViewHolder instance to indicate backspace action is
     * fired</p>
     * <p>Remove the last number from pin and notify pin change</p>
     */
    @Override
    public void onBackspaceAction() {
        // make sure pin isn't empty
        if (pin.length() > 0) {
            // delete the last number from pin
            pin.deleteCharAt(pin.length() - 1);
            // notify outside listener that pin code changed
            listener.onPinChange(pin.toString());
        }
    }

    /**
     * <p>Gets called from within the last row ViewHolder instance to indicate user has lift his finger
     * of the backspace button, ACTION_UP</p>
     * <p>If the backspace-allow state changed between the DOWN and UP event, notify adapter to
     * redraw the last row</p>
     * <p>If the pin length was max before the touch DOWN, notify adapter to redraw the all pins
     * to re-enable the user input on numbers</p>
     */
    @Override
    public void onBackspaceUp() {
        // if
        if (wasMaximumLengthBeforeTouchDown) _notifyDataSetChanged();
        else if (!isBackspaceAllowed()) _notifyLastRowUpdated();
    }
    /**
     * <p>This gets called from within the ViewHolder instance to indicate backspace is pressed,
     * ACTION_DOWN</p>
     * <p>Stores the current state of whether the pin was max before the backspace firing</p>
     */
    @Override
    public void onBackspaceDown() {
        wasMaximumLengthBeforeTouchDown = hasReachedMaxLength();
    }

    /**
     * Checks if the pin has reached max length
     * @return true if pin length equals the max length allowed, false otherwise
     */
    @Override
    public boolean hasReachedMaxLength() {
        return pin.length() == maxLength;
    }

    /**
     * Checks if the backspace action is allowed
     * @return true if the pin is not empty, false otherwise
     */
    @Override
    public boolean isBackspaceAllowed() {
        return pin.length() > 0;
    }

    /**
     * Wrapper for {@link #notifyDataSetChanged()} that delays the action by 400ms in order not to
     * affect the ripple effect on pin view.
     */
    private void _notifyDataSetChanged(){
        new Handler().postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, 400);
    }
    /**
     * Wrapper for {@link #notifyItemChanged(int)} that delays the action by 400ms in order not to
     * affect the ripple effect on pin view.
     */
    private void _notifyLastRowUpdated(){
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(3);
                    }
                }, 400);
    }

    /**
     * Sets the attrs array that holds xml attributes, Called by the Recycler view.
     * @param attrValues xml attributes,
     */
    void setAttrValues(SparseArray<Object> attrValues) {
        this.attrValues = attrValues;
    }

    /**
     * Gets called from within the VierHolder to apply passed xml attributes on the views' inputs
     * @param views an Array of the ViewHolder children inputs.
     */
    @Override
    public void apply(View... views) {
        // make sure the Recycler has set the attributes
        if (attrValues != null) {
            // loop over the indices of the attribute array
            for (int i = 0; i < attrValues.size(); i++) {
                //get The key
                int key = attrValues.keyAt(i);
                // get the object by the key.
                Object obj = attrValues.get(key);
                // if key is textSize
                if(key == R.styleable.PinLockView_android_textSize){
                    // loop over passed views
                    for (View view: views)
                        try {
                            //Try cast the view as TextView and set the textSize
                            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, (Float) obj);
                        }catch (ClassCastException e){
                            // if the view isn't a TextView, then it is the backspace Image
                            // set the layoutparams of Linearlayout parent of the image to reflect
                            // about the same effect as the textView textSize.
                            try {
                                ImageView imageView = (ImageView) view;
                                imageView.getLayoutParams().height = (int) Math.round(((Float) obj) * 2.5);
                                imageView.requestLayout();
                            }catch (ClassCastException ignored){}
                        }
                    break;
                }
                // TextColor attribute
                else if (key == R.styleable.PinLockView_android_textColor){
                    // Loop over the views
                    for (View view: views)
                        // try casting to TextView and set textColor
                        try {
                            ((TextView) view).setTextColor((Integer) obj);
                        }
                        // if casting failed, then cast to an ImageView and setColorFilter.
                        catch (Exception e){
                            try {
                                ImageView imageView = (ImageView) view;
                                ((ImageView) view).setColorFilter((Integer) obj);
                                imageView.requestLayout();
                            }catch (ClassCastException ignored){}
                        }
                    break;
                }
            }
        };
    }
}
