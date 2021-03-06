# Pincode-Lock
Provides a customizable pin code lock screen (PIN input only) for android apps


## Features:
  * Customizable colors, backgrounds, sizes, etc...
  * Auto error handling animation.
  * Success animation highlight
  * Auto freeze input when maximum number of attempts is reached
  * Dot-view indicator
  * Customizations:    
    - Setting required length (actual user pin code length) to get a callback to start validation without letting on.
    - Setting maximum length that can be entered.
    - Setting Allowed attempts before freezing input.
    - Setting the duration in which the input will be disabled once the user run out of attempts.
    - Setting a message to show the user when they run out of attempts.
    - Setting the max consecutive backspace touches to mark as an attempt.
    - Customize size of view, text size, text color, dot background color, dot container border and backgrouynd color.
 
 ![Library screen view](https://github.com/nour-elghareeb/pincode-lock/blob/master/library_screen.gif?raw=true)

## Update V0.1.15
  1. PIN row height is now chaning dynamically to fill the screen size / 4
  2. It's probably better now to always put the PinLockView in a ScrollView layout to be able to fix
     height issue on landscape orientation 
  
## Update V0.1.134
  1. Added onSavedInstance and onRestoreInstance feature.
  2. IndicatorDots view is now detectable through findViewByTag() without having the need to 
   attach it manually
  3. PinCodeLockListener is now automatically detected if it is implemented
   in the activity/fragment (no need to setListener any more).
  4. Added library customization through XML
  5. Added setMode feature; it handles both verify and create pin processes
  6. Added customizable extra pin on the left-bottom of the view.
  7. Fixed minor bugs
  8. Fixed indicator dots animation
  9. Added dot size customization
 

## Download
To download the library, include this repository:
```gradle
repositories {
	// other repositories
	
	maven { url "https://dl.bintray.com/nour-elghareeb/ne" }
}
```
And compile library dependency (Check latest version from my [bintray repository](https://bintray.com/nour-elghareeb/ne/pincode-lock) ):
```gradle
dependencies {
	// other dependencies here 
	
	//recyclerview dependency
	implementation "com.android.support:recyclerview-v7:26.1.0"
	compile 'com.ne:pincode-lock:0.1.15'
}
```
## Usage
Set up your xml layout:
```xml
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto" >
	<ne.pincodelock.IndicatorDotView
		android:id="@+id/dotsView"
		android:layout_width="0dp"
		android:layout_height="wrap_content"
		app:layout_constraintTop_toTopOf="parent"
		app:layout_constraintStart_toStartOf="parent"
		app:layout_constraintEnd_toEndOf="parent"
		app:layout_constraintBottom_toTopOf="@id/pinLockView"		
		android:layout_marginStart="10dp"
		android:layout_marginEnd="10dp"
		
	/>
	<ne.pincodelock.PinLockView
		android:id="@+id/pinLockView"
		android:layout_width="0dp"
		android:layout_height="wrap_content"
		android:layout_marginTop="20dp"
		app:layout_constraintTop_toTopOf="@id/dotsView"
		app:layout_constraintBottom_toBottomOf="parent"
		app:layout_constraintStart_toStartOf="parent"
		app:layout_constraintEnd_toEndOf="parent"
		app:mode="verify"
		app:maxLength="10"
        app:requiredLength="4"
        app:maxConsecutiveBackspace="2"
        app:maxAttempts="4"
        app:minLength="3"
	/>
</android.support.constraint.ConstraintLayout>

```

In java activity/fragment:
```java

public class MainActivity extends AppCompatActivity implements PinLockListener {
    @Override
    public onCreate(savedInstanceState Bundle) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Bundle != null){
            // Assign Dots view
            IndicatorDotView dotsView = findViewById(R.id.dotsView);
            // Assign PinLockView
            PinLockView pinLockView = findViewById(R.id.pinLockView);
            
            // Attach dots view to pinlockview
            pinLockView.attachPinDotView(dotsView);
            
            // Maximum length the user can input (default is 15)
            pinLockView.setMaxLength(15);
            
            // Actual required pincode length (default is 4)
            pinLockView.setRequiredLength(4);
            
            // input freeze duration is seconds when the user run out of attempts. (default is 30 seconds)
            pinLockView.setFreezeDuration(30);
            
            // Maximum number of attempts before freezing input (default is 5)
            pinLockView.setMaxNumberOfAttempts(5);
            
            // Maximum consecutive number of backspace touches to consider as an attempt (default is 2)
            pinLockView.setMaxConsecutiveBackspaceForAnAttempt(2);
            
            // set pin progromatically. Useful if you want to restore pin after onRestoreInstance 
            pinLockView.setPin("1234");
            
            // Make your activity/fragment implement a PinCodeLockListener or create a new instance and set a listener.
            pinLockView.setPinChangeListener(this);
            
            // Setting mode
            pinLockView.setMode(PinLockMode.VERIFY);
        }
        
    }
}
// Make sure to implement this listener
public interface PinLockListener {
 /**
     * Called when a pin length change occur
     * @param pin String: current pin
     */
    void onPinChange(String pin);

    /**
     * Called when pin code reaches pre-set required length to apply custom pin-verification
     * @param pin String: current pin
     */
    boolean onPinReachRequiredLength(String pin);

    /**
     * Called when pin code's length reaches pre-set maximum value.

     * @param pin String: current pin
     * @return false to clear pin and start default error animation,
     * True to consume event in the listener
     */
    boolean onPinReachMaxLength(String pin);
    /**
     * <p>Called when user re-attempt to enter PIN code and still has attempts left.</p>
     * <p>Attempts increase by either reaching the max pin length or hitting the pincodelock_backspace_icon the
     * pre-set max limit of consecutive touches</p>
     * @param fromClear true when the attempt was caused by max-length clear, false when a
     *                  consecutive pincodelock_backspace_icon limit was reached
     * @param attemptNumber current number of attempts
     * @return false to start default error animation, true to consume event in the listener
     */
    boolean onPinReAttempt(boolean fromClear, int attemptNumber);

    /**
     * Called when user reach the pre-set limit of attempts. Default is 30 seconds
     * @param attemptLimitReachedCount the number of times user reached limit.
     * @return false to disable input for the pre-set freeze duration.
     */
    boolean onPinAttemptReachLimit(int attemptLimitReachedCount);

    /**
     * Called when user click on the extra pin (bottom-left)
     * @param currentMode current view mode
     * @param currentPin current pin
     * @param pinToBeConfirmed pin user chose that need to be confirmed (always null unless the mode
     *                         is #CONFIRMED
     * @return false to apply default action, true to consume event in the listener
     */
    boolean onExtraPinClick(PinLockMode currentMode, String currentPin, @Nullable String pinToBeConfirmed);
    /**
     * Called whenever app user input is enabled/disabled when attempts run out.
     * @param isEnabled true if enabled, false if disabled
     */
    void onPinClickableStateChanged(boolean isEnabled);

    /**
     * Called whenever PinLockView mode changed internally or externally
     * @param mode current mode.
     * @return false to apply default action, true to consume event in the listener
     */
    boolean onPinModeChanged(PinLockMode mode);

    /**
     * Called when user clicks on any pin while the view is not clickable..
     */
    void onDisabledPinClick();   
}

```

Library customization:

To customize library copy these values to its appropriate resources files and update them as you like.
```xml
<!-- colors.xml -->
<resources>
   <!-- pin layout text color -->
       <color name="pincodelock_pin_color">#333333</color>
       <!-- pin layout click ripple effect color -->
       <color name="pincodelock_rippleColor">#CCCCCC</color>
       <!-- dots view container border color -->
       <color name="pincodelock_dotContainer_border">#F0F0F0</color>
       <!-- dots view container background color -->
       <color name="pincodelock_dotContainer_background">#FCFCFC</color>
       <!-- dots view container border color when animation error occurr -->
       <color name="pincodelock_dotContainer_border_error">#C00000</color>
       <!-- dots view container background color when animation error occurr -->
       <color name="pincodelock_dotContainer_background_error">#FFFFFF</color>
       <!-- dots view container border color when animation error occurr -->
       <color name="pincodelock_dotContainer_border_success">#00A000</color>
       <!-- dots view container background color when animation success occur -->
       <color name="pincodelock_dotContainer_background_success">#FFFFFF</color>
       <!-- actual dot color -->
       <color name="pincodelock_dot_color">#666666</color>
       <!-- message shown when the layout is frozen text color color -->
       <color name="pincodelock_freeze_msg_color">#666666</color>
       <!--Background on PinLockView when is clickable-->
       <color name="pincodeLock_pin_background_enabled">#FFFFFFFF</color>
       <!--Background on PinLockView when is not clickable-->
       <color name="pincodeLock_pin_background_disabled">#FFFFFFFF</color>
       <!-- extra pin color when in CREATE mode -->
       <color name="pincodelock_extra_icon_pin_color__create_mode">@color/pincodelock_text_color</color>
       <!-- extra pin color when in CONFIRM mode -->
       <color name="pincodelock_extra_icon_pin_color__confirm_mode">@color/pincodelock_text_color</color>
       <!-- extra pin color when in CONFIRMED mode -->
       <color name="pincodelock_extra_icon_pin_color__confirmed_mode">#C00000</color>
       <!-- extra pin color when in VERIFY mode -->
       <color name="pincodelock_extra_icon_pin_color__verify_mode">@color/pincodelock_text_color</color>
       
    
    <!-- dimens.xml -->
    
    <!-- message shown when the layout is frozen text size -->
        <dimen name="pincodelock_freeze_msg_textsize">15sp</dimen>
        <!-- pin layout text size -->
        <dimen name="pincodelock_pin_textsize">25sp</dimen>
        <!-- pin layout row minimum height -->
        <dimen name="pincodelock_pinRow_minHeight">60dp</dimen>
        <!-- pin layout row maximum height -->
        <dimen name="pincodelock_pinRow_maxHeight">80dp</dimen>
        <!-- pin layout row horizontal margin -->
        <dimen name="pincodelock_pinRow_marginHorizontal">25dp</dimen>
        <!-- pin layout row vertical margin -->
        <dimen name="pincodelock_pinRow_marginVertical">10dp</dimen>
        <!-- each pin padding (affects ripple) minimum height -->
        <dimen name="pincodelock_pin_padding">15dp</dimen>
        <!-- dot indicator container radius -->
        <dimen name="pincodelock_dotContainer_border_radius">8dp</dimen>
        <!-- dot icon size -->
        <dimen name="pincodelock_dot_icon_size">20dp</dimen>
    
</resources>
```

## Known Bugs
* maintaining/restoring view instance when activity is destroyed **(Fixed in v0.1.133)**
* Library not optimized for landscape orientation. **(Fixed in V.0.1.15)**

## Support 
For any bugs or suggestions regarding the library, feel free to open an issue. 

