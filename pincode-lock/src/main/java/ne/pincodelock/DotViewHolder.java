package ne.pincodelock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * A ViewHolder that holds DotView holder
 */

class DotViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = DotViewHolder.class.getSimpleName();
    private final Animation animation;
    DotViewHolder(View itemView, Context context) {
        super(itemView);
        animation = AnimationUtils.loadAnimation(context, R.anim.resize);
        animation.setZAdjustment(3);
    }
    void animate(){
        itemView.findViewById(R.id.dotItem).startAnimation(animation);
    }
}
