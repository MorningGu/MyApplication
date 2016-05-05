package com.example.gulei.myapplication.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.gulei.myapplication.R;

/**
 * Created by gulei on 2016/5/3 0003.
 */
public class BaseDialog extends Dialog {
    private TextView tv_title;
    private TextView tv_msg;
    private TextView tv_cancel;
    private TextView tv_ok;
    private String title;
    private String msg;
    private View.OnClickListener okListener;
    private View.OnClickListener cancelListener;
    private boolean canDismiss;
    public BaseDialog(Context context) {
        super(context, R.style.alert);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_base);
        initView();
    }
    private void initView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
        tv_ok = (TextView)findViewById(R.id.tv_ok);
        tv_title.setText(title);
        tv_msg.setText(msg);
        tv_cancel.setOnClickListener(cancelListener);
        tv_ok.setOnClickListener(okListener);
        setCanceledOnTouchOutside(canDismiss);
    }

    /**
     * 初始化数据，在show之前调用
     * @param title
     * @param msg
     * @param cancelListener
     * @param okListener
     * @param canDismiss
     */
    public void initContent(String title, String msg,boolean canDismiss,
                            View.OnClickListener cancelListener, View.OnClickListener okListener){
        this.title = title;
        this.msg = msg;
        this.cancelListener = cancelListener;
        this.okListener = okListener;
        this.canDismiss = canDismiss;
    }
}
