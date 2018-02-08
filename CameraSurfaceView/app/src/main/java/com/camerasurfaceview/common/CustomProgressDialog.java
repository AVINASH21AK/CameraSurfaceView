package com.camerasurfaceview.common;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.LightingColorFilter;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.camerasurfaceview.R;


@SuppressLint("ResourceAsColor")
public class CustomProgressDialog extends Dialog
{

	LinearLayout llMainBg;
	ProgressBar mProgressBar;

	public CustomProgressDialog(final Context context)
	{
		super(context);
		 getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		getWindow().setBackgroundDrawableResource(R.drawable.prograss_bg); //temp removed
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.progressbar);
		
		
		
		llMainBg = (LinearLayout)findViewById(R.id.llMainBg);

		setCancelable(false);
		
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
		/*

		0 x 00     00   00    00
    	alpha      red  green blue

		*/

		/*int mul = 0xFFFFFF00; //remove BLUE component
		int add = 0x0000FF00; //set GREEN full*/


		int mul = 0x00000000;     //Transparent
		//int mul = 0xFFFF0000;   //remove BLUE & Green component
		int add = 0xFF601421;     //0x00FF0000; -- Pure Red color
		//0xFF601421 -- dark red shade

		mProgressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(mul, add));

		setOnKeyListener(new OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface arg0, int keyCode,
                             KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				dismiss();

				((Activity) context).finish();
			}
			return true;
		}
	});
		

	}

	public void setMessage(String message)
	{
	}
}