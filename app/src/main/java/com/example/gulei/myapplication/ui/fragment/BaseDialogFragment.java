package com.example.gulei.myapplication.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by gulei on 2016/5/3 0003.
 */
public class BaseDialogFragment extends DialogFragment {
    private Dialog dialog;
    public static BaseDialogFragment newInstance(){
        BaseDialogFragment baseDialogFragment = new BaseDialogFragment();
        return baseDialogFragment;
    }
    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }
}
