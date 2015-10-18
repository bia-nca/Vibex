package de.girlsgeek.vibex;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventListAdapter extends BaseAdapter {

    String [] event_names;
    String [] venue_names;
    String[] urls;

    Context context;
    private static LayoutInflater inflater=null;

    public EventListAdapter(VibexActivity activity, String[] events, String[] venues, String[] url_list) {

        context = activity;
        event_names = events;
        venue_names = venues;
        urls = url_list;

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return event_names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView title;
        TextView location;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.event_list_item, null);
        holder.title = (TextView) rowView.findViewById(R.id.title);
        holder.location = (TextView) rowView.findViewById(R.id.place);
        holder.title.setText(event_names[position]);
        holder.location.setText(venue_names[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = urls[position];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
        return rowView;
    }

}
