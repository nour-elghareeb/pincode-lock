package ne.pincodelock;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * An interface connects the PinViewAdapter with PinViewHolders
 */

interface PinAdapterInterface {
    void onNumberAction(String number);
    void onBackspaceAction();
    void onBackspaceUp();
    void onBackspaceDown();
    boolean hasReachedMaxLength();
    boolean isBackspaceAllowed();
}
