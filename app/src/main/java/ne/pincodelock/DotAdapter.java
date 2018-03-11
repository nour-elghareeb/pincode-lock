package ne.pincodelock;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Adapter for PinDotView the recycler
 */

class DotAdapter extends RecyclerView.Adapter<DotViewHolder> implements PinCodeLockListener, ApplyAttrInterface{
    //tag for logging
    private static final String TAG = DotAdapter.class.getSimpleName();
    // Arraylist to hold the dot models
    private ArrayList<DotModel> models;
    // SparseArray that holds the attrs values passed from the xml, used to update ViewHolder inputs.
    private SparseArray<Object> attrValues;

    // Basic constructor that initiate the models array
    DotAdapter(){
        models = new ArrayList<>();
        setMaxLength(4);
    }


    @Override
    public DotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a view for the view holder.
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        // Return a ViewHolder instance for the DotView Recycler.
        return new DotViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(DotViewHolder holder, int position) {
        // Fetch the dot model from the list based on the position and passes it to the ViewHolder
        holder.bind(models.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.dot_view;
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    /**
     * Set the length of the required pin code in order to view that many dots.
     * @param length pincode length. Default is 4.
     */
    void setMaxLength(int length) {
        //models.clear();
        Log.e(TAG, String.valueOf(length));
        int diff = length - models.size();
        if (diff == 0) return;
        for (int i =0; i < Math.abs(diff); i++){
            if (diff > 0) {
                models.add(new DotModel(models.size() + i));
            }else{
                models.remove(models.size()-1);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * <p>gets called whenever pin code changed</p>
     * It reflects the current pin code to the dot view based on pin length.
     * @param pin current pincode
     */
    @Override
    public void onPinChange(String pin) {
        //get current length
        int length = pin.length();
        // if an error occurred and the pin length was actually longer than the number of dots
        // it sets the length to the number of dots..
        if (length > models.size()) length = models.size();
        // loop over every dot
        for (int i = 0; i < models.size(); i++){
            // if the current dot is before the length, set it disabled
            if (i >= length){
                models.get(i).setEnabled(false);
            }
            // if the current dot is after the length, set it enabled
            else{
                models.get(i).setEnabled(true);
            }
        }
        // notify adapter
        notifyDataSetChanged();
    }

    /**
     * set the attr set passed with xml.
     * @param attrValues attrs set values.
     */
    void setAttrValues(SparseArray<Object> attrValues) {
        this.attrValues = attrValues;
    }

    @Override
    public void apply(View... views) {
        if (attrValues != null) {
            for (int i = 0; i < attrValues.size(); i++) {
                int key = attrValues.keyAt(i);
                // get the object by the key.
                Object obj = attrValues.get(key);
                if (key ==  R.styleable.PinDotView_dotBackground){
                    for (View view : views){
                        view.setBackgroundResource((Integer) obj);
                    }
                }
            }
        }
    }
}
