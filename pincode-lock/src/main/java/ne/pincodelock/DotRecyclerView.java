package ne.pincodelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;

/**
 * Represents current pin code as a dots view.
 */

class DotRecyclerView extends RecyclerView {
    // A string represntation for the dots.
    private static final String TAG = DotRecyclerView.class.getSimpleName();
    private DotAdapter adapter;
    private TypedArray attrArray;
    private SparseArray<Object> attrValues;
    public DotRecyclerView(Context context) {
        super(context);
        init();
    }

    public DotRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.DotRecyclerView, 0, 0);
        init();

    }

    public DotRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.DotRecyclerView, 0, 0);
        init();
    }

    /**
     * A initialization method runs after constructor
     */
    private void init() {
        // sets the recycler layout to horizontal
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //setHasFixedSize(true);
        // creates a new adapter
        adapter = new DotAdapter(getContext());
        setAdapter(adapter);
        // init the attributes map
        attrValues = new SparseArray<>();
        setMinimumHeight(30);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // apply the passed xml attributes
        applyAttrs();
    }

    /**
     * apply passed xml attributes to the views
     */
    private void applyAttrs() {
        for (int i = 0; i < attrArray.getIndexCount(); i++) {
            int index = attrArray.getIndex(i);
            try {
                if (index == R.styleable.DotRecyclerView_dotBackground){
                        attrValues.put(index, attrArray.getResourceId(index, R.drawable.dot_background_light));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // notify adapter with the current attribue values
        adapter.setAttrValues(attrValues);
    }

    @Override
    public DotAdapter getAdapter() {
        return adapter;
    }
}
