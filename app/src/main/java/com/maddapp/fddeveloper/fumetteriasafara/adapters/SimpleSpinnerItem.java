package com.maddapp.fddeveloper.fumetteriasafara.adapters;

/**
 * a spinner key-value pair
 */

public class SimpleSpinnerItem {
    public String id;
    public String text;

    public SimpleSpinnerItem(String id, String text){
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
