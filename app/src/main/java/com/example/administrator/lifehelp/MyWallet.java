package com.example.administrator.lifehelp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 用户钱包界面
 */
public class MyWallet extends Activity implements View.OnClickListener{

    private final static String rmb = "¥";
    //显示用户余额
    public double userWalletBalance = 0.0;
    //返回按钮
    public ImageButton walletReturn;
    //标题名称
    public TextView titleName;
    //充值按钮
    public Button recharge;
    //提现按钮
    public Button cash;
    //显示余额
    public TextView walletNum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        initControl();


    }

    private void initControl() {
        walletReturn = (ImageButton) findViewById(R.id.title_return);
        walletReturn.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("我的钱包");
        walletNum = (TextView) findViewById(R.id.wallet_rmb_num);
        walletNum.setText(rmb + userWalletBalance);
        recharge = (Button) findViewById(R.id.wallet_Recharge);
        recharge.setOnClickListener(this);
        cash = (Button) findViewById(R.id.wallet_cash);
        cash.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //充值按钮
            case R.id.wallet_Recharge:
                break;
            //提现按钮
            case R.id.wallet_cash:
                break;
            //返回按钮
            case R.id.title_return:
                finish();
                overridePendingTransition(0,R.anim.fragment_left_exit);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.isFinishing()){
            finish();
            overridePendingTransition(0,R.anim.walletactivity_left_exit);
        }
    }
}
