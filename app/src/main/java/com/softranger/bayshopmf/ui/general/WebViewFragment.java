package com.softranger.bayshopmf.ui.general;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    private static final String URL_ARG = "url";

    private MainActivity mActivity;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(URL_ARG, url);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.webViewProgressBar);
        mProgressBar.setMax(100);
        mWebView.setWebChromeClient(new MyWebViewClient());
        mWebView.setWebViewClient(new WebClient());
        mActivity = (MainActivity) getActivity();
        String productUrl = getArguments().getString(URL_ARG);
        if (!productUrl.contains("https://") && !productUrl.contains("http://")) {
            productUrl = "http://" + productUrl;
        }
        mWebView.loadUrl(productUrl);
        return view;
    }


    private class WebClient extends WebViewClient {

        @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);

        }
    }

    private class MyWebViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
}
