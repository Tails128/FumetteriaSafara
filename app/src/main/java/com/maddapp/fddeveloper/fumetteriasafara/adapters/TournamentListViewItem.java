package com.maddapp.fddeveloper.fumetteriasafara.adapters;

import android.view.View;

/**
 * Adapter which adapts the tournament entity into Tournament adapter
 */

public class TournamentListViewItem {
    public String text;
    public String id;
    View.OnClickListener callBackButton;
    View.OnClickListener callBackText;

    public TournamentListViewItem(String monoValue, View.OnClickListener buttonCallback, View.OnClickListener textCallback){
        text = monoValue;
        id = monoValue;
        callBackButton = buttonCallback;
        callBackText = textCallback;
    }
}
