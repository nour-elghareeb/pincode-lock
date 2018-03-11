package ne.pincodelock;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A ViewHolder that holds DotView holder
 */

class DotViewHolder extends RecyclerView.ViewHolder {
    private final View dotView;

    DotViewHolder(View itemView, ApplyAttrInterface attrInterface) {
        super(itemView);
        dotView = itemView.findViewById(R.id.dotItem);
        attrInterface.apply(dotView);
    }
    void bind(DotModel dot){
        dotView.setEnabled(dot.isEnabled());
    }
}
