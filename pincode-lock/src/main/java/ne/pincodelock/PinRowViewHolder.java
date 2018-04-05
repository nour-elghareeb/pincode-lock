package ne.pincodelock;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Pin row ViewHolder that represents each row of the pin view.
 */

class PinRowViewHolder extends RecyclerView.ViewHolder {
    // left textview (1, 7, 9)
    protected TextView left;
    // center textview (2, 5, 8)
    TextView center;
    // right text view (3, 6, 9)
    private TextView right;
    // a callback to the adapter.
    PinAdapterInterface callback;
    // OnClick listener for each number
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // if pin code hasn't reach maximum, pass the number pressed to the adapter
            if (callback.isClickable())
                callback.onNumberAction(((TextView) v).getText().toString() );
        }
    };
    /**
     * Constructor for the view holder.
     * @param itemView inflated view.
     * @param callback callback to the adapter
     */
    PinRowViewHolder(View itemView, PinAdapter callback) {
        super(itemView);
        // try to find view left and right
        // will produce ClassCastException on the last row, so let's catch those...
        try {
            left = itemView.findViewById(R.id.left);
            right = itemView.findViewById(R.id.right);
        }catch (ClassCastException ignored){

        }
        center = itemView.findViewById(R.id.center);

        this.callback = callback;
    }

    /**
     * Binds the inputs to the model passed from the adapter.
     * @param rowModel PinRowModel instance that holds each and every input witin this row.
     */
    void bindData(PinRowModel rowModel) {
        // set current number for the textview
        left.setText(rowModel.getLeft().getValue());
        center.setText(rowModel.getCenter().getValue());
        right.setText(rowModel.getRight().getValue());

        //set a click listener for each number
        right.setOnClickListener(clickListener);
        center.setOnClickListener(clickListener);
        left.setOnClickListener(clickListener);
    }
}
