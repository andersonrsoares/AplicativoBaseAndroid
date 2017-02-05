package br.com.anderson.aplicativobase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewLinkActivity extends AppCompatActivity {

    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_link);

        this.webview = (WebView) findViewById(R.id.webview);

        String webData =  "<!DOCTYPE html><head> <meta http-equiv=\"Content-Type\" " +
                "content=\"text/html; charset=utf-8\"> <html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1250\">"+
                "<title></title></head><body id=\"body\">"+
                "printer<br>" +
                "<a href=\"intent://print/#Intent;scheme=printer;package=br.com.anderson.aplicativobase;end\"> Outra pagina </a>" +
                "</body></html>";
        webview.loadData(webData, "text/html", "UTF-8");
    }
}
