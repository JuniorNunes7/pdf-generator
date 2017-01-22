package com.pdf.generator;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * This class echoes a string called from JavaScript.
 */
public class PDFGenerator extends CordovaPlugin {

    private final static String APPNAME = "PDFGenerator";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("htmlToPDF")) {
            this.pdfPrinter(args, callbackContext);

            return true;
        }
        return false;
    }

    //TODO need improvements.
    private boolean isValidURL(String url){
        boolean validation = url.matches("^(http|https)://.*$");
        Log.i(APPNAME, "this should match:" +validation );
        if(validation)
            return true;
        else
            return false;
    }

    private void pdfPrinter(final JSONArray args, final CallbackContext callbackContext) throws JSONException{

        final Context ctx = this.cordova.getActivity().getApplicationContext();
        final CordovaInterface _cordova = this.cordova;
        final CallbackContext cb = callbackContext;

        _cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final WebView webview = new WebView(ctx);

                webview.getSettings().setJavaScriptEnabled(true);

                PDFPrinterWebView wvPrinter = new PDFPrinterWebView((PrintManager)
                        _cordova.getActivity().getSystemService(Context.PRINT_SERVICE));

                wvPrinter.setCordovaCallback(cb);
                webview.setWebViewClient(wvPrinter);

                try {

                    if(args.getString(0) != null )
                        webview.loadUrl(args.getString(0));

                     if(args.getString(1) != null )
                        webview.loadData(args.getString(1), "text/html", null);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(APPNAME, e.getMessage());
                    cb.error("Native pasing arguments: "+ e.getMessage());
                }
            }
        });
    }


}
