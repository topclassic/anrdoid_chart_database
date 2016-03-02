package com.smartelectric.data;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.xxmassdeveloper.mpchartexample.R;

public class Anima extends Activity {
	AnimationDrawable splash;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.anima);
	        ImageView image = (ImageView) findViewById(R.id.imageView1);
	        splash = (AnimationDrawable) image.getBackground();
	        
			new Thread(new Runnable() {
				public void run() {
		        	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) { }
		        	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		  		  	startActivity(intent);
		  		  	finish();
				}
			}).start();
	        image.post(new Starter());
	        
	    }
	    
	    class Starter implements Runnable {
	        public void run() {
	        	splash.start();
	        }
	    }    
	    
	    /*public boolean onTouchEvent(MotionEvent event) {
	    	  if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    		  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	    		  startActivity(intent);
	    		  this.finish();
	    		  return true;
	    	  }
	    	  return super.onTouchEvent(event);
	    }*/
	
}
