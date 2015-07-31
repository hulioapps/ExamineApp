package com.apps.hulios.examineapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Boss on 2015-07-28.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    ArrayList<LinkSmallItem> mLinkSmallItems= new ArrayList<>();
    Context mContext;
    public CustomAdapter(ArrayList<LinkSmallItem> list){
        mLinkSmallItems = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_link_small, parent, false);

        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.getSmallText().setMaxLines(1);
        holder.getSmallText().setEllipsize(TextUtils.TruncateAt.END);

        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, GenericPageActivity.class);
                i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA, mLinkSmallItems.get(position).getLink());
                int extra = ((Activity) mContext).getIntent().getIntExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, 1);

                switch (extra) {
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


        LinkSmallItem lsi = mLinkSmallItems.get(position);
        holder.getName().setText(Html.fromHtml(lsi.getName()));
        String text = Html.fromHtml(lsi.getSmallText()).toString();
        if (text.length() > 3) {
            holder.getSmallText().setText(Html.fromHtml(lsi.getSmallText()));
        } else {
            ViewGroup tempVG = ((ViewGroup) holder.getSmallText().getParent());
            if(tempVG!=null)if(holder.getSmallText()!=null)tempVG.removeView(holder.getSmallText());
        }

    }

    @Override
    public int getItemCount() {
        return mLinkSmallItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final TextView smallText;
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView smallText = ((TextView) v.findViewById(R.id.list_link_small_smallText));
                    if (smallText != null) {
                        if (smallText.getEllipsize() == TextUtils.TruncateAt.END) {
                            smallText.setEllipsize(null);
                            smallText.setMaxLines(314159);
                        } else {
                            smallText.setEllipsize(TextUtils.TruncateAt.END);
                            smallText.setMaxLines(1);
                        }
                    }
                }
            });
            name = (TextView) itemView.findViewById(R.id.list_link_small_name);
            smallText = (TextView) itemView.findViewById(R.id.list_link_small_smallText);
            imageView = (ImageView) itemView.findViewById(R.id.list_link_small_imageView);
        }

        public TextView getName() {
            return name;
        }

        public TextView getSmallText() {
            return smallText;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
