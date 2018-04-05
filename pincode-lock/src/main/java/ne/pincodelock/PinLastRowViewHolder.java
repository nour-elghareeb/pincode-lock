package ne.pincodelock;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A view holder for the last row of the PinLockView that holds the 'zero', and the 'pincodelock_backspace_icon'
 */

class PinLastRowViewHolder extends PinRowViewHolder {
    private static final String TAG = PinLastRowViewHolder.class.getSimpleName();

    // right image (pincodelock_backspace_icon)
    private ImageView right;
    // container for the image
    private LinearLayout rightLayout;
    PinLastRowViewHolder(View itemView, PinAdapter callback) {
        super(itemView, callback);
        right = itemView.findViewById(R.id.right);
        rightLayout = itemView.findViewById(R.id.rightLayout);

        int padding = rightLayout.getPaddingTop() + 10;
        rightLayout.setPadding(padding, padding, padding, padding);
        right.setColorFilter(ContextCompat.getColor(callback.getContext(), R.color.pincodelock_text_color), PorterDuff.Mode.SRC_IN);

    }
    // Override the bind method to offer different functionality for the pincodelock_backspace_icon..

    @SuppressLint("ClickableViewAccessibility")
    @Override
    void bindData(PinRowModel rowModel) {
        // Set text for the center input (zero)
        center.setText(rowModel.getCenter().getValue());
        // attach the OnClickListener from the super class.
        center.setOnClickListener(clickListener);

        /*
         * set a onTouch listener on the pincodelock_backspace_icon be able to trigger
         * pincodelock_backspace_icon action every 200ms
         * the user keeps his finger down.         *
         */
        rightLayout.setOnTouchListener(new View.OnTouchListener() {
            private Long downOnBackSpaceSince = 0L;
            private long maxDiff = 200;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // if user touches the pincodelock_backspace_icon
                if (!callback.isClickable()) return false;
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    maxDiff = 200;
                    // send a flag to the adapter (user started touch)
                    callback.onBackspaceDown();
                    // init a time indicates the ms the user started touching
                    downOnBackSpaceSince = System.currentTimeMillis();
                    // if pin is not empty, send a pincodelock_backspace_icon action to the adapter..
                    if (callback.isBackspaceAllowed()) callback.onBackspaceAction();
                }
                // User is still touching
                else if (event.getAction() == MotionEvent.ACTION_MOVE){
                    // get the difference between the time of this event the ACTION_DOWN event
                    long diff = System.currentTimeMillis() - downOnBackSpaceSince;
                    // if a 200ms passed, send another pincodelock_backspace_icon action
                    if (diff > maxDiff){
                        downOnBackSpaceSince = System.currentTimeMillis();
                        if (callback.isBackspaceAllowed()) callback.onBackspaceAction();
                        // decrease difference limit between each firing each time
                        if (maxDiff == 200) maxDiff = 150;
                        else if (maxDiff == 150) maxDiff = 100;
                        else if (maxDiff == 100) maxDiff = 50;
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
