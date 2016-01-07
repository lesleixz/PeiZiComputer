package com.dure.peizifulicompute;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText total_money, mutiple, use_money, explosion_percent,
			establish_cost, current_win, current_lose;
	private TextView result;
	private Button compute_btn;

	BannerView bv;
	private ViewGroup bannerContainer;
	InterstitialAD iad;
	private int quitTimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);

		this.initBanner();
		this.bv.loadAD();

		initView();

	}

	private void initView() {
		total_money = (EditText) findViewById(R.id.total_money);
		mutiple = (EditText) findViewById(R.id.mutiple);
		use_money = (EditText) findViewById(R.id.use_money);
		explosion_percent = (EditText) findViewById(R.id.explosion_percent);
		establish_cost = (EditText) findViewById(R.id.establish_cost);
		current_win = (EditText) findViewById(R.id.current_win);
		current_lose = (EditText) findViewById(R.id.current_lose);

		result = (TextView) findViewById(R.id.result);

		compute_btn = (Button) findViewById(R.id.compute_btn);

		TextWatcher winWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0.length() != 0) {
					current_lose.setEnabled(false);
				} else {
					current_lose.setEnabled(true);
				}
			}
		};

		TextWatcher loseWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0.length() != 0) {
					current_win.setEnabled(false);
				} else {
					current_win.setEnabled(true);
				}
			}
		};

		current_win.addTextChangedListener(winWatcher);
		current_lose.addTextChangedListener(loseWatcher);
		compute_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String totalMoney = total_money.getText().toString();
				String multipleStr = mutiple.getText().toString();
				String useMoney = use_money.getText().toString();
				String explosionPercent = explosion_percent.getText()
						.toString();
				String currentWin = current_win.getText().toString();
				String currentLose = current_lose.getText().toString();
				String establishCost = establish_cost.getText().toString();

				if (TextUtils.isEmpty(totalMoney)) {
					Toast.makeText(MainActivity.this, "请输入投资金额",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (TextUtils.isEmpty(multipleStr)) {
					Toast.makeText(MainActivity.this, "请输入扩大倍数",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (TextUtils.isEmpty(useMoney)) {
					Toast.makeText(MainActivity.this, "请输入占用资金比例",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (TextUtils.isEmpty(explosionPercent)) {
					Toast.makeText(MainActivity.this, "请输入爆仓比例",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (TextUtils.isEmpty(establishCost)) {
					Toast.makeText(MainActivity.this, "请输入建仓手续费",
							Toast.LENGTH_LONG).show();
					return;
				}

				float currentWinOrLose = 0;
				if (!TextUtils.isEmpty(currentWin)) {
					currentWinOrLose = Float.valueOf(currentWin);
				} else if (!TextUtils.isEmpty(currentLose)) {
					currentWinOrLose = -Float.valueOf(currentLose);
				}

				computeFinancing(Float.valueOf(totalMoney),
						Float.valueOf(useMoney), Integer.valueOf(multipleStr),
						Float.valueOf(explosionPercent) / 100,
						currentWinOrLose, Float.valueOf(establishCost) / 1000);

			}
		});
	}

	// 计算赢利/亏损百分比
	private void compute(float totalMoney, float remindMoney) {
		float percent = 0;
		String resultStr = "";
		if (totalMoney > remindMoney) {// 亏损
			// totalMoney * (1 - percent) = remindMoney;
			percent = 1 - remindMoney / totalMoney;
			resultStr = "爆仓时亏损为:" + "亏损";
		} else {// 赢利
				// totalMoney * (1 + percent) = remindMoney;
			percent = remindMoney / totalMoney - 1;
			resultStr = "计算结果为:" + "赢利";
		}

		result.setText(resultStr + String.format("%.1f", percent * 100) + "个点");
	}

	// 计算配资爆仓亏损比例
	private void computeFinancing(float originMoney, float useMoney,
			int mutiple, float explosionPercent, float currentWinOrLose,
			float establishCostPercent) {
		// 买入useMoney所用建仓费
		float establishMoney = useMoney * establishCostPercent;
		// 爆仓亏损总金额 + 当前赢利/亏损金额　＝　建仓所花费用　+　亏损金额
		// originMoney * explosionPercent　+ currentWinOrLose　＝　establishMoney +
		// useMoney * x
		float lostPercent = (originMoney * explosionPercent - establishMoney + currentWinOrLose)
				/ useMoney;

		result.setText("爆仓时亏损为:亏损" + String.format("%.1f", lostPercent * 100)
				+ "个点");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quitTimes++;

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					quitTimes = 0;
				}
			}).start();
			if (quitTimes == 2) {
				System.exit(0);
			} else {
				Toast.makeText(this, "连续按2次返回退出应用", Toast.LENGTH_LONG).show();
				showAsPopup();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initBanner() {
		this.bv = new BannerView(this, ADSize.BANNER, "1105012485",
				"9060108729194573");
		bv.setRefresh(30);
		bv.setADListener(new AbstractBannerADListener() {

			@Override
			public void onNoAD(int arg0) {
				Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
			}

			@Override
			public void onADReceiv() {
				Log.i("AD_DEMO", "ONBannerReceive");
			}
		});
		bannerContainer.addView(bv);
	}

	private InterstitialAD getIAD() {
		if (iad == null) {
			iad = new InterstitialAD(this, "1105012485", "1010309779695594");
		}
		return iad;
	}

	private void showAsPopup() {
		getIAD().setADListener(new AbstractInterstitialADListener() {

			@Override
			public void onNoAD(int arg0) {
				Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
			}

			@Override
			public void onADReceive() {
				iad.showAsPopupWindow();
			}
		});
		iad.loadAD();
	}

}
