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

public class PinDotView extends RecyclerView {
    // A string represntation for the dots.
    private static final String TAG = PinDotView.class.getSimpleName();
    private DotAdapter adapter;
    private TypedArray attrArray;
    private SparseArray<Object> attrValues;
    public PinDotView(Context context) {
        super(context);
        init();
    }

    public PinDotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.PinDotView, 0, 0);
        init();

    }

    public PinDotView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        attrArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.PinDotView, 0, 0);
        init();
    }

    /**
     * A initialization method runs after constructor
     */
    private void init() {
        // sets the recycler layout to horizontal
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setHasFixedSize(true);
        // creates a new adapter
        adapter = new DotAdapter();
        setAdapter(adapter);
        // init the attributes map
        attrValues = new SparseArray<>();
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
                if (index == R.styleable.PinDotView_dotBackground){
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

    /**
     * Set max length of dots.
     * <p>to update length, use the {@link PinLockView#setMaxLength}</p>
     * @param length current lenth
     */
    void setMaxLength(int length) {
        adapter.setMaxLength(length);
    }

    @Override
    public DotAdapter getAdapter() {
        return adapter;
    }
}
