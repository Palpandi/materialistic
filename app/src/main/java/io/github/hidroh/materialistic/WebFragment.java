package io.github.hidroh.materialistic;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;
import javax.inject.Named;

import io.github.hidroh.materialistic.data.ItemManager;

public class WebFragment extends BaseFragment implements Scrollable {

    private static final String EXTRA_ITEM = WebFragment.class.getName() + ".EXTRA_ITEM";
    private ItemManager.WebItem mItem;
    private WebView mWebView;
    private NestedScrollView mScrollView;
    private boolean mIsHackerNewsUrl;
    private boolean mExternalRequired = false;
    @Inject @Named(ActivityModule.HN) ItemManager mItemManager;

    public static WebFragment instantiate(Context context, ItemManager.WebItem item) {
        final WebFragment fragment = (WebFragment) Fragment.instantiate(context, WebFragment.class.getName());
        fragment.mItem = item;
        fragment.mIsHackerNewsUrl = AppUtils.isHackerNewsUrl(item);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mIsHackerNewsUrl) {
            return createLocalView(container, savedInstanceState);
        }

        final View view = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_web, container, false);
        mScrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    mWebView.setBackgroundColor(Color.WHITE);
                    mWebView.setVisibility(mExternalRequired ? View.GONE : View.VISIBLE);
                }
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                if (getActivity() == null) {
                    return;
                }
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
                    return;
                }
                mExternalRequired = true;
                mWebView.setVisibility(View.GONE);
                view.findViewById(R.id.download_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.download_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                    }
                });
            }
        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        setWebViewSettings(mWebView.getSettings());
        return view;
    }

    private View createLocalView(ViewGroup container, Bundle savedInstanceState) {
        final View view = getLayoutInflater(savedInstanceState)
                .inflate(R.layout.fragment_web_hn, container, false);
        mScrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
        ((TextView) view.findViewById(R.id.posted))
                .setText(mItem.getDisplayedTime(getActivity(), false));
        if (mItem.getType().equals(ItemManager.Item.COMMENT_TYPE)) {
            view.findViewById(R.id.title).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.title)).setText(mItem.getDisplayedTitle());
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mIsHackerNewsUrl) {
            bindContent();
            return;
        }

        if (savedInstanceState != null) {
            ItemManager.WebItem savedItem = savedInstanceState.getParcelable(EXTRA_ITEM);
            if (savedItem != null) {
                mItem = savedItem;
            }
        }
        if (mItem != null) {
            mWebView.loadUrl(mItem.getUrl());
        }
    }

    private void bindContent() {
        if (mItem instanceof ItemManager.Item) {
            AppUtils.setTextWithLinks((TextView) getView().findViewById(R.id.text),
                    ((ItemManager.Item) mItem).getText());
        } else {
            mItemManager.getItem(mItem.getId(),
                    new ItemManager.ResponseListener<ItemManager.Item>() {
                        @Override
                        public void onResponse(ItemManager.Item response) {
                            AppUtils.setTextWithLinks((TextView) getView().findViewById(R.id.text),
                                    response.getText());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // do nothing
                        }
                    });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_ITEM, mItem);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setWebViewSettings(WebSettings webSettings) {
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }
    }

    @Override
    public void scrollToTop() {
        mScrollView.smoothScrollTo(0, 0);
    }
}
