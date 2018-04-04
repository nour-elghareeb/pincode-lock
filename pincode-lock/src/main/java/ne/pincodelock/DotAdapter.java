package ne.pincodelock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Adapter for PinDotView the recycler
 */

class DotAdapter extends RecyclerView.Adapter<DotViewHolder> implements DotLayoutChangeListener {
    //tag for logging
    private static final String TAG = DotAdapter.class.getSimpleName();
    // Arraylist to hold the dot models
    private ArrayList<DotModel> models;
    // SparseArray that holds the attrs values passed from the xml, used to update ViewHolder inputs.
    private Context context;

    // Basic constructor that initiate the models array
    DotAdapter(Context context){
        models = new ArrayList<>();
        this.context = context;
    }
    @Override
    public DotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a view for the view holder.
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        // Return a ViewHolder instance for the DotView Recycler.
        return new DotViewHolder(view, getContext());
    }

    @Override
    public void onBindViewHolder(DotViewHolder holder, int position) {
        // animate dot during showing up.
        if (position == models.size() - 1 ){
            holder.animate();
        }

    }
    @Override
    public int getItemViewType(int position) {
        return R.layout.layout_viewholder__dot_icon;
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    private Context getContext() {
        return context;
    }

    /**
     * Called whenever PIN is chaged to reflect on the dots view.
     * @param newLength new pin length
     */
    @Override
    public void onPinLengthChange(int newLength) {
        int lastPos = models.size() - 1;
        if (newLength == 0){
            models.clear();
            notifyDataSetChanged();
        }
        else if (models.size() - newLength == Math.abs(1) ){
            if (models.size() > newLength){
                models.remove(lastPos);
                notifyItemRemoved(lastPos);
            }else {
                models.add(new DotModel(lastPos + 1));
                notifyItemChanged(lastPos + 1);
            }
        }else{
            models.clear();
            for (int i = 0; i < newLength; i++){
                models.add(new DotModel(i));
            }
            notifyDataSetChanged();
        }
    }
}
