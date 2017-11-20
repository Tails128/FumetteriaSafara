package com.maddapp.fddeveloper.fumetteriasafara.adapters;

/**
 * A spinner key-value pair. Very simple and intuitive.
 */

public class SimpleSpinnerItem {
    public String id;
    public String text;

    public SimpleSpinnerItem(String id, String text){
        this.id = id;
        this.text = text;
    }

    /**
     * returns the item's text.
     * @return
     */
    @Override
    public String toString() {
        return text;
    }
}
