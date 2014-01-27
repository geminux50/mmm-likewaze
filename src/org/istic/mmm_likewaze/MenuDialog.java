package org.istic.mmm_likewaze;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MenuDialog extends AlertDialog {

	public MenuDialog(Context context, int title, Button srcBtn, int layout) {
		super(context);

		// Get the position of the button which made the call
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		int[] srcBtnLocation = new int[2];
		srcBtn.getLocationInWindow(srcBtnLocation);
		
		// Positioning the Dialog box over the button which made the call
		params.x = srcBtnLocation[0];
		params.y = srcBtnLocation[1];

		// Set the origin on the top-left corner 
		params.gravity = Gravity.TOP | Gravity.LEFT;

		// Create the View from the layout
		LayoutInflater inf = LayoutInflater.from(this.getContext());
		View v = inf.inflate(layout, null, false);
		
		// Apply the View
		this.setView(v);

		// Set dialog title
		this.setTitle(title);

		// Disable icon near the title
		this.setIcon(0);
	}

}