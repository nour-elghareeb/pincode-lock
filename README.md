# Pincode-Lock
Provides a pin code lock screen for android apps

## Features:
  * Customiable colors, backgrounds, sizes, etc...
  * Auto error handling animation.
  * Auto freeze input when maximum number of attempts is reached
  * Customizable values:
    - Dot-view indicator
    - Required length (actual user pin code length) to get a callback to start validation without letting them know the actual length.
    - Maximum pin length the view can handle.
    - Numnber of allowed attempts.
    - Duration of input-freeze when the user run out of attempts.
    - Message to show the user when they run out of attempts.
    - Consecutive backspace touches to mark as an attempt.     		
## Download
To download library include this repository:
```gradle
repositories {
	// other repositories
	
	maven { url "https://dl.bintray.com/nour-elghareeb/ne" }
}
```
And grap library dependency (Check latest version from my [bintray repository](https://bintray.com/nour-elghareeb/ne/pincode-lock) ):
```gradle
dependencies {
	// other dependencies here 
	
	compile 'com.ne:pincode-lock:0.1.117'
}
```
## Usage
Set up your xml:
```xml
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto" >
	<ne.pincodelock.IndicatorDotView
		android:id="@+id/dotsView"
		android:layout_width="0dp"
		android:layout_height="wrap_content"
		app:layout_constraintTop_toBottomOf="parent"
		app:layout_constraintStart_toStartOf="parent"
		app:layout_constraintEnd_toEndOf="parent"
		app:layout_constraintBottom_toTopOf="@id/pinLockView"
		app:layout_constraintVertical_bias=".7"
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
	/>
</android.support.constraint.ConstraintLayout>
```

In Java:
```java
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
```

Library customization:
To customize library copy these values to its appropriate resources files and update them as you like.
```xml
<!-- colors.xml -->

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
<!-- actual dot color -->
<color name="pincodelock_dot_color">#666666</color>
<!-- message shown when the layout is frozen text color color -->
<color name="pincodelock_freeze_msg_color">#666666</color>


<!-- dimens.xml -->

<!-- message shown when the layout is frozen text size -->
<dimen name="pincodelock_freeze_msg_textsize">15sp</dimen>
<!-- pin layout text size -->
<dimen name="pincodelock_pin_textsize">25sp</dimen>
```

## Known Bugs
* none

## Support 
feel free to contact me for any bugs or suggestions regarding the library

