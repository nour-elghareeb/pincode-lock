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
    private final Context context;

    DotViewHolder(View itemView, Context context) {
        super(itemView);
        animation = AnimationUtils.loadAnimation(context, R.anim.resize);
        this.context = context;

    }
    void animate(){
        int size = context.getResources().
                getDimensionPixelSize(R.dimen.pincodelock_dot_icon_size) + 50;
        itemView.startAnimation(animation);
    }
}
