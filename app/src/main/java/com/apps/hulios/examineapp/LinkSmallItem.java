package com.apps.hulios.examineapp;

import java.io.Serializable;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Boss on 2015-07-07.
 */
public class LinkSmallItem implements Serializable{
    private UUID mId;
    private String mName;
    private String link;
    private String mSmallText;

    public LinkSmallItem(){
        mId = UUID.randomUUID();
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSmallText() {
        return mSmallText;
    }

    public void setSmallText(String smallText) {
        mSmallText = smallText;
    }

    public UUID getId() {
        return mId;
    }

}
