package ne.pincodelock;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Represents current pin code as a dots view.
 */

class DotRecyclerView extends RecyclerView {
    // A string represntation for the dots.
    private static final String TAG = DotRecyclerView.class.getSimpleName();
    private DotAdapter adapter;
    public DotRecyclerView(Context context) {
        super(context);
        init();
    }

    public DotRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public DotRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * A initialization method runs after constructor
     */
    private void init() {
        // sets the recycler layout to horizontal
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // creates a new adapter
        adapter = new DotAdapter(getContext());
        setAdapter(adapter);
        setMinimumHeight(30);
    }
    @Override
    public DotAdapter getAdapter() {
        return adapter;
    }
}
