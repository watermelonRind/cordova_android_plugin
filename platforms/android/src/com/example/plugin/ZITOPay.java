package com.example.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.SDK.ZitoCloud;
import com.SDK.ZitoPay;
import com.SDK.ZitoPayResult;
import com.SDK.ZitoPayUpLoadParmas;
import com.SDK.inter.ZitoCallBack;
import com.SDK.inter.ZitoResult;
import com.SDK.utils.ZiToPayFields;
import com.example.plugin.utils.TimeUtils;
import com.example.plugin.utils.ToastUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class ZITOPay extends CordovaPlugin {
    private Activity activity;
    String toastMsg;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private CallbackContext callbackContext;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ToastUtil.showToast(toastMsg, activity);
                    break;
                default:
                    break;
            }
        }
    };
    ZitoCallBack zitoCallback = new ZitoCallBack() {
        @Override
        public void done(ZitoResult result) {
            //判断结果
            final ZitoPayResult zitoPayResult = (ZitoPayResult) result;

            Message msg = mHandler.obtainMessage();
            //单纯的显示支付结果
            msg.what = 1;
            String res = zitoPayResult.result;
            if (res.equals(ZitoPayResult.RESULT_SUCCESS)) {
                toastMsg = "支付成功";
                callbackContext.success(res);
            } else if (res.equals(ZitoPayResult.RESULT_CANCEL)) {
                toastMsg = "支付取消" + zitoPayResult.detailInfo;
                callbackContext.error(res + zitoPayResult.detailInfo);
            } else if (res.equals(ZitoPayResult.RESULT_FAIL)) {
                toastMsg = "支付失败" + zitoPayResult.detailInfo;
                callbackContext.error(res + zitoPayResult.detailInfo);
            } else if (res.equals(ZitoPayResult.START_WEB_SCUESS)) {
                toastMsg = "支付webview初始化成功" + zitoPayResult.detailInfo;
                callbackContext.error(res + zitoPayResult.detailInfo);
            }
            mHandler.sendMessage(msg);

        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.e("initialize", "============================");
        activity = cordova.getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", "ZITOPay....onStart方法调用了");
        //设置ZTIO上注册获得的融拓提供的Id和融拓提供的Secret和支付应用的AppId
        ZitoCloud.setAppIdAndSecret("RZF206855", "e3c7cc4540463c64b22cb619f52abd9e", "RZF206852");
        //是测试模式
        ZitoCloud.setTest(true);
        //初始化微信
        String initInfo = ZitoPay.initWechatPay(activity, "wx2a490909492ec841");
        if (initInfo != null) {
            ToastUtil.showToast("微信初始化失败：" + initInfo, activity);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        //参数赋值
        ZitoPayUpLoadParmas parma = new ZitoPayUpLoadParmas();
        //随机生成订单号
        parma.orderidinf = "S222_" + TimeUtils.getNowTime();

        //parma.totalPrice="0.01";
        parma.currey = "cny";
        //parma.ordertitle="食品";
        // parma.goodsname ="零食";
        // parma.goodsdetail ="乐事薯片";
        parma.bgRetUrl = "http://www.baidu.com";
        parma.returnUrl = "http://www.baidu.com";
        parma.retUrl = "http://www.baidu.com";

        parma.totalPrice = args.getString(0);
        parma.ordertitle = args.getString(1);
        parma.goodsname = args.getString(2);
        parma.goodsdetail = args.getString(3);


        if (action.equals("ALiPay")) {
            Map<String, String> optional = new HashMap<String, String>();
            //扩展参数可以传任意数量
            optional.put("Key", "Value");
            parma.optional = optional;
            parma.analysis = null;

            ZitoPay.getInstance(activity).sendZITOReq(ZiToPayFields.AliPay, parma, zitoCallback);
            return true;
        } else if (action.equals("WXPay")) {
            if (ZitoPay.isWXAppInstalledAndSupported() && ZitoPay.isWXPaySupported()) {
                ZitoPay.getInstance(activity).sendZITOReq(ZiToPayFields.WXPay, parma, zitoCallback);
            } else {
                ToastUtil.showToast("您尚未安装微信或者安装的微信版本不支持", activity);
            }
            return true;
        } else if (action.equals("JIUFBANKPay")) {
            parma.usrName = args.getString(4);
            parma.usrMblNo = args.getString(5);
            parma.usrAddr = args.getString(6);
            ZitoPay.getInstance(activity).sendZITOReq(ZiToPayFields.JIUFBANKPay, parma, zitoCallback);
            return true;
        } else if (action.equals("JingDongPay")) {
            ZitoPay.getInstance(activity).sendZITOReq(ZiToPayFields.JingDongPay, parma, zitoCallback);
            return true;
        } else if (action.equals("XINGYEBANKWXPay")) {
            if (ZitoPay.isWXAppInstalledAndSupported() && ZitoPay.isWXPaySupported()) {
                ZitoPay.getInstance(activity).sendZITOReq(ZiToPayFields.XINGYEBANKWXPay, parma, zitoCallback);
            } else {
                ToastUtil.showToast("您尚未安装微信或者安装的微信版本不支持", activity);
            }
            return true;
        } else {
        }

        return false;
    }

}
