package ne.pincodelock;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * An adapter for the PinLockView recycler
 */

class PinAdapter extends RecyclerView.Adapter implements PinAdapterInterface {
    // A string representation for logging
    private static final String TAG = PinAdapter.class.getSimpleName();
    // A list that holds Pin rows
    private PinRowModel[] models;
    // Max length for the pincode
    private int maxLength;
    private int minLength;
    // String builder for the pincode
    private StringBuilder pin;
    // Array that holds xml attributes.
    private Context context;
    // backspace counter to detect attempts
    private int consecutiveBackspace;
    private int maxConsecutiveBackspace = 2;
    private boolean clickable = true;
    private boolean isLayoutReady;

    @AnyThread
    private void runOnMainThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).postDelayed(runnable, (long) 0);
    }

    int getConsecutiveBackspace() {
        return consecutiveBackspace;
    }

    int getMaxConsecutiveBackspace(){
        return maxConsecutiveBackspace;
    }

    /**
     * <p>A constructor for the pin adapter</p>
     * Initialize the models - 4 rows.
     */
    PinAdapter(Context context) {
        this.context = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                models = new PinRowModel[]{
                        new PinRowModel(0),
                        new PinRowModel(1),
                        new PinRowModel(2),
                        new PinRowModel(3),
                };
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

                isLayoutReady = true;
            }
        }).start();

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
    private PinLockListener listener;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the view based on the view type
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        // if the viewtype was aint attemptNumber generic row
        if (viewType == R.layout.layout_viewholder__pin_row)
            return new PinRowViewHolder(view, this);
        // if the type was last row
        else if (viewType == R.layout.layout_viewholder__pin_last_row)
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
        if (models != null)
            return models.length;
        else return 0;
    }

    @Override
    public int getItemViewType(final int position) {
        // return the view of any other generic row.
        if (position < 3){
            return R.layout.layout_viewholder__pin_row;
        }
        // return the view of the last row
        else{
            return R.layout.layout_viewholder__pin_last_row;
        }
    }

    /**
     * Setter for the outside listener
     * @param listener Any class that implements PinLockListener
     */
    void setListener(PinLockListener listener) {
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
        if (!hasReachedMaxLength()) {
            consecutiveBackspace = 0;
            // get the backspace-allow state before adding the number
            boolean backspaceAllowedBefore = isBackspaceAllowed();
            // append the pin with the newly added number
            pin.append(number);
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
            consecutiveBackspace++;
            // delete the last number from pin
            pin.deleteCharAt(pin.length() - 1);
            // notify outside listener that pin code changed
            listener.onPinChange(pin.toString());
            if (consecutiveBackspace == maxConsecutiveBackspace) listener.onPinReAttempt(false, -1);
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

    }
    /**
     * <p>This gets called from within the ViewHolder instance to indicate backspace is pressed,
     * ACTION_DOWN</p>
     * <p>Stores the current state of whether the pin was max before the backspace firing</p>
     */
    @Override
    public void onBackspaceDown() {
    }

    /**
     * Checks if the pin has reached max length
     * @return true if pin length equals the max length allowed, false otherwise
     */
    @Override
    public boolean hasReachedMaxLength() {
        return pin.length() == maxLength; //maxLength;
    }

    /**
     * Checks if the backspace action is allowed
     * @return true if the pin is not empty, false otherwise
     */
    @Override
    public boolean isBackspaceAllowed() {
        return pin.length() > 0;
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    /**
     * Pin getter
     * @return pin value
     */
    public String getPin() {
        return pin.toString();
    }

    /**
     * Pin setter
     * @param _pin pin value
     */
    public void setPin(String _pin) {
        pin = new StringBuilder();
        pin.append(_pin);
        listener.onPinChange(pin.toString());
    }

    /**
     * Clear pin value
     */
    void clearPin() {
        pin = new StringBuilder();
        listener.onPinChange(pin.toString());
    }

    Context getContext() {
        return context;
    }


    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @AnyThread
    void setMaxConsecutiveBackspace(int maxConsecutiveBackspace) {
        this.maxConsecutiveBackspace = maxConsecutiveBackspace;
    }

    void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    void setConsecutiveBackspace(int consecutiveBackspace) {
        this.consecutiveBackspace = consecutiveBackspace;
    }
}
