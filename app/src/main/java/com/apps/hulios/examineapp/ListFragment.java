package com.apps.hulios.examineapp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ListFragment extends android.support.v4.app.ListFragment implements Loadable{
    public static final String ARG_ID = "id";
    private ArrayList<LinkSmallItem> mLinkSmallItems= new ArrayList<>();
    private boolean moreLoading;
    LinearLayout linlaHeaderProgress;

    static ListFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        ListFragment frag = new ListFragment();
        frag.setArguments(args);
        return frag;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mLinkSmallItems = (ArrayList<LinkSmallItem>)(savedInstanceState.getSerializable("array"));
            moreLoading = savedInstanceState.getBoolean("moreLoading");
        } else{
            new PopulateListTask(((GenericPageActivity)getActivity()).getCurrentPageDoc()).execute();
        }
        setListAdapter(new LinkSmallListAdapter(getActivity(),
                0,
                mLinkSmallItems));


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("array",mLinkSmallItems);
        outState.putBoolean("moreLoading", moreLoading);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
            ((LinkSmallListAdapter) getListAdapter()).notifyDataSetChanged();
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView smallText = ((TextView) v.findViewById(R.id.list_link_small_smallText));
        if(smallText!=null) {
            if(smallText.getEllipsize()== TextUtils.TruncateAt.END){
                smallText.setEllipsize(null);
                smallText.setMaxLines(314159);
            }else {
                smallText.setEllipsize(TextUtils.TruncateAt.END);
                smallText.setMaxLines(1);
            }
        }
    }

    @Override
    public void setLoading(boolean isLoading) {
        moreLoading = isLoading;
    }
    @Override
    public boolean isLoading(){
        return moreLoading;
    }


    private class PopulateListTask extends AsyncTask<String,Void,ArrayList<LinkSmallItem>> {
        Document doc;

        public PopulateListTask(Document document){
            doc = document;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linlaHeaderProgress = (LinearLayout)getActivity().findViewById(R.id.linlaHeaderProgress);
          //  Log.d("pager", "Start loading " + getArguments().getString("id"));
            moreLoading = true;
        }

        @Override
        protected void onPostExecute(ArrayList<LinkSmallItem> linkSmallItemList) {
            if(getActivity()!=null) {
                mLinkSmallItems = linkSmallItemList;

                setListAdapter(new LinkSmallListAdapter(getActivity(),
                        0,
                        mLinkSmallItems));
                moreLoading = false;
              //  Log.d("pager", "Stop loading " + getArguments().getString("id"));
                linlaHeaderProgress.setVisibility(View.GONE);
              //  Log.d("pager", "not Visible");
            }
        }


        @Override
        protected ArrayList<LinkSmallItem> doInBackground(String... id) {
            ArrayList<LinkSmallItem> linkSmallItemList = new ArrayList<LinkSmallItem>();
            long time= System.currentTimeMillis();
            Elements sections = doc.getElementsByTag("section").select(getArguments().getString(ARG_ID)).select("li");
            for(Element e :sections) {
                LinkSmallItem linkSmallItem = new LinkSmallItem();
                //get <a>
                Element a = e.select("a").first();
                //get link <a href>
                String link = a.attr("abs:href");
                //get text <a> </a>
                String name = a.text();
                //get text <small> </small>
                String small = e.select("small").text();

                linkSmallItem.setLink(link);
                linkSmallItem.setName(name);
                linkSmallItem.setSmallText(small);

                linkSmallItemList.add(linkSmallItem);
//            Log.d("section", "LINK " +link);
//            Log.d("section", "NAME "+name);
//            Log.d("section", "SMALL "+small);
            }
            return linkSmallItemList;
        }
    }


}
