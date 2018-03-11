package ne.pincodelock;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A view holder for the last row of the PinLockView that holds the 'zero', and the 'backspace'
 */

class PinLastRowViewHolder extends PinRowViewHolder {
    private static final String TAG = PinLastRowViewHolder.class.getSimpleName();

    // right image (backspace)
    private ImageView right;
    // container for the image
    private LinearLayout rightLayout;
    PinLastRowViewHolder(View itemView, PinAdapter callback) {
        super(itemView, callback);
        right = itemView.findViewById(R.id.right);
        rightLayout = itemView.findViewById(R.id.rightLayout);
    }
    // Override the bind method to offer different functionality for the backspace..

    @SuppressLint("ClickableViewAccessibility")
    @Override
    void bindData(PinRowModel rowModel) {
        // Set text for the center input (zero)
        center.setText(rowModel.getCenter().getValue());
        // attach the OnClickListener from the super class.
        center.setOnClickListener(clickListener);
        // set zero enabled if code hasn't reach max length
        center.setEnabled(!callback.hasReachedMaxLength());
        // set backspacce enabled if pin is not empty
        rightLayout.setEnabled(callback.isBackspaceAllowed());
        /*
         * set a onTouch listener on the backspace be able to trigger backspace action every 200ms
         * the user keeps his finger down.         *
         */
        rightLayout.setOnTouchListener(new View.OnTouchListener() {
            private Long downOnBackSpaceSince = 0L;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // if user touches the backspace
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    // send a flag to the adapter (user started touch)
                    callback.onBackspaceDown();
                    // init a time indicates the ms the user started touching
                    downOnBackSpaceSince = System.currentTimeMillis();
                    // if pin is not empty, send a backspace action to the adapter..
                    if (callback.isBackspaceAllowed()) callback.onBackspaceAction();
                }
                // User is still touching
                else if (event.getAction() == MotionEvent.ACTION_MOVE){
                    // get the difference between the time of this event the ACTION_DOWN event
                    long diff = System.currentTimeMillis() - downOnBackSpaceSince;
                    // if a 200ms passed, send another backspace action
                    if (diff > 200){
                        downOnBackSpaceSince = System.currentTimeMillis();
                        if (callback.isBackspaceAllowed()) callback.onBackspaceAction();
                    }
                }
                // if user lifts his finger
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    // send a flag to the adapter (touch event is over)
                    callback.onBackspaceUp();
                }
                return false;
            }
        });
    }

}
