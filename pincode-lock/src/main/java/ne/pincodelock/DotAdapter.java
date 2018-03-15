package ne.pincodelock;

import android.content.Context;
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
    private Context context;

    // Basic constructor that initiate the models array
    DotAdapter(Context context){
        models = new ArrayList<>();
        this.context = context;
        //setMaxLength(4);
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
        if (position == models.size() - 1 ){
            holder.animate();
        }

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
     * <p>gets called whenever pin code changed</p>
     * It reflects the current pin code to the dot view based on pin length.
     * @param pin current pincode
     */
    @Override
    public void onPinChange(String pin) {
        int lastPos = models.size() - 1;
        if (pin.length() == 0){
            models.clear();
            notifyDataSetChanged();
        }
        else if (models.size() > pin.length()){
            models.remove(lastPos);
            notifyItemRemoved(lastPos);
        }else {
            models.add(new DotModel(lastPos + 1));
            notifyItemChanged(lastPos + 1);
        }
    }

    @Override
    public void onPinReachRequired(String pin) {

    }

    @Override
    public void onPinReachMax(String pin) {

    }

    @Override
    public void onPinReAttempt() {

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
                if (key ==  R.styleable.DotRecyclerView_dotBackground){
                    for (View view : views){
                        view.setBackgroundResource((Integer) obj);
                    }
                }
            }
        }
    }

    public int getMaxLength() {
        return models.size();
    }

    public Context getContext() {
        return context;
    }
}
