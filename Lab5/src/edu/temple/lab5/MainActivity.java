package edu.temple.lab5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity implements OnClickListener{

	EditText etURL;
	Button bGo;
	WebView webView;
	Runnable run;
	URL url;
	String urlString;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //initialize variables for widgets set the on click listener
        etURL = (EditText) findViewById(R.id.etURL);
        bGo = (Button) findViewById(R.id.bGo);
        webView = (WebView) findViewById(R.id.webView);
        //webView.getSettings().setJavaScriptEnabled(true);
        bGo.setOnClickListener(this);
        
    }
    
	@Override
	public void onClick(View v) {
		Thread httpThread = new Thread(){
			public void run(){
	    		try{
	    			//get input from text box and if it doesn't have http:// append it to the front of the url
	    			if(etURL.getText().toString().contains("http://"))
	    				url = new URL(etURL.getText().toString());
	    			else
	    				url = new URL("http://" + etURL.getText().toString());
	    			urlString = url.toString();
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					//read html from website and place it into a string
					String htmlCode = "",tempHtml = "";
					tempHtml = in.readLine();
					while(tempHtml != null){
						htmlCode = htmlCode + tempHtml;
						tempHtml = in.readLine();	
					}
					//get a message from the pool, put the html string into it, and send it back to the UI thread
					Message msg = Message.obtain();
					msg.obj = htmlCode;
					msg.setTarget(handler);
					msg.sendToTarget();
	    		}catch(Exception e)
	    		{
	    			Log.e("CATCH", e.toString());
	    			e.printStackTrace();
	    		}
	    		finally{}
	    	}
		};
		//start the thread
		httpThread.start();
	}
	
    //handler that receives the html string and displays it in the web browser
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
        	String html = (String) msg.obj;
        	Log.e("HTML",html);
        	webView.loadData(html, "text/html", "UTF-8");
        }
    };
	
}

