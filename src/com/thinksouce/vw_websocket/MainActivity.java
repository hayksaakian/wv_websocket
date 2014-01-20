package com.thinksouce.vw_websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.strumsoft.websocket.phonegap.WebSocketFactory;
import com.strumsoft.websocket.phonegap.WebSocket;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        WebView webView = (WebView)findViewById(R.id.webview);	
        String targeturl = "http://www.destiny.gg/embed/chat";	
//      String targeturl = "http://websocket.org/echo.html";		
//      String targeturl = "http://websocket.org/echo.html";	
//      String targeturl = "file:///android_asset/www/index.html";
//      String targeturl = "file:///android_asset/www/test.html";
      final String websocketjspath = "www/js/websocket.js";
        String maybewebsocketjs = "";
		try {
			maybewebsocketjs = GetFileFromAssets(this, websocketjspath);
			//Log.d("Websocket.js", maybewebsocketjs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String websocketjs = maybewebsocketjs;
		
		webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        android.webkit.WebSettings settings = webView.getSettings();
        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            boolean startedit = false;
            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
            }

            String scheme = "localassets:";
            List loadedURLs;
            @Override
            public void onPageFinished(android.webkit.WebView view, String url)
            {
            	if(loadedURLs == null){
            		loadedURLs = new ArrayList<String>();
            	}
//            	view.loadUrl(scheme+websocketjspath);
            	if(!loadedURLs.contains(url)){
            		loadedURLs.add(url);
	            	view.loadUrl("javascript:(function() { "
	
	            	           + "var script=document.createElement('script'); "
	            	           + " script.setAttribute('type','text/javascript'); "
	            	           + " script.setAttribute('src', 'localassets:www/js/websocket.js'); "
	            	      /*      + " script.onload = function(){ "
	            	           + "     test(); "
	            	           + " }; "
	            	      */     + "document.body.appendChild(script); "
	            	           + "})();");
            	}
            	
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url){
                if (url.startsWith(scheme)){
                    try
                        {
                        return new WebResourceResponse(url.endsWith("js") ? "text/javascript" : "text/css", "utf-8",
                                MainActivity.this.getAssets().open(url.substring(scheme.length())));
                        }
                    catch (IOException e)
                        {
                        Log.e(getClass().getSimpleName(), e.getMessage(), e);
                        }
                    Log.d("scheme detected", scheme);
                }
                return null;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new WebSocketFactory(null, webView), "WebSocketFactory");
		webView.loadData("", "text/html", null);
		webView.loadUrl(targeturl);
		
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public String GetFileFromAssets(Context context, String path) throws IOException{
		StringBuilder buf=new StringBuilder();
	    InputStream json= context.getAssets().open(path);
	    BufferedReader in=
	        new BufferedReader(new InputStreamReader(json));
	    String str;

	    while ((str=in.readLine()) != null) {
	      buf.append(str);
	    }

	    in.close();
		
		return buf.toString();
	}

}
