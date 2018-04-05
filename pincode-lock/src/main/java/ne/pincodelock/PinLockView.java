package ne.pincodelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.AnyThread;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * ConstraintLayout container for the PinLockRecycler. Have a z-index view that blocks input whenever
 * Input is set to disabled.
 */

public class PinLockView extends ConstraintLayout implements PinLockInternalListener {
    public static final int DEFAULT_CONSEQUTIVE_BACKSPACE = 2;
    public static final int DEFAULT_REQUIRED_LENGTH = -1;
    public static final int DEFAULT_MAX_ATTEMPTS = 5;
    public static final int DEFAULT_MAX_LENGTH = 15;
    public static final int DEFAULT_MIN_LENGTH = 4;
    public static final int DEFAULT_FREEZE_DURATION = 30;
    private static final PinLockMode DEFAULT_MODE = PinLockMode.VERIFY ;
    private static final String TAG = PinLockView.class.getSimpleName();
    static final String DEFAULT_FREEZE_MSG = "Try again in %s seconds";
    private PinLockRecycler pinLockRecycler;
    private View zIndexView;
    private LinearLayout extraPinLayout;
    private ImageView extraPinView;
    private boolean animationInProgress = false;
    private int extraResourceId;
    private int extraTintId;
    private int indicatorsViewId;
    private TypedArray attrArray;
    private boolean isRestored = false;
    private int extraPinWidth;

    public PinLockView(Context context) {
        super(context);
        init(null);
    }

    public PinLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PinLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_container__pin_view, this);
        if (attrs == null) return;
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.PinLockView, 0, 0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        pinLockRecycler = findViewById(R.id.pinLockRecycler);
        zIndexView = findViewById(R.id.zIndexView);
        extraPinLayout = findViewById(R.id.extraPinLayout);
        extraPinView = findViewById(R.id.extraPinView);
        extraPinView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onExtraPinClick();
            }
        });
        pinLockRecycler.setInternalListener(this);
    }


    /**
     * Detects listener if it was implemented by the parent fragment/activity context..
     */
    private void detectListener() {
        try{
            if (getContext() instanceof PinLockListener) {
                pinLockRecycler.setPinChangeListener((PinLockListener) getContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        detectListener();
        detectIndicatorView();
        if (!isRestored) {
            applyAttrs();

        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                extraPinLayout.getLayoutParams().height = pinLockRecycler.getMeasuredHeight() / 4;
                int pad = getResources().getDimensionPixelSize(R.dimen.pincodelock_pin_padding);
                extraPinView.setPadding(pad, pad, pad, pad);
                extraPinView.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.pincodelock_pin_textsize) + pad*2;
                extraPinView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.pincodelock_pin_textsize) + pad*2;

            }
        }, 100);
    }

    /**
     * Attaches indicator view by Id
     * @param id view Id
     */
    private void attachIndicatorsView(int id){
        try {
            attachIndicatorsView((IndicatorDotView) getRootView().findViewById(id));
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            detectIndicatorView();
        }
    }

    /**
     * Detect indicator view by its tag..
     */
    private void detectIndicatorView(){
        IndicatorDotView view = getRootView().findViewWithTag(IndicatorDotView.class.getSimpleName());
        if (view != null){
            indicatorsViewId = view.getId();
            attachIndicatorsView(view);
        }
    }

    private void applyAttrs(){
        if (attrArray == null) return;
        int index = R.styleable.PinLockView_indicatorView;
        if (attrArray.hasValue(index)){
            indicatorsViewId = attrArray.getResourceId(index, 0);
            pinLockRecycler.attachPinDotView((IndicatorDotView) getRootView().findViewById(indicatorsViewId));
        }
        index = R.styleable.PinLockView_mode;
        if(attrArray.hasValue(index)){
            pinLockRecycler.setMode(PinLockMode.values()[attrArray.getInt(index, DEFAULT_MODE.ordinal())]);
        }
        index = R.styleable.PinLockView_freezeDuration;
        if(attrArray.hasValue(index)) {
            setFreezeDuration(attrArray.getInt(index, DEFAULT_FREEZE_DURATION));
        }
        index = R.styleable.PinLockView_maxAttempts;
        if(attrArray.hasValue(index)) {
            setMaxNumberOfAttempts(attrArray.getInt(index, DEFAULT_MAX_ATTEMPTS));
        }
        index = R.styleable.PinLockView_maxConsecutiveBackspace;
        if(attrArray.hasValue(index)) {
            setMaxConsecutiveBackspaceForAnAttempt(attrArray.getInt(index, DEFAULT_CONSEQUTIVE_BACKSPACE));
        }
        index = R.styleable.PinLockView_maxLength;
        if(attrArray.hasValue(index)) {
            setMaxLength(attrArray.getInt(index, DEFAULT_MAX_LENGTH));
        }
        index = R.styleable.PinLockView_minLength;
        if(attrArray.hasValue(index)) {
            setMinLength(attrArray.getInt(index, DEFAULT_MIN_LENGTH));
        }
        index = R.styleable.PinLockView_requiredLength;
        if(attrArray.hasValue(index)) {
            setRequiredLength(attrArray.getInt(index, DEFAULT_REQUIRED_LENGTH));
        }
        index = R.styleable.PinLockView_freezeMsg;
        if(attrArray.hasValue(index)) {
            setFreezeMsg(attrArray.getString(index));
        }
        attrArray.recycle();
    }
    /**
     * Attach a DotView to represent current pin as dots if not detected automatically..
     * @param pinDotView PinDotView instance
     */
    @UiThread
    public void attachIndicatorsView(IndicatorDotView pinDotView) {
        // sets the IndicatorDotView instance
        pinLockRecycler.attachPinDotView(pinDotView);
    }
    @AnyThread
    public void setPinChangeListener(PinLockListener listener) {
        pinLockRecycler.setPinChangeListener(listener);
    }

    /**
     * Set pin code, may be used to restore state after savedInstance
     * @param pin string : pin value
     */
    @UiThread
    public void setPin(String pin){
        pinLockRecycler.setPin(pin);
    }

    /**
     * <p>clears pin code</p>
     *
     * @param showErrorAnimation boolean: if true, display default incorrect-error-animation
     */
    @UiThread
    public void clear(boolean showErrorAnimation){
        pinLockRecycler.clear(showErrorAnimation);
    }


    /**
     * Set max invalid attempts before calling {@link PinLockListener#onPinAttemptReachLimit(int)}
     * @param maxAttemptCount integer: max number of attempts allowed
     */
    @AnyThread
    public void setMaxNumberOfAttempts(int maxAttemptCount){
        pinLockRecycler.setMaxNumberOfAttempts(maxAttemptCount);
    }

    /**
     * Clear dotsView highlight color (error/success)
     */
    @UiThread
    public void clearHighlight(){
        pinLockRecycler.clearHighlight();
    }
    /**
     * Set the max number of consecutive pincodelock_backspace_icon to consider an attempt.
     * <p>Set to -1 to disable feature.</p>
     * @param count consecutive pincodelock_backspace_icon count. Default is 2
     */
    @AnyThread
    public void setMaxConsecutiveBackspaceForAnAttempt(int count){
        pinLockRecycler.setMaxConsecutiveBackspaceForAnAttempt(count);
    }

    /**
     * Show success highlight animation
     */
    @UiThread
    public void showSuccessAnimation(){
        pinLockRecycler.showSuccessAnimation();
    }

    /**
     * Start error animation
     */
    @UiThread
    public void showErrorAnimation(){
        pinLockRecycler.showErrorAnimation();
    }

    /**
     * Set maximum length the view can reach
     * @param maxLength int: max possible Length of a pin code
     */
    @AnyThread
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
    @AnyThread
    public void setRequiredLength(int requiredLength) {
        pinLockRecycler.setRequiredLength(requiredLength);
    }

    /**
     * Set a duration to freeze (disable) input when user run out of attempts.
     * @param freezeSeconds number of seconds to disable input for.
     */
    @AnyThread
    public void setFreezeDuration(int freezeSeconds){
        pinLockRecycler.setFreezeDuration(freezeSeconds);
    }
    /**
     * Change the default freeze input message.
     * @param freezeMsg String: message to display to the user when attempts run out.
     *                  Use one placeholder %s if you want to display countdown timer value in the
     *                  message
     */
    @AnyThread
    public void setFreezeMsg(String freezeMsg) {
        pinLockRecycler.setFreezeMsg(freezeMsg);
    }

    /**
     * set layout buttons clickable. This does not affect the extra pin (bottom-left)
     * @param isClickable true if clickable, false otherwise
     */
    @Override
    @UiThread
    public void setClickable(boolean isClickable){
        super.setClickable(isClickable);
        zIndexView.setVisibility(isClickable ? GONE : VISIBLE);
        zIndexView.setOnClickListener(isClickable ? null : new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinLockRecycler.onDisabledPinClick();
            }
        });
    }

    @Override
    public boolean isClickable() {
        return zIndexView.getVisibility() == GONE;
    }

    @Override
    @UiThread
    public void setOverScrollMode(int overScrollMode){
        super.setOverScrollMode(overScrollMode);
        if(pinLockRecycler != null)
            pinLockRecycler.setOverScrollMode(overScrollMode);
    }
    public void onExtraPinClick(){

        if (pinLockRecycler.onExtraPinClick(null, null, null)) return;
        switch(pinLockRecycler.getMode()){
            case CONFIRM: case CONFIRMED:
                pinLockRecycler.setMode(PinLockMode.CREATE);
                break;
            case CREATE:
                if (pinLockRecycler.getPin()[0] != null && !pinLockRecycler.getPin()[0].isEmpty())
                    pinLockRecycler.setMode(PinLockMode.CONFIRM);
                break;
            case VERIFY:
                break;
        }
    }

    @Override
    @UiThread
    public void updateClickableStatus(boolean isClickable) {
        setClickable(isClickable);
    }

    @Override
    @UiThread
    public void updateExtraPinLayout(final boolean setVisible, final int resourceId, final int colorId) {
        Drawable drawable = null;
        if (animationInProgress) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateExtraPinLayout(setVisible, resourceId, colorId);
                }
            }, 20);
            return;
        }
        animationInProgress = true;

        if (resourceId != 0) {
            this.extraResourceId = resourceId;
            drawable = getResources().getDrawable(resourceId);
        }
        if (colorId != 0) this.extraTintId = colorId;
        int currentVisibility = extraPinLayout.getVisibility();
        Animation animation = null;
        if (setVisible && currentVisibility == GONE){
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_left);
        }else if (!setVisible && currentVisibility == VISIBLE){
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        }



        if (animation == null){
            if(resourceId != 0){
                extraPinView.setImageDrawable(drawable);
            }
            if(colorId != 0) extraPinView.setColorFilter(getContext().getResources().getColor(colorId), PorterDuff.Mode.SRC_IN);
            animationInProgress = false;
            return;
        }
        final Drawable finalDrawable = drawable;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(setVisible){
                    if(resourceId != 0) extraPinView.setImageDrawable(finalDrawable);
                    if(colorId != 0) extraPinView.setColorFilter(getContext().getResources()
                            .getColor(colorId), PorterDuff.Mode.SRC_IN);
                    extraPinLayout.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!setVisible){
                    if(resourceId != 0) extraPinView.setImageDrawable(finalDrawable);
                    if(colorId != 0) extraPinView.setColorFilter(getContext().getResources()
                            .getColor(colorId), PorterDuff.Mode.SRC_IN);
                    extraPinLayout.setVisibility(GONE);

                }
                animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        extraPinLayout.startAnimation(animation);
    }

    /**
     * <p>Change the extra PIN default drawable's id and color of its tint for each mode.</p>
     * <p><b>Must be called before {@link #setMode(PinLockMode mode)}</b></p>
     * @param mode current mode to alter
     * @param resourceId resource id of the drawable. set it to 0 to leave it as is
     * @param colorId color id for the resource. set it to 0 to leave it as is
     */
    @AnyThread
    public void setDefaultExtraPinLayout(PinLockMode mode, int resourceId, int colorId){
        pinLockRecycler.setDefaultExtraPinLayout(mode, resourceId, colorId);
    }

    /**
     * <p>Set the mode of view.</p>
     * <p><b>Don't call this method after setting any custom view options
     * such as {@link #setClickable(boolean)}, {@link #setMaxNumberOfAttempts(int)}, etc..
     * as it will override them. </b></p>
     * You can override the default behaviour for any mode by making sure that
     * {@link PinLockListener#onPinModeChanged(PinLockMode)}
     * returns <b>true</b>
     * @param mode current mode to set
     */
    @UiThread
    public void setMode(PinLockMode mode) {
        pinLockRecycler.setMode(mode);
    }

    /**
     * set minimum length of required PIN
     * @param minLength minimum PIN length
     */
    @AnyThread
    public void setMinLength(int minLength) {
        pinLockRecycler.setMinLength(minLength);
    }
    private static final String STATE_SUPER_PARCELABLE = "state_super";
    private static final String STATE_IS_CLICKABLE = "state_is_clickable";
    private static final String STATE_EXTRA_VISIBLE = "state_is_extra_visible";
    private static final String STATE_EXTRA_RESOURCE_ID = "state_extra_resrouceId";
    private static final String STATE_EXTRA_RESOURCE_TINT_ID = "state_extra_tintId";
    private static final String STATE_INDICATORS_VIEW = "state_dots_view_id";

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        isRestored = true;
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_PARCELABLE));
            setClickable(bundle.getBoolean(STATE_IS_CLICKABLE));
            updateExtraPinLayout(bundle.getBoolean(STATE_EXTRA_VISIBLE),
                    bundle.getInt(STATE_EXTRA_RESOURCE_ID),
                    bundle.getInt(STATE_EXTRA_RESOURCE_TINT_ID));
            attachIndicatorsView(bundle.getInt(STATE_INDICATORS_VIEW, 0));
            detectListener();
        }else{
            super.onRestoreInstanceState(state);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_SUPER_PARCELABLE, super.onSaveInstanceState());
        bundle.putBoolean(STATE_IS_CLICKABLE, isClickable());
        bundle.putBoolean(STATE_EXTRA_VISIBLE, extraPinLayout.getVisibility() == VISIBLE);
        bundle.putInt(STATE_EXTRA_RESOURCE_TINT_ID, extraTintId);
        bundle.putInt(STATE_EXTRA_RESOURCE_ID, extraResourceId);
        bundle.putInt(STATE_INDICATORS_VIEW, indicatorsViewId);
        return bundle;
    }
    public PinLockMode getMode(){
        return pinLockRecycler.getMode();
    }
}
