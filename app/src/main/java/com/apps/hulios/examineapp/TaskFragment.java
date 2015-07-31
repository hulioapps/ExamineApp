package com.apps.hulios.examineapp;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {
    int mTimeout = 5000;
    public TaskFragment() {
    }
    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute(Document v, ArrayList<String[]> pageInfo, String pageTitle);
    }

    private TaskCallbacks mCallbacks;
    private AsyncTask<String,Void,Document> mTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        int extra = getActivity().getIntent().getIntExtra(GenericPageActivity.CURRENT_TYPE_EXTRA,1);
        switch(extra){
            case GenericPageActivity.TYPE_FAQ_ITEM:
                mTask = new GetFaqItemCodeTask();
                break;
            case GenericPageActivity.TYPE_STACKS:
                mTask = new GetStacksCodeTask();
                break;
            case GenericPageActivity.TYPE_STACK:
                mTask = new GetStacksCodeTask();
                break;
            case GenericPageActivity.TYPE_RUBRIC:
                mTask = new GetStacksCodeTask();
                break;
            default:
                mTask = new GetCodeTask();
                break;
        }
        mTask.execute(getActivity().getIntent().getStringExtra(GenericPageActivity.CURRENT_PAGE_EXTRA));

    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    // works for /supplements/ and /faq/ main pages
    private class GetCodeTask extends AsyncTask<String, Void, Document> {
        ArrayList<String[]> pageInfo;
        String pageTitle;
        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Document v) {
            if (mCallbacks != null) {

                if(pageTitle!=null) pageTitle = pageTitle.substring(0,pageTitle.indexOf("|"));
                mCallbacks.onPostExecute(v, pageInfo, pageTitle);
            }
        }

        @Override
        protected Document doInBackground(String... args) {
            Document pageCode = null;
            try {
                long time = System.currentTimeMillis();
                Connection con = Jsoup.connect(args[0]).followRedirects(true).timeout(mTimeout);
//                Log.d("time", "time after making connection" + (System.currentTimeMillis() - time));
                pageCode = con.get();
//                Log.d("time", "time after parsing page " + (System.currentTimeMillis() - time));

            } catch (IOException e) {
                pageInfo = null;
                return null;
            }


            pageInfo = new ArrayList<String[]>();
            Elements listTab = pageCode.getElementsByTag("section");
            for (Element e : listTab){
                String tabID = e.id();
//                Log.d("jakis ID",tabID);
                if(tabID.equals("summary_listing")){
                    String tabName = e.text();
                    tabName = tabName.substring(0,tabName.indexOf("Supplements"));
                    String[] temp = {tabName, "#"+tabID};
                    pageInfo.add(temp);
                }
            }

            Elements tabsNames = pageCode.select("ul.nav.nav-list.bs-docs-sidenav").select("li");
            for (Element e : tabsNames) {
                String tabName = e.text();
                String link = e.select("a").attr("href");
                if(link.equals("/contribute/faq-question/") || link.equals("/faq/potential/")){

                }else {
                    for(Element z:listTab){
                        if(link.equals("#"+z.id())){
                            String[] temp = {tabName, link};
                            pageInfo.add(temp);
                        }
                    }

                }

            }
            pageTitle = pageCode.select("title").first().text();
            return pageCode;
        }
    }

    //works for faq item
    private class GetFaqItemCodeTask extends AsyncTask<String, Void, Document> {
        ArrayList<String[]> pageInfo;
        String pageTitle;
        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Document v) {
            if (mCallbacks != null) {

                mCallbacks.onPostExecute(v,pageInfo, pageTitle);
            }
        }

        @Override
        protected Document doInBackground(String... args) {
            Document pageCode = null;
            try {
                long time = System.currentTimeMillis();
//                Connection.Response response = Jsoup.connect(args[0]).followRedirects(true).execute();
                Connection con = Jsoup.connect(args[0]).followRedirects(true).timeout(mTimeout);
//                Log.d("urllog","From response: "+response.url().toString());
//                Log.d("urllog","From connection: "+con.response().url().toString());
//                Log.d("time", "time after making connection" + (System.currentTimeMillis() - time));
                pageCode = con.get();
//                Log.d("time", "time after parsing page " + (System.currentTimeMillis() - time));
            } catch (IOException e) {
                pageInfo = null;
                return null;
            }

            pageInfo = new ArrayList<String[]>();

            String[] temp = {"Answer","#answer"};
            pageInfo.add(temp);

            String[] temp1 = {"Related Questions","#share_related"};
            pageInfo.add(temp1);

            String[] temp2 = {"Scientific Support & Reference Citations","#citations"};
            pageInfo.add(temp2);

            pageTitle = pageCode.select("title").first().text();

            return pageCode;
            }

        }

    private class GetStacksCodeTask extends AsyncTask<String, Void, Document> {
        ArrayList<String[]> pageInfo;
        String pageTitle;
        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Document v) {
            if (mCallbacks != null) {

                mCallbacks.onPostExecute(v,pageInfo,pageTitle);
            }
        }

        @Override
        protected Document doInBackground(String... args) {
            Document pageCode = null;
            try {
                long time = System.currentTimeMillis();
//                Connection.Response response = Jsoup.connect(args[0]).followRedirects(true).execute();
                Connection con = Jsoup.connect(args[0]).followRedirects(true).timeout(mTimeout);
//                Log.d("urllog","From response: "+response.url().toString());
//                Log.d("urllog","From connection: "+con.response().url().toString());
//                Log.d("time", "time after making connection" + (System.currentTimeMillis() - time));
                pageCode = con.get();
//                Log.d("time", "time after parsing page " + (System.currentTimeMillis() - time));
            } catch (IOException e) {
                pageInfo = null;
                return null;
            }

            pageInfo = new ArrayList<String[]>();
            pageTitle = pageCode.select("title").first().text();
            if(!pageTitle.contains("Rubric")) {
                String[] temp = {pageTitle.substring(0, pageTitle.indexOf("|")), ""};
                pageInfo.add(temp);
            } else {
                String[] temp = {pageTitle.substring(0, pageTitle.indexOf("|")), "#"};
                pageInfo.add(temp);
            }



            return pageCode;
        }

    }
    }



