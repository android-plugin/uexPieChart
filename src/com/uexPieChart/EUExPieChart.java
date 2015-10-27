package com.uexPieChart;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import com.uexPieChart.bean.PieChartBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

public class EUExPieChart extends EUExBase implements OnGetDataListener {
	public EUExPieChart(Context context, EBrowserView arg1) {
		super(context, arg1);
		this.mainActivity = (Activity) context;
	}

	static String opID = "0";
	static final String functionName = "uexPieChart.loadData";
	static final String callBackName = "uexPieChart.callBackData";
	static final String stopName = "uexPieChart.pieChartStop";
	static final String cbOpenFunName = "uexPieChart.cbOpen";
	static final String onDataFunName = "uexPieChart.onData";
	static final String onTouchUpFunName = "uexPieChart.onTouchUp";
	private Activity mainActivity;
	private PieChartBaseView pieContext;
	public static final String TAG = "uexPieChart";

	private int startX = 0;
	private int startY = 0;
	public static int screenWidth = 0;
	public static int screenHeight = 0;

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_OPEN = 1;
    private static final int MSG_CLOSE = 2;
    private static final int MSG_SET_JSON_DATA = 3;

	@Override
	protected boolean clean() {
		close(null);
		return false;
	}

    public void open(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_OPEN;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void openMsg(String[] params) {
        if(pieContext!=null){
            return;
        }
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        opID = params[0];
        if (params[1].length() != 0) {
            startX = (int) Double.parseDouble(params[1]);
        }
        if (params[2].length() != 0) {
            startY = (int) Double.parseDouble(params[2]);
        }
        if (params[3].length() != 0) {
            screenWidth = (int) Double.parseDouble(params[3]);
        }
        if (params[4].length() != 0) {
            screenHeight = (int) Double.parseDouble(params[4]);
        }
        pieContext = new PieChartBaseView(mContext);
        PieChartBaseView.setOpid(opID);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                screenWidth, screenHeight);
        lp.leftMargin = startX;
        lp.topMargin = startY;
        addViewToCurrentWindow(pieContext, lp);
        pieContext.setGetDataListener(EUExPieChart.this);

        loadData(opID);
    }

    public void close(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLOSE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void closeMsg() {
        if (null != pieContext) {
            removeViewFromCurrentWindow(pieContext);
            pieContext = null;
        }
    }

    public void setJsonData(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SET_JSON_DATA;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setJsonDataMsg(String[] params) {
        try {
            JSONObject json = new JSONObject(params[0]);
            String jsonResult = json.getString("data");
            final List<PieChartBean> pieList = PieChartUtility
                    .parseData(jsonResult);
            pieContext.setData(pieList, screenWidth, screenHeight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_OPEN:
                openMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLOSE:
                closeMsg();
                break;
            case MSG_SET_JSON_DATA:
                setJsonDataMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }


	public void loadData(String opID) {
		jsCallback(functionName, Integer.parseInt(opID), 0, 0);
		jsCallback(cbOpenFunName, Integer.parseInt(opID), 0, 0);
	}

	@Override
	public void onPieChartMove(String opID, int type, String jsonData) {
		jsCallback(callBackName,Integer.parseInt(opID), 0, jsonData);
		jsCallback(onDataFunName,Integer.parseInt(opID), 0, jsonData);

	}

	@Override
	public void onPieChartStop(String ipID, int type, String jsonData) {
		jsCallback(stopName,Integer.parseInt(opID), 0, jsonData);
		jsCallback(onTouchUpFunName,Integer.parseInt(opID), 0, jsonData);
	}
}
