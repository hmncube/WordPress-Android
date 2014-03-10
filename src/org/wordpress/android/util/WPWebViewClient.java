package org.wordpress.android.util;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.wordpress.android.datasets.TrustedSslDomainTable;
import org.wordpress.android.models.Blog;

/**
 * WebViewClient that is capable of handling HTTP authentication requests using the HTTP
 * username and password of the blog configured for this activity.
 */
public class WPWebViewClient extends WebViewClient {
    private final Blog mBlog;
    private String mCurrentUrl;

    public WPWebViewClient(Blog blog) {
        super();
        this.mBlog = blog;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Found a bug on some pages where there is an incorrect
        // auto-redirect to file:///android_asset/webkit/.
        if (!url.equals("file:///android_asset/webkit/")) {
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mCurrentUrl = url;
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (mBlog != null) {
            handler.proceed(mBlog.getHttpuser(), mBlog.getHttppassword());
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        String domain = UrlUtils.getDomainFromUrl(mCurrentUrl);
        if (TrustedSslDomainTable.isDomainTrusted(domain)) {
            handler.proceed();
            return;
        }

        super.onReceivedSslError(view, handler, error);
    }
}

