package com.apps.hulios.examineapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Boss on 2015-07-07.
 */
public class GetLinkSmallItemListTask extends AsyncTask<String,Void,ArrayList<LinkSmallItem>>{
    String mStart;
    Document doc;

    public GetLinkSmallItemListTask(Document doc ){
        this.doc = doc;
    }
    public String cutString(String s, String start){
        String temp = start;
        s = s.substring(s.indexOf(start)+start.length());
        return s;
    }
    @Override

    protected ArrayList<LinkSmallItem> doInBackground(String... id) {
        ArrayList<LinkSmallItem> linkSmallItemList = new ArrayList<LinkSmallItem>();

        Elements sections = doc.getElementsByTag("section").select(id[0]).select("li");

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
