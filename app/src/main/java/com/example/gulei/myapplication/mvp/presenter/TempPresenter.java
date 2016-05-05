package com.example.gulei.myapplication.mvp.presenter;

import android.support.annotation.Nullable;

import com.example.gulei.myapplication.common.callback.JsonCallback;
import com.example.gulei.myapplication.common.utils.MD5Utils;
import com.example.gulei.myapplication.common.utils.UrlInfoUtils;
import com.example.gulei.myapplication.mvp.iview.IBaseView;
import com.lzy.okhttputils.OkHttpUtils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by gulei on 2016/5/3 0003.
 */
public class TempPresenter {
    private IBaseView mView;
    public TempPresenter(IBaseView view){
        mView = view;
    }
    public void method(String arg){
        mView.showLoading();
        OkHttpUtils.post(UrlInfoUtils.getAppUrl()+"mobile/login/authentication/login.do")     // 请求方式和请求url
                .tag(this)                       // 请求的 tag, 主要用于取消对应的请求
//                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
//                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .params("mobile", "yimi1")
                .params("password", MD5Utils.getMD5Str("123456"))
                .execute(new JsonCallback<String>(String.class) {
                    @Override
                    public String parseNetworkResponse(Response response) {
                        super.parseNetworkResponse(response);
                        //在这里进行个性化的解析
                        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.dismissLoading();
                            }
                        });
                        return null;
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        //提示失败
                    }
                });
    }

}
