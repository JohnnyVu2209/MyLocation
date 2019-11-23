package com.example.mylocation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindowAdapter implements InfoWindowAdapter {

    private Activity context;
    private Bitmap btmp;
    public MyInfoWindowAdapter(Activity context)
    {
        this.context=context;

    }
    @Override
    public View getInfoContents(Marker arg0) {
// Getting view from the layout file info_window_layout
        View v = this.context.getLayoutInflater().inflate(R.layout.custom_info, null);

// Getting the position from the marker
        LatLng latLng = arg0.getPosition();

// Getting reference to the TextView to set latitude
        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

// Getting reference to the TextView to set longitude
        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);

        TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);



// Setting the latitude
        tvLat.setText("Latitude:" + latLng.latitude);

// Setting the longitude
        tvLng.setText("Longitude:"+ latLng.longitude);

        tvTitle.setText(arg0.getTitle());
        tvSnippet.setText(arg0.getSnippet());
        return v;
    }

    @Override
    public View getInfoWindow(Marker arg0) {

        return null;
    }

}
