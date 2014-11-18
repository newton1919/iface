package com.faceshot.main;

import java.util.HashMap;
import java.util.Map;

import com.faceshot.helper.UserHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public UserHelper userHelper;
	static Handler handler;
	Dialog pd;
	private TextView tvHint;
	LinearLayout login_activity;
	PullDoorView pullDoorView;
	Button signin_button;
	EditText username_edit;
	EditText password_edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();

		tvHint = (TextView) this.findViewById(R.id.tv_hint);
		Animation ani = new AlphaAnimation(0f, 1f);
		ani.setDuration(1500);
		ani.setRepeatMode(Animation.REVERSE);
		ani.setRepeatCount(Animation.INFINITE);
		tvHint.startAnimation(ani);
	}

	public void init() {
		String host = this.getString(R.string.server_host);
		String port = this.getString(R.string.server_port);
		userHelper = UserHelper.getInstance(host, port);
		//处理登录逻辑
		username_edit = (EditText) this.findViewById(R.id.username_edit);
		password_edit = (EditText) this.findViewById(R.id.password_edit);
		signin_button = (Button) this.findViewById(R.id.signin_button);
		signin_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, view.toString(), 1000).show();
				String userName = username_edit.getText().toString();
				String password = password_edit.getText().toString();
				//开启子线程去登陆这个网络操作，必须
				new Thread(new LoginThread(userName, password)).start();
				
			}
		});
		//end
		pullDoorView = (PullDoorView) this.findViewById(R.id.myImage);
		//login_activity
		login_activity = (LinearLayout) this.findViewById(R.id.login_activity);
		login_activity.setOnTouchListener(new View.OnTouchListener() {
			private int mLastDownY = 0;
			private int mCurryY;
			private int mDelY;
			
			public void startBounceAnim(int startY, int dy, int duration) {
				Scroller mScroller = pullDoorView.getmScroller();
				mScroller.startScroll(0, startY, 0, dy, duration);
				pullDoorView.invalidate();
			}
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mLastDownY = (int) event.getY();
					System.err.println("ACTION_DOWN=" + mLastDownY);
					return true;
					
				case MotionEvent.ACTION_UP:
					mCurryY = (int) event.getY();
					mDelY = mCurryY - mLastDownY;
					if (mDelY > 0) {
						pullDoorView.setVisibility(View.VISIBLE);
						startBounceAnim(pullDoorView.getmScreenHeigh(), -pullDoorView.getmScreenHeigh(), 1000);
						pullDoorView.setmCloseFlag(false);
					}

					break;
				}
				return false;
			}
		});
		
		// Progress
    	pd=new Dialog(MainActivity.this, R.style.new_circle_progress);
    	pd.setContentView(R.layout.layout_progressbar);
    	
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (!Thread.currentThread().isInterrupted()) {
					switch (msg.what) {
					case 0:
						//pd.show();//
						//Toast.makeText(MainActivity.this, "second", 1000).show();
						break;
					case 1:
						//pd.hide();
						break;
					case 0x123:
						HashMap<Integer, String> respMap = (HashMap<Integer, String>)msg.obj;
						//Toast.makeText(MainActivity.this, respMap.toString(), 5000).show();
						loginAfter(respMap);
						break;
					}
				}
				super.handleMessage(msg);
			}
		};
	}
	/*
	public void loadurl(final WebView view, final String url) {
		new Thread() {
			public void run() {
				//handler.sendEmptyMessage(0);
				view.loadUrl(url);//
			}
		}.start();
	}*/
	
	private void loginAfter(HashMap<Integer, String> respMap) {
		//to-do
		for(Map.Entry<Integer, String> resp:respMap.entrySet()){
			int respStatus = resp.getKey();
			String respJson = resp.getValue();
			if (respStatus == 200) {
				//登录成功
				Intent intent = new Intent(MainActivity.this, HomePage.class);
				startActivity(intent);
			} else {
				//失败
				Toast.makeText(MainActivity.this, this.getString(R.string.login_error_msg), 2000).show();
			}
			
			break;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			ConfirmExit();
			return true;
		}
			
		return super.onKeyDown(keyCode, event);
	}

	public void ConfirmExit() {
		AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
		ad.setTitle("Exit");
		ad.setMessage("confirm to exit?");
		ad.setPositiveButton("yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		});
		ad.setNegativeButton("no", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				// pass
			}
		});
		ad.show();
	}

	int lastDownY = 0;
	
	//login thread class
	private class LoginThread implements Runnable {
		private String userName;
		private String password;
		
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}

		public LoginThread(String userName, String password) {
			super();
			this.userName = userName;
			this.password = password;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<Integer, String> respMap = userHelper.loginUser(userName, password);
			
			Message message=Message.obtain();
            message.obj=respMap;
            message.what=0x123;
            handler.sendMessage(message);
		}
		
	}
}