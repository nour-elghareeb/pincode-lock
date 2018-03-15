package ne.pincodelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;

/**
 * PinLock RecycleView
 */

public class PinLockView extends RecyclerView implements PinCodeLockListener {
    private static final String TAG = PinLockView.class.getSimpleName();
    private DotRecyclerView pinDotView;
    private PinAdapter adapter;
    private int maxLength = 15;
    private int requiredLength = 4;
    protected TypedArray attrArray ;
    private SparseArray<Object> attrValues;
    private PinCodeLockListener listener;
    private static final String STATE_SUPER_CLASS = "super_class_state";
    private static final String STATE_MAX_LENGTH = "pin_max_length";
    private static final String STATE_PIN_VALUE = "pin_value";

    public PinLockView(Context context) {
        super(context);
        init();
        onFinishInflate();
    }
    public PinLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.PinLockView, 0, 0);
        init();
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.PinLockView, 0, 0);
        init();
    }

    /**
     * initialize component, gets called right after constructor.
     */
    private void init() {
        attrValues = new SparseArray<>();
        adapter = new PinAdapter(getContext());
        adapter.setMaxLength(maxLength);
        setLayoutManager(new LinearLayoutManager(getContext()));
        setHasFixedSize(true);
        adapter.setListener(this);
        adapter.setMaxLength(maxLength);
        setAdapter(adapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        applyAttrs();
    }

    /**
     * Apply attributes passed within xml
     */
    private void applyAttrs() {
        for(int i = 0; i < attrArray.getIndexCount(); i++) {
            int index = attrArray.getIndex(i);
            try {
                // Text size
                if(index == R.styleable.PinLockView_android_textSize)
                    attrValues.put(index, attrArray.getDimension(index, 18));
                // Text color
                else if (index == R.styleable.PinLockView_android_textColor)
                    attrValues.put(index, attrArray.getColor(index, getContext().getResources().getColor(R.color.pincodelock_text_color)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // notify adapter with the new attribute values
        adapter.setAttrValues(attrValues);
        // recycle the TypedArray.
        attrArray.recycle();
    }

    /**
     * Attach a DotView to represent current pin as dots..
     * @param pinDotView PinDotView instance
     */
    public void attachPinDotView(IndicatorDotView pinDotView) {
        // sets the pinDotView instance
        this.pinDotView = pinDotView.getRecycler();
    }

    /**
     * Set maximum maxLength of the required pin code.
     * @param maxLength int: maxLength of pin code
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        // notify adapter about the max length change
        adapter.setMaxLength(maxLength);
    }

    /**
     * An interface that connects the PinAdapter with the view to notify the view about pin change
     * @param pin String : current pin code
     */
    @Override
    public void onPinChange(String pin) {
        // if a DotView is attached, notify the pin change
        if (pinDotView != null){
            pinDotView.getAdapter().onPinChange(pin);
        }
        // if an external listener is attached, notify the pin change
        if (listener != null){
            listener.onPinChange(pin);
            if (requiredLength != 0 && pin.length() == requiredLength) listener.onPinReachRequired(pin);
            if (adapter.hasReachedMaxLength()) listener.onPinReachMax(pin);
        }
    }

    @Override
    public void onPinReachRequired(String pin) {

    }

    @Override
    public void onPinReachMax(String pin) {

    }

    @Override
    public void onPinReAttempt() {

    }

    public void setPinChangeListener(PinCodeLockListener listener) {
        this.listener = listener;
    }

    /**
     * Set pin code, may be used to restore state after savedInstance
     * @param pin string : pin value
     */
    public void setPin(String pin){
        adapter.setPin(pin);
    }

    /**
     * clears pin code
     */
    public void clear(){
        adapter.clearPin();
    }

    public void setRequiredLength(int requiredLength) {
        this.requiredLength = requiredLength;
    }
}
