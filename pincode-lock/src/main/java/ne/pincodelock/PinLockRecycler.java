package ne.pincodelock;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.AnyThread;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * PinLock RecycleView
 */

class PinLockRecycler extends RecyclerView implements PinLockListener {
    private static final String TAG = PinLockRecycler.class.getSimpleName();

    private DotRecyclerView pinDotRecycler;
    private PinAdapter adapter;
    private int maxLength = PinLockView.DEFAULT_MAX_LENGTH;
    private int requiredLength = PinLockView.DEFAULT_REQUIRED_LENGTH;
    private int maxAttemptCount = PinLockView.DEFAULT_MAX_ATTEMPTS;
    private int minLength = PinLockView.DEFAULT_MIN_LENGTH;
    private long freezeDuration = 30000L;
    private PinLockListener listener;
    private boolean errorAnimated = false;
    private int attempts;
    private int freezeCount;
    private String freezeMsg = PinLockView.DEFAULT_FREEZE_MSG;
    private TextView pinDotMsgView;
    private boolean countDownInProgress = false;
    private PinLockInternalListener internalListener;
    private PinLockMode mode = PinLockMode.VERIFY;
    private String[] pin = new String[2];
    private boolean canScrollVertically;
    private boolean succesHighlight;

    public PinLockRecycler(Context context) {
        super(context);
        onFinishInflate();
    }
    public PinLockRecycler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PinLockRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public boolean canScrollVertically(){
        return canScrollVertically;
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setTag(getClass().getSimpleName());
        adapter = new PinAdapter(getContext());
        adapter.setMaxLength(maxLength);
        adapter.setMinLength(minLength);
        setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return PinLockRecycler.this.canScrollVertically();
            }
        });
        setHasFixedSize(true);
        adapter.setListener(this);
        adapter.setMaxLength(maxLength);
        setAdapter(adapter);
        setOverScrollMode(OVER_SCROLL_NEVER);

    }
    /**
     * Attach a DotView to represent current pin as dots if not detected automatically..
     * @param pinDotView PinDotView instance
     */
    @AnyThread
    public void attachPinDotView(IndicatorDotView pinDotView) {
        // sets the pinDotRecycler instance
        this.pinDotRecycler = pinDotView.getRecycler();
        this.pinDotMsgView = pinDotView.getDotMsgView();
    }


    protected void setMode(PinLockMode mode) {
        this.mode = mode;
        if(listener != null && onPinModeChanged(mode)) return;
        clearHighlight();
        internalListener.updateClickableStatus(false);
        switch(mode){
            case CREATE:
                setMaxConsecutiveBackspaceForAnAttempt(-1);
                setRequiredLength(-1);
                setMaxNumberOfAttempts(-1);
                setMaxConsecutiveBackspaceForAnAttempt(-1);
                clear(false);
                internalListener.updateExtraPinLayout(false, mode.getDrawable(),
                        mode.getColor());
                pin[1] = null;
                internalListener.updateClickableStatus(true);
                break;
            case CONFIRM:
                pin[1] = pin[0];
                setRequiredLength(pin[1].length());
                clear(false);
                internalListener.updateExtraPinLayout(true, mode.getDrawable(),
                        mode.getColor());
                internalListener.updateClickableStatus(true);
                break;
            case CONFIRMED:
                pin[1] = null;
                if (pin[0] == null || pin[0].length() == 0){
                    if (requiredLength != -1)
                        setPin(new String(new char[requiredLength ]).replace("\0",
                                "0"));
                }
                internalListener.updateExtraPinLayout(true, mode.getDrawable(),
                        mode.getColor());
                showSuccessAnimation();
                break;
            case VERIFY:
                internalListener.updateClickableStatus(true);
                internalListener.updateExtraPinLayout(false, mode.getDrawable(),
                        mode.getColor());
                setMaxConsecutiveBackspaceForAnAttempt(PinLockView.DEFAULT_CONSEQUTIVE_BACKSPACE);
                setRequiredLength(PinLockView.DEFAULT_REQUIRED_LENGTH);
                setMaxNumberOfAttempts(PinLockView.DEFAULT_MAX_ATTEMPTS);
                setMaxLength(PinLockView.DEFAULT_MAX_LENGTH);
                setPin("");

                break;
        }

    }


    /**
     * Set maximum length the view can reach.
     * @param maxLength int: max possible Length of a pin code
     */
    @AnyThread
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
        this.pin[0] = pin;
        errorAnimated = false;
        if (pinDotRecycler != null){
            pinDotRecycler.getAdapter().onPinLengthChange(pin.length());
        }
        // if an external listener is attached, notify the pin change
        if (listener != null){
            listener.onPinChange(pin);
            if (requiredLength != 0 && pin.length() == requiredLength) onPinReachRequiredLength(pin);
            if (adapter.hasReachedMaxLength()) onPinReachMaxLength(pin);
        }

        if(mode == PinLockMode.CREATE){
            internalListener.updateExtraPinLayout(pin.length() >= minLength, 0,0);
        }
    }

    @Override
    public boolean onPinReachRequiredLength(String pin) {
        if (listener != null && listener.onPinReachRequiredLength(pin)) return true;
        if (mode == PinLockMode.CONFIRM){
            // pin match
            if(!(pin == null || this.pin[1] == null || pin.isEmpty()) && pin.equals(this.pin[1])){
                setMode(PinLockMode.CONFIRMED);
            }
        }
        return false;
    }

    @Override
    public boolean onPinReachMaxLength(String pin) {
        if(listener != null){
            if (!listener.onPinReachMaxLength(pin)){
                if (mode == PinLockMode.VERIFY) {
                    clear(true);
                    errorAnimated = true;
                    onPinReAttempt(true, -1);
                }
            }
        }
        return false;
    }
    /**
     * Clear dotsView highlight color (error/success)
     */
    public void clearHighlight(){
        if (pinDotRecycler != null) {
            ((IndicatorDotView) pinDotRecycler.getParent()).setBackgroundDrawable(getResources().getDrawable(R.drawable.pincodelock_container_dot));
            succesHighlight = false;
        }
    }

    @Override
    public boolean onPinReAttempt(boolean fromClear, int attemptNumber) {
        attempts++;
        if (maxAttemptCount != -1 && attempts >= maxAttemptCount){
            freezeCount++;
            onPinAttemptReachLimit(freezeCount);
            return false;
        }

        if (listener != null) {
            if (!listener.onPinReAttempt(fromClear, attempts)){
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
                internalListener.updateClickableStatus(false);
                onPinClickableStateChanged(false);
                pinDotMsgView.setVisibility(VISIBLE);
                countDownInProgress = true;
                new CountDownTimer(freezeDuration + 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        pinDotMsgView.setText(String.format(freezeMsg, millisUntilFinished/1000));
                    }

                    public void onFinish() {
                        pinDotMsgView.setText("");
                        pinDotMsgView.setVisibility(GONE);
                        internalListener.updateClickableStatus(true);
                        onPinClickableStateChanged(true);
                        countDownInProgress = false;

                    }
                }.start();
            }
        }
        return false;
    }

    @Override
    public boolean onExtraPinClick(PinLockMode mode, String pin, @Nullable String pinToBeConfirmed) {
        return listener != null && listener.onExtraPinClick(this.mode, this.pin[0], this.pin[1]);
    }

    @Override
    public void onPinClickableStateChanged(boolean isEnabled) {
        if (listener != null){
            listener.onPinClickableStateChanged(isEnabled);
        }
    }

    @Override
    public boolean onPinModeChanged(PinLockMode mode) {
        return listener != null && listener.onPinModeChanged(mode);
    }

    @Override
    public void onDisabledPinClick() {
        if (listener != null){
            listener.onDisabledPinClick();

        }
    }

    /**
     * Set a duration to freeze (disable) input when user run out of attempts.
     * @param freezeSeconds number of seconds to disable input for.
     */
    @AnyThread
    public void setFreezeDuration(int freezeSeconds){
        this.freezeDuration = freezeSeconds * 1000L;
    }

    @AnyThread
    public void setPinChangeListener(PinLockListener listener) {
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
     * Change the extra PIN default drawable's id and color of its tint for each mode.
     * @param mode current mode to alter
     * @param resourceId resource id of the drawable
     * @param colorId color id for the resource
     */
    @AnyThread
    void setDefaultExtraPinLayout(PinLockMode mode, int resourceId, int colorId){
        if (resourceId != 0)
            mode.setDrawable(resourceId);
        if (colorId != 0)
            mode.setColor(colorId);
    }

    /**
     * Set max invalid attempts before calling {@link PinLockListener#onPinAttemptReachLimit(int)}
     * <p>Set it -1 to disable feature</p>
     * @param maxAttemptCount integer: max number of attempts allowed
     */
    @AnyThread
    public void setMaxNumberOfAttempts(int maxAttemptCount){
        this.maxAttemptCount = maxAttemptCount;
    }

    /**
     * Set the max number of consecutive pincodelock_backspace_icon to consider an attempt.
     * <p>Set to -1 to disable feature.</p>
     * @param count consecutive pincodelock_backspace_icon count. Default is 2
     */
    @AnyThread
    public void setMaxConsecutiveBackspaceForAnAttempt(int count){
        adapter.setMaxConsecutiveBackspace(count);
    }

    /**
     * show animation for success. Must be cleared with {@link #clearHighlight()}
     * or {@link #showErrorAnimation()}
     */
    public void showSuccessAnimation(){
        if (pinDotRecycler != null) {
            succesHighlight = true;
            final TransitionDrawable transition = (TransitionDrawable) getContext().getResources().getDrawable(R.drawable.pincodelock_container_dot_hightlight_success);
            ((IndicatorDotView) pinDotRecycler.getParent()).setBackgroundDrawable(transition);
            transition.startTransition(400);
        }
    }

    /**
     * Show error default animation highlight
     */
    @UiThread
    public void showErrorAnimation(){
        if (pinDotRecycler != null){
            succesHighlight = false;
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            final TransitionDrawable transition = (TransitionDrawable) getContext().getResources().getDrawable(R.drawable.pincodelock_container_dot_hightlight_error);
            ((IndicatorDotView) pinDotRecycler.getParent()).setBackgroundDrawable(transition);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    internalListener.updateClickableStatus(false);
                    transition.startTransition(400);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    transition.reverseTransition(400);
                    if (!countDownInProgress)
                        internalListener.updateClickableStatus(true);
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
     * {@link PinLockListener#onPinReachRequiredLength(String)}</p>
     * <p>If unspecified, you should either create a button for the user to verify pin or
     * verify pin each time {@link PinLockListener#onPinChange(String)} gets called</p>
     *
     * @param requiredLength actual pin code length
     */
    @AnyThread
    public void setRequiredLength(int requiredLength) {

        this.requiredLength = requiredLength;
    }

    /**
     * Change the default freeze input message.
     * @param freezeMsg String: message to display to the user when attempts run out.
     *                  Use one placeholder %s if you want to display countdown timer value in the
     *                  message
     */
    @AnyThread
    public void setFreezeMsg(String freezeMsg) {
        this.freezeMsg = freezeMsg;
    }

    public void setInternalListener(PinLockInternalListener internalListener) {
        this.internalListener = internalListener;
    }

    public PinLockMode getMode() {
        return mode;
    }

    public String[] getPin() {
        return pin;
    }

    @AnyThread
    public void setMinLength(int minLength) {
        this.minLength = minLength;
        adapter.setMinLength(minLength);
    }

    private static final String STATE_SUPER_PARCELABLE = "state_super";
    private static final String STATE_MIN_LENGTH = "state_minLength";
    private static final String STATE_MAX_LENGTH = "state_maxLength";
    private static final String STATE_REQUIRED_LENGTH = "state_requiredLength";
    private static final String STATE_MAX_ATTEMPTS = "state_maxAttempts";
    private static final String STATE_CURRENT_ATTEMPTS = "state_currentAttempts";
    private static final String STATE_MAX_CONSECUTIVE_BACKSPACE = "state_maxBackspace";
    private static final String STATE_CURRENT_BACKSPACE = "state_currentBackspace";
    private static final String STATE_PIN = "state_pin";
    private static final String STATE_MODE = "state_mode";
    private static final String STATE_FREEZE_DURATION = "state_freeze_duration";
    private static final String STATE_FREEZE_COUNT = "state_freeze_count";
    private static final String STATE_FREEZE_MSG = "state_freeze_msg";
    private static final String STATE_IS_SUCCESS_HIGHLIGHTED = "state_success_highlight";

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_PARCELABLE));
            setMinLength(bundle.getInt(STATE_MIN_LENGTH));
            setMaxLength(bundle.getInt(STATE_MAX_LENGTH));
            setRequiredLength(bundle.getInt(STATE_REQUIRED_LENGTH));
            setMaxNumberOfAttempts(bundle.getInt(STATE_MAX_ATTEMPTS));
            attempts = bundle.getInt(STATE_CURRENT_ATTEMPTS);
            setMaxConsecutiveBackspaceForAnAttempt(bundle.getInt(STATE_MAX_CONSECUTIVE_BACKSPACE));
            adapter.setConsecutiveBackspace(bundle.getInt(STATE_CURRENT_BACKSPACE));
            mode = PinLockMode.values()[bundle.getInt(STATE_MODE)];
            freezeCount = bundle.getInt(STATE_FREEZE_COUNT);
            freezeDuration = bundle.getLong(STATE_FREEZE_DURATION);
            setFreezeMsg(bundle.getString(STATE_FREEZE_MSG));
            pin = bundle.getStringArray(STATE_PIN);
            succesHighlight = bundle.getBoolean(STATE_IS_SUCCESS_HIGHLIGHTED);
            if (pin != null)
                setPin(pin[0]);
        }else{
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(STATE_SUPER_PARCELABLE, super.onSaveInstanceState());
        bundle.putInt(STATE_MIN_LENGTH, minLength);
        bundle.putInt(STATE_MAX_LENGTH, maxLength);
        bundle.putInt(STATE_REQUIRED_LENGTH, requiredLength);
        bundle.putInt(STATE_MAX_ATTEMPTS, maxAttemptCount);
        bundle.putInt(STATE_CURRENT_ATTEMPTS, attempts);
        bundle.putInt(STATE_MAX_CONSECUTIVE_BACKSPACE, adapter.getMaxConsecutiveBackspace());
        bundle.putInt(STATE_CURRENT_BACKSPACE, adapter.getConsecutiveBackspace());
        bundle.putInt(STATE_MODE, mode.ordinal());
        bundle.putInt(STATE_FREEZE_COUNT, freezeCount);
        bundle.putLong(STATE_FREEZE_DURATION, freezeDuration);
        bundle.putString(STATE_FREEZE_MSG, freezeMsg);
        bundle.putStringArray(STATE_PIN, pin);
        bundle.putBoolean(STATE_IS_SUCCESS_HIGHLIGHTED, succesHighlight);
        return bundle;
    }

}
