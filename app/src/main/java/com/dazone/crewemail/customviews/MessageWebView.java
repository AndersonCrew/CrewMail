package com.dazone.crewemail.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebSettings;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;


public class MessageWebView extends RigidWebView {

    private Context mContext;

    public MessageWebView(Context context) {
        super(context);
        mContext = context;
    }

    public MessageWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public MessageWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

    }

    /**
     * Configure a web view to load or not load network data. A <b>true</b> setting here means that
     * network data will be blocked.
     *
     * @param shouldBlockNetworkData True if network data should be blocked, false to allow network data.
     */
    public void blockNetworkData(final boolean shouldBlockNetworkData) {
        /*
         * Block network loads.
         *
         * Images with content: URIs will not be blocked, nor
         * will network images that are already in the WebView cache.
         *
         */
        getSettings().setBlockNetworkLoads(shouldBlockNetworkData);
    }


    /**
     * Configure a {@link android.webkit.WebView} to display a Message. This method takes into account a user's
     * preferences when configuring the view. This message is used to view a message and to display a message being
     * replied to.
     */
    public void configure() {

        this.setLongClickable(true);
        boolean isAdjust = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH, true);

        if (isAdjust) {
            this.setInitialScale(400);
        } else {
            this.setInitialScale(1);
        }


        final WebSettings webSettings = this.getSettings();

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        disableDisplayZoomControls();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);



        webSettings.setTextZoom(DaZoneApplication.getFontSizes().getMessageViewContentAsPercent());

        blockNetworkData(false);
    }

    /**
     * Disable on-screen zoom controls on devices that support zooming via pinch-to-zoom.
     */
    private void disableDisplayZoomControls() {
        PackageManager pm = getContext().getPackageManager();
        boolean supportsMultiTouch =
                pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH) ||
                        pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);

        getSettings().setDisplayZoomControls(!supportsMultiTouch);
    }
    @SuppressLint("NewApi")
    private void openWebView(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        } else {
            this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        String data = "<div>" +text+ "</div>";
        this.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
    }

    private String getHtmlData(String bodyHTML) {
        String content = "<html><head><style>img{max-width: 100%; width:auto; height: auto;}</style>" +
                "<meta name=\"viewport\" content=\"width=auto, initial-scale=1\"/>";
        content += "</head><body>" + bodyHTML + "</body></html>";

        return content;
    }
    /**
     * Load a message body into a {@code MessageWebView}
     * <p/>
     * <p>
     * Before loading, the text is wrapped in an HTML header and footer
     * so that it displays properly.
     * </p>
     *
     * @param text The message body to display.  Assumed to be MIME type text/html.
     */
    public void setText(String text) {
        openWebView(text);
        resumeTimers();
    }
}
