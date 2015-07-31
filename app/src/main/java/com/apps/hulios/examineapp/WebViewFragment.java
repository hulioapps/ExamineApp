package com.apps.hulios.examineapp;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebViewFragment extends Fragment implements Loadable{
    private static final String ARG_ID = "id";

    private String mId;
    private boolean moreLoading;
    WebView mWebView;
    private String text;
    private Bundle webViewBundle;
    private LinearLayout linlaHeaderProgress;

    public static WebViewFragment newInstance(String id) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID,id);
        fragment.setArguments(args);
        return fragment;
    }

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        webViewBundle = new Bundle();
        webViewBundle.clear();
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            if(mWebView == null) {
                linlaHeaderProgress = (LinearLayout)getActivity().findViewById(R.id.linlaHeaderProgress);
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                mWebView = new WebView(getActivity());
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                mWebView.setWebViewClient(new MyWebViewClient());
                mWebView.setWebChromeClient(new WebChromeClient());
                mWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));
                mWebView.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
                        if (hr != null) {
                            if (hr.getExtra() != null) {
                                if (hr.getExtra().contains("#ref")) {
                                    ((GenericPageActivity) getActivity()).setRefTab(
                                            hr.getExtra().substring(hr.getExtra().indexOf("#"))
                                    );
                                }
                            }
                        }
                        return false;
                    }
                });
                new GetContentTask(((GenericPageActivity) getActivity()).getCurrentPageDoc()).execute();
            }
        return mWebView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void setLoading(boolean isLoading) {
        moreLoading = isLoading;
    }

    @Override
    public boolean isLoading() {
        return moreLoading;
    }

    public void scrollToTop(){
        if(mWebView !=null){
            mWebView.loadUrl("javascript:$('html, body').animate({scrollTop:0}, 'fast');");
        }
    }

    public void scrollToCitation(String ref){
        if(mWebView !=null){
            mWebView.loadUrl("javascript:$('html, body').animate({\n" +
                    "        scrollTop: $(\""+ref+"\").offset().top\n" +
                    "    }, 2000);");
        }
    }



    private class GetContentTask extends AsyncTask<Void,Void,Void>{
        Document doc;

        public GetContentTask(Document document){
            doc = document;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linlaHeaderProgress = (LinearLayout)getActivity().findViewById(R.id.linlaHeaderProgress);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            setLoading(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(text == null) {
                String id = getArguments().getString("id");
                if(id.length()>2) {
                    Elements elements = doc.getElementsByTag("section").select(id);
                    text = elements.outerHtml();
                    StringBuilder sb = new StringBuilder();
                    sb.append("<HTML><HEAD><LINK href=\"styles.css\" type=\"text/css\" rel=\"stylesheet\"/>" +
                            "<script src=\"scripts/jquery-2.1.4.min.js\"></script>" +
                            "<script type=\"text/javascript\" charset=\"utf-8\">" +
                            "function toggleShow(selector) {\n" +
                            "   $(document.getElementById(selector)).toggle(\"fast\",\"swing\");  \n" +
                            "}" +
                            "</script>" +
                            "</HEAD><body>");
                    sb.append(text.toString());
                    sb.append("</body></HTML>");
                    text = sb.toString();
                } else if (id.length()==1){
                    Elements elements = doc.select("div.inner-body");
                    text = elements.outerHtml();
                    StringBuilder sb = new StringBuilder();
                    sb.append("<HTML><HEAD><LINK href=\"styles.css\" type=\"text/css\" rel=\"stylesheet\"/>" +
                            "<script src=\"scripts/jquery-2.1.4.min.js\"></script>" +
                            "<script type=\"text/javascript\" charset=\"utf-8\">" +
                            "function toggleShow(selector) {\n" +
                            "   $(document.getElementById(selector)).toggle(\"fast\",\"swing\");  \n" +
                            "}" +
                            "</script>" +
                            "</HEAD><body>");
                    sb.append(text.toString());
                    sb.append("</body></HTML>");
                    text = sb.toString();
                }

                else
                {
                    String body = doc.body().outerHtml();
                    String start = "<div class=\"alert alert-error\">";
                    String end = "<div class=\"span3 bs-docs-sidebar\">";
                    text = body.substring(body.indexOf(start),body.indexOf(end));
                    StringBuilder sb = new StringBuilder();
                    sb.append("<HTML><HEAD><LINK href=\"styles.css\" type=\"text/css\" rel=\"stylesheet\"/>" +
                            "<script src=\"scripts/jquery-2.1.4.min.js\"></script>" +
                            "<script type=\"text/javascript\" charset=\"utf-8\">" +
                            "function toggleShow(selector) {\n" +
                            "   $(document.getElementById(selector)).toggle(\"fast\",\"swing\");  \n" +
                            "}" +
                            "</script>" +
                            "</HEAD><body>");
                    sb.append(text.toString());
                    sb.append("</body></HTML>");
                    text = sb.toString();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mWebView !=null) {
                mWebView.loadDataWithBaseURL("file:///android_asset/", text, "text/html", "utf-8", null);
                linlaHeaderProgress.setVisibility(View.GONE);
                setLoading(false);
            }

        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            String temp ="file:///";
            url = url.substring(url.indexOf(temp)+temp.length());

            if(url.contains("supplements/") || url.contains("top/")) {

                if(!url.contains("examine.com")) {
                    if (!url.startsWith("http://www.examine.com/"))
                        url = "http://www.examine.com/" + url;
                } else {
                    if (!url.startsWith("http://www.")){
                        url = "http://www." + url;
                    }
                }
                Intent i = new Intent(getActivity(),GenericPageActivity.class);
                i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA,url);
                i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_SUPPLEMENT);
                i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(i);
            }else if(url.contains("disclaimer")){
                ((GenericPageActivity) getActivity()).showDisclaimer();
            } else if(url.contains("faq/")){

                if(!url.contains("examine.com")) {
                    if (!url.startsWith("http://www.examine.com/"))
                        url = "http://www.examine.com/" + url;
                } else {
                    if (!url.startsWith("http://www.")){
                        url = "http://www." + url;
                    }
                }
                Intent i = new Intent(getActivity(),GenericPageActivity.class);
                i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA,url);
                i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_FAQ_ITEM);
                i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(i);
            } else if (url.contains("topics/")) {
                if(!url.contains("examine.com")) {
                    if (!url.startsWith("http://www.examine.com/"))
                        url = "http://www.examine.com/" + url;
                } else {
                    if (!url.startsWith("http://www.")){
                        url = "http://www." + url;
                    }
                }
                Intent i = new Intent(getActivity(),GenericPageActivity.class);
                i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA,url);
                i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_TOPIC);
                i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(i);
            } else if (url.contains("stacks/")) {
                if(!url.contains("examine.com")) {
                    if (!url.startsWith("http://www.examine.com/"))
                        url = "http://www.examine.com/" + url;
                } else {
                    if (!url.startsWith("http://www.")){
                        url = "http://www." + url;
                    }
                }
                Intent i = new Intent(getActivity(),GenericPageActivity.class);
                i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA,url);
                i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_STACK);
                i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(i);
            } else if(url.contains("user/") || url.contains("refer/"))
            {
                if(!url.contains("examine.com")) {
                    if (!url.startsWith("http://www.examine.com/"))
                        url = "http://www.examine.com/" + url;
                } else {
                    if (!url.startsWith("http://www.")){
                        url = "http://www." + url;
                    }
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } else if(url.contains("rubric/")){
                if(!url.contains("examine.com")) {
                    if (!url.startsWith("http://www.examine.com/"))
                        url = "http://www.examine.com/" + url;
                } else {
                    if (!url.startsWith("http://www.")){
                        url = "http://www." + url;
                    }
                }
                Intent i = new Intent(getActivity(),GenericPageActivity.class);
                i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA,url);
                i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_RUBRIC);
                i.setData(Uri.parse(url));
                startActivity(i);
            }


            else
            {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            return true;
//            if (Uri.parse(url).getHost().equals("www.example.com")) {
//                // This is my web site, so do not override; let my WebView load the page
//                return false;
//            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
//            return true;
        }
    }


}
