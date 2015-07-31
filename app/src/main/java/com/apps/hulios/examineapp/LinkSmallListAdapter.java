package com.apps.hulios.examineapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Boss on 2015-07-08.
 */
public class LinkSmallListAdapter extends ArrayAdapter<LinkSmallItem> {
        Context mContext;
        ArrayList<LinkSmallItem> mLinkSmallItems;
        int mResource;
    public LinkSmallListAdapter(Context context, int resource, ArrayList<LinkSmallItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mLinkSmallItems = objects;
        }


@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list_link_small, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.list_link_small_name);

            holder.smallText = (TextView) convertView.findViewById(R.id.list_link_small_smallText);
            holder.smallText.setMaxLines(1);
            holder.smallText.setEllipsize(TextUtils.TruncateAt.END);

            holder.image = (ImageView) convertView.findViewById(R.id.list_link_small_imageView);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext,GenericPageActivity.class);
                    i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA,mLinkSmallItems.get(position).getLink());
                    int extra = ((Activity)mContext).getIntent().getIntExtra(GenericPageActivity.CURRENT_TYPE_EXTRA,1);

                    switch (extra){
                        case GenericPageActivity.TYPE_SUPPLEMENTS:
                            i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_SUPPLEMENT);
                            break;
                        case GenericPageActivity.TYPE_FAQ:
                            i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_FAQ_ITEM);
                            break;
                        case GenericPageActivity.TYPE_TOPICS:
                            i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_TOPIC);
                    }

                    i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    mContext.startActivity(i);
                   // Toast.makeText(mContext,"View clicked at pos "+position+" Extras "+extra,Toast.LENGTH_SHORT).show();
                }
            });
            convertView.setTag(holder);
        }
            ViewHolder mainHolder = (ViewHolder) convertView.getTag();
            LinkSmallItem lsi = mLinkSmallItems.get(position);
            mainHolder.name.setText(Html.fromHtml(lsi.getName()));
            String text = Html.fromHtml(lsi.getSmallText()).toString();
            if (text.length() > 3) {
                mainHolder.smallText.setText(Html.fromHtml(lsi.getSmallText()));
            } else {
                ViewGroup tempVG = ((ViewGroup) mainHolder.smallText.getParent());
               if(tempVG!=null)if(mainHolder.smallText!=null)tempVG.removeView(mainHolder.smallText);
            }
        return convertView;

        }
    private class ViewHolder {
        public TextView name;
        public TextView smallText;
        public ImageView image;
    }
}
