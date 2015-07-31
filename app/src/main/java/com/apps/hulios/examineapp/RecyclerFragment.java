package com.apps.hulios.examineapp;



import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecyclerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecyclerFragment extends Fragment implements Loadable{
    public static final String ARG_ID = "id";

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<LinkSmallItem> mLinkSmallItems= new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    LinearLayout linlaHeaderProgress;
    private boolean moreLoading;

    // TODO: Rename and change types and number of parameters
    public static RecyclerFragment newInstance(String id) {
        RecyclerFragment fragment = new RecyclerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public RecyclerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mLinkSmallItems = (ArrayList<LinkSmallItem>)(savedInstanceState.getSerializable("array"));
            moreLoading = savedInstanceState.getBoolean("moreLoading");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(mLinkSmallItems.size()==0){
            new PopulateListTask(((GenericPageActivity)getActivity()).getCurrentPageDoc()).execute();
        }
        mAdapter = new CustomAdapter(mLinkSmallItems);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("array",mLinkSmallItems);
        outState.putBoolean("moreLoading", moreLoading);
        super.onSaveInstanceState(outState);
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
            moreLoading = true;
        }

        @Override
        protected void onPostExecute(ArrayList<LinkSmallItem> linkSmallItemList) {
            if(getActivity()!=null) {

                mLinkSmallItems = linkSmallItemList;
                mRecyclerView.setAdapter(new CustomAdapter(
                        mLinkSmallItems
                ));

                moreLoading = false;
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        }


        @Override
        protected ArrayList<LinkSmallItem> doInBackground(String... id) {
            ArrayList<LinkSmallItem> linkSmallItemList = new ArrayList<LinkSmallItem>();
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
            }
            return linkSmallItemList;
        }
    }


}
