package ne.pincodelock;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * A ViewHolder that holds DotView holder
 */

class DotViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = DotViewHolder.class.getSimpleName();
    private final View dotView;
    private final Animation animation;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    DotViewHolder(View itemView, ApplyAttrInterface attrInterface) {
        super(itemView);

        dotView = itemView.findViewById(R.id.dotItem);
        attrInterface.apply(dotView);
        animation = AnimationUtils.loadAnimation(((DotAdapter)attrInterface).getContext(), R.anim.resize);
        animation.setZAdjustment(3);
    }
    void animate(){
        itemView.findViewById(R.id.dotItem).startAnimation(animation);
    }

    void bind(DotModel dot){
        dotView.setEnabled(dot.isEnabled());

    }
}
