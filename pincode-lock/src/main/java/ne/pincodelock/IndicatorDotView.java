package ne.pincodelock;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 *
 * LinearLayout wrapper for Dot Recycler
 */

public class IndicatorDotView extends LinearLayout {
    private DotRecyclerView recycler;

    public IndicatorDotView(Context context) {
        super(context);
        init();
    }

    public IndicatorDotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndicatorDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.indicator_container, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recycler = findViewById(R.id.dotRecycler);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(getContext().getResources().getDrawable(R.drawable.dot_container));
        }else{
            setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.dot_container));
        }
        setPadding(20, 20, 20, 20);
        setMinimumHeight(130);
        setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
    }

    DotRecyclerView getRecycler() {
        return recycler;
    }
}
