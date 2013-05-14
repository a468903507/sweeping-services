package net.sweeping.app.ui;

import net.sweeping.app.R;
import net.sweeping.app.base.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 首页
 * @author Bert Guo
 * 2013-5-11
 */
public class Main extends BaseActivity {

	private Button home_scan_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		initView();
		setOnClick();
	}
	
	public void initView(){
		home_scan_btn=(Button)findViewById(R.id.home_scan_btn);
		
		
	}
	
	public void setOnClick(){
		home_scan_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("点击");
				Intent intent=new Intent("com.google.zxing.client.android.SCAN");
				Main.this.startActivityForResult(intent, 0);
				
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode!=0){
			System.out.println("没有扫描结果");
			return ;
		}
		if(resultCode == RESULT_OK){
			String contents = data.getStringExtra("SCAN_RESULT");
			System.out.println(contents);
		}
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
