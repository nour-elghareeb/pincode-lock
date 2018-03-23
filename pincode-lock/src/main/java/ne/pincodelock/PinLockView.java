package ne.pincodelock;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * ConstraintLayout container for the PinLockRecycler. Have a z-index view that blocks input whenever
 * Input is set to disabled.
 */

public class PinLockView extends ConstraintLayout implements PinLockClickableUpdate {
    private static final String TAG = PinLockView.class.getSimpleName();
    private PinLockRecycler pinLockRecycler;
    private View zIndexView;
    public PinLockView(Context context) {
        super(context);
        init();
    }

    public PinLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.pin_lock_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        pinLockRecycler = findViewById(R.id.pinLockRecycler);
        pinLockRecycler.setClickableListener(this);
        zIndexView = findViewById(R.id.zIndexView);

    }

    /**
     * Attach a DotView to represent current pin as dots..
     * @param pinDotView PinDotView instance
     */
    public void attachPinDotView(IndicatorDotView pinDotView) {
        // sets the pinDotRecycler instance
        pinLockRecycler.attachPinDotView(pinDotView);
    }

    public void setPinChangeListener(PinLockListener listener) {
        pinLockRecycler.setPinChangeListener(listener);
    }

    /**
     * Set pin code, may be used to restore state after savedInstance
     * @param pin string : pin value
     */
    public void setPin(String pin){
        pinLockRecycler.setPin(pin);
    }

    /**
     * <p>clears pin code</p>
     *
     * @param showErrorAnimation boolean: if true, display default incorrect-error-animation
     */
    public void clear(boolean showErrorAnimation){
        pinLockRecycler.clear(showErrorAnimation);
    }


    /**
     * Set max invalid attempts before calling {@link PinLockListener#onPinAttemptReachLimit(int)}
     * @param maxAttemptCount integer: max number of attempts allowed
     */
    public void setMaxNumberOfAttempts(int maxAttemptCount){
        pinLockRecycler.setMaxNumberOfAttempts(maxAttemptCount);
    }

    /**
     * Clear dotsView highlight color (error/success)
     */
    public void clearHighlight(){
        pinLockRecycler.clearHighlight();
    }
    /**
     * Set the max number of consecutive backspace to consider an attempt.
     * <p>Set to -1 to disable feature.</p>
     * @param count consecutive backspace count. Default is 2
     */
    public void setMaxConsecutiveBackspaceForAnAttempt(int count){
        pinLockRecycler.setMaxConsecutiveBackspaceForAnAttempt(count);
    }
    public void showSuccessAnimation(){
        pinLockRecycler.showSuccessAnimation();
    }
    public void showErrorAnimation(){
        pinLockRecycler.showErrorAnimation();
    }

    /**
     * Set maximum length the view can reach
     * @param maxLength int: max possible Length of a pin code
     */
    public void setMaxLength(int maxLength) {
        pinLockRecycler.setMaxLength(maxLength);
    }
    /**
     * <p>set actual user pin code length to get a callback when pin reaches that length through
     * {@link PinLockListener#onPinReachRequiredLength(String)}</p>
     * <p>If unspecified, you should either create a button for the user to verify pin or
     * verify pin each time {@link PinLockListener#onPinChange(String)} gets called</p>
     *
     * @param requiredLength actual pin code length
     */
    public void setRequiredLength(int requiredLength) {
        pinLockRecycler.setRequiredLength(requiredLength);
    }

    /**
     * Set a duration to freeze (disable) input when user run out of attempts.
     * @param freezeSeconds number of seconds to disable input for.
     */
    public void setFreezeDuration(int freezeSeconds){
        pinLockRecycler.setFreezeDuration(freezeSeconds);
    }
    /**
     * Change the default freeze input message.
     * @param freezeMsg String: message to display to the user when attempts run out.
     *                  Use one placeholder %s if you want to display countdown timer value in the
     *                  message
     */
    public void setFreezeMsg(String freezeMsg) {

        pinLockRecycler.setFreezeMsg(freezeMsg);
    }

    /**
     * set layout buttons clickable
     * @param isClickable true if clickable, false otherwise
     */
    @Override
    public void setClickable(boolean isClickable){
        super.setClickable(isClickable);
        zIndexView.setVisibility(isClickable ? GONE : VISIBLE);
    }
    /**
     * Set the over-scroll mode for this view. Valid over-scroll modes are
     * {@link View#OVER_SCROLL_ALWAYS} (default), {@link View#OVER_SCROLL_IF_CONTENT_SCROLLS}
     * (allow over-scrolling only if the view content is larger than the container),
     * or {@link View#OVER_SCROLL_NEVER}.
     *
     * Setting the over-scroll mode of a view will have an effect only if the
     * view is capable of scrolling.
     *
     * @param overScrollMode The new over-scroll mode for this view.
     */
    public void setOverScrollMode(int overScrollMode){
//        pinLockRecycler.setOverScrollMode(overScrollMode);
    }

    @Override
    public void isClickableUpdated(boolean isClickable) {
        setClickable(isClickable);
    }
}
