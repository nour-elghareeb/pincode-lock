package ne.pincodelock;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * PinLock RecycleView
 */

public class PinLockView extends RecyclerView implements PinCodeLockListener {
    private static final String TAG = PinLockView.class.getSimpleName();
    private DotRecyclerView pinDotRecycler;
    private PinAdapter adapter;
    private int maxLength = 15;
    private int requiredLength = 4;
    private PinCodeLockListener listener;
    private boolean errorAnimated = false;
    private int maxAttemptCount = 5;
    private int attempts;
    private long freezeDuration = 3000;
    private int freezeCount;
    private String freezeMsg = "Try again in %s seconds";
    private IndicatorDotView indicatorsContainer;
    private TextView pinDotMsgView;
    private boolean isFrozen;


    public PinLockView(Context context) {
        super(context);
        init();
        onFinishInflate();
    }
    public PinLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }



    /**
     * initialize component, gets called right after constructor.
     */
    private void init() {
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
    }

    /**
     * Attach a DotView to represent current pin as dots..
     * @param pinDotView PinDotView instance
     */
    public void attachPinDotView(IndicatorDotView pinDotView) {
        // sets the pinDotRecycler instance
        this.indicatorsContainer = pinDotView;
        this.pinDotRecycler = pinDotView.getRecycler();
        this.pinDotMsgView = pinDotView.getDotMsgView();
    }

    /**
     * Set maximum length the view can reach
     * @param maxLength int: max possible Length of a pin code
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
        errorAnimated = false;
        if (pinDotRecycler != null){
            pinDotRecycler.getAdapter().onPinChange(pin);
        }
        // if an external listener is attached, notify the pin change
        if (listener != null){
            listener.onPinChange(pin);
            if (requiredLength != 0 && pin.length() == requiredLength) listener.onPinReachRequiredLength(pin);
            if (adapter.hasReachedMaxLength()) onPinReachMaxLength(pin);
        }
    }

    @Override
    public void onPinReachRequiredLength(String pin) {

    }

    @Override
    public boolean onPinReachMaxLength(String pin) {
        if(listener != null){
            if (!listener.onPinReachMaxLength(pin)){
                clear(true);
                errorAnimated = true;
                onPinReAttempt(true);
            }
        }
        return false;
    }

    @Override
    public boolean onPinReAttempt(boolean fromClear) {
        attempts++;
        if (attempts > maxAttemptCount){
            freezeCount++;
            onPinAttemptReachLimit(freezeCount);
            return false;
        }
        if (listener != null) {
            if (!listener.onPinReAttempt(fromClear)){
                if (!errorAnimated) showErrorAnimation();
            }
        }
        return false;
    }

    @Override
    public boolean onPinAttemptReachLimit(int attemptLimitReachedCount) {
        attempts = 0;
        if (listener != null){
            if (!listener.onPinAttemptReachLimit(attemptLimitReachedCount)){
                clear(true);
                adapter.setClickable(false);
                onPinFreezeStateChanged(false);
                pinDotMsgView.setVisibility(VISIBLE);
                new CountDownTimer(freezeDuration + 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        pinDotMsgView.setText(String.format(freezeMsg, millisUntilFinished/1000));
                    }

                    public void onFinish() {
                        pinDotMsgView.setText("");
                        pinDotMsgView.setVisibility(GONE);
                        adapter.setClickable(true);
                        onPinFreezeStateChanged(true);

                    }
                }.start();
            }
        }
        return false;
    }

    @Override
    public void onPinFreezeStateChanged(boolean isEnabled) {
        isFrozen = !isEnabled;
        if (listener != null){
            listener.onPinFreezeStateChanged(isEnabled);
        }
    }

    /**
     * Set a duration to freeze (disable) input when user run out of attempts.
     * @param freezeSeconds number of seconds to disable input for.
     */
    public void setFreezeDuration(int freezeSeconds){
        this.freezeDuration = freezeSeconds * 1000L;
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
     * <p>clears pin code</p>
     *
     * @param showErrorAnimation boolean: if true, display default incorrect-error-animation
     */
    public void clear(boolean showErrorAnimation){
        adapter.clearPin();
        if (showErrorAnimation){
            showErrorAnimation();
        }
    }

    /**
     * Set max invalid attempts before calling {@link PinCodeLockListener#onPinAttemptReachLimit(int)}
     * @param maxAttemptCount integer: max number of attempts allowed
     */
    public void setMaxNumberOfAttempts(int maxAttemptCount){
        this.maxAttemptCount = maxAttemptCount;
    }

    /**
     * Set the max number of consecutive backspace to consider an attempt.
     * <p>Set to -1 to disable feature.</p>
     * @param count consecutive backspace count. Default is 2
     */
    public void setMaxConsecutiveBackspaceForAnAttempt(int count){
        adapter.setMaxConsecutiveBackspace(count);
    }
    public void showErrorAnimation(){
        if (pinDotRecycler != null){
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            final TransitionDrawable transition = (TransitionDrawable) getContext().getResources().getDrawable(R.drawable.dot_container_hightlight);
            ((IndicatorDotView) pinDotRecycler.getParent()).setBackgroundDrawable(transition);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    adapter.setClickable(false);
                    transition.startTransition(400);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    transition.reverseTransition(400);
                    adapter.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ((IndicatorDotView) pinDotRecycler.getParent()).startAnimation(anim);
        }
    }

    /**
     * <p>set actual user pin code length to get a callback when pin reaches that length through
     * {@link PinCodeLockListener#onPinReachRequiredLength(String)}</p>
     * <p>If unspecified, you should either create a button for the user to verify pin or
     * verify pin each time {@link PinCodeLockListener#onPinChange(String)} gets called</p>
     *
     * @param requiredLength actual pin code length
     */
    public void setRequiredLength(int requiredLength) {
        this.requiredLength = requiredLength;
    }

    /**
     * Change the default freeze input message.
     * @param freezeMsg String: message to display to the user when attempts run out.
     *                  Use one placeholder %s if you want to display countdown timer value in the
     *                  message
     */
    public void setFreezeMsg(String freezeMsg) {
        this.freezeMsg = freezeMsg;
    }
}
