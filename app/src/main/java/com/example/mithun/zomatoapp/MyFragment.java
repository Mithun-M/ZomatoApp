package com.example.mithun.zomatoapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {
    Button button;
    RecyclerView recyclerView;
    LocationManager manager;
    MyAdapter myAdapter;
    MyTask myTask;
    LinearLayoutManager linearLayoutManager;
    double latitude, longitude;
    ArrayList<Resturant> resturants;
    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getActivity(), "got out location..", Toast.LENGTH_SHORT).show();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
//since we got location ,stop location listener
            manager.removeUpdates(listener);

            //start asynctask with zomato url
            myTask.execute("https://developers.zomato.com/api/v2.1/geocode?lat=" + latitude + "&lon=" + longitude);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public boolean checkInternet() {
        ConnectivityManager mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isConnected() == true) {
            return true; //means internet is active
        } else {
            return false;//means no internet
        }

    }


    public class MyTask extends AsyncTask<String, Void, String> {
        URL url;
        HttpURLConnection con;
        InputStream i;
        InputStreamReader ir;
        BufferedReader br;
        String s;
        StringBuilder sb;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(strings[0]);
                con = (HttpURLConnection) url.openConnection();

                //extra setup to connect to zomato server
                //-----------------------------------------------------------
                con.addRequestProperty("Content-Type", "application/json");
                con.addRequestProperty("user-key", "6c83ac32ed9fa9fb836697c749632a04");
                //-----------------------------------------------------------

                i = con.getInputStream();
                ir = new InputStreamReader(i);
                br = new BufferedReader(ir);
                s = br.readLine();
                sb = new StringBuilder();
                while (s != null) {
                    sb.append(s);
                    s = br.readLine();
                }
                return sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("B40", "url problem");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("B40", "network problem");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity(), "JSON DATA =" + s, Toast.LENGTH_SHORT).show();
            Log.e("json data", s);
//write json parsing logic
            if (s == null) {
                Toast.makeText(getActivity(), "no response from server", Toast.LENGTH_SHORT).show();
                return;
            } else {

            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray arr = jsonObject.getJSONArray("nearby_restaurants");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    JSONObject rest = obj.getJSONObject("restaurant");
                    String hotel_name = rest.getString("name");
                    JSONObject loc = rest.getJSONObject("location");
                    String hotle_address = loc.getString("address");

                    double hotel_latitude = Double.parseDouble(loc.getString("latitude"));
                    double hotel_longitude = Double.parseDouble(loc.getString("longitude"));
                    String hotel_cuisines = rest.getString("cuisines");
                    String hotel_image_url = rest.getString("thumb");

                    JSONObject user_rating = rest.getJSONObject("user_rating");
                    float hotel_rating = Float.parseFloat(user_rating.getString("aggregate_rating"));

//create reataurant object with above details
                    Resturant r = new Resturant(hotel_name, hotle_address, hotel_cuisines, hotel_image_url, hotel_rating, hotel_latitude,hotel_longitude);

                    //now add rrstaurant object to array list
                    resturants.add(r);

                    //now tell to adapter
                    myAdapter.notifyDataSetChanged();

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "json parsing error" + e, Toast.LENGTH_SHORT).show();
            }


            super.onPostExecute(s);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            Resturant res = resturants.get(position);//read from arraylist

            holder.tv1.setText(res.getName());
            holder.tv2.setText(res.getAddress());
            holder.tv3.setText(res.getCuisine());
            holder.ratingBar.setRating(res.getRating());

            //display hotel image
            Glide.with(getActivity()).load(res.getImageurl()).into(holder.imageView);

        }

        @Override
        public int getItemCount() {
            return resturants.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv1, tv2, tv3;
            public ImageView imageView;
            public RatingBar ratingBar;
            public CardView cv1;

            public ViewHolder(View itemView) {
                super(itemView);
                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                imageView = itemView.findViewById(R.id.imageview);
                ratingBar = itemView.findViewById(R.id.ratingbar);
cv1=itemView.findViewById(R.id.cardview1);
cv1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int pos=getAdapterPosition();//where user clicks

        Resturant res=resturants.get(pos);
        //now let us start maps activity
        Intent i=new Intent(getActivity(),MapsActivity.class);
        i.putExtra("name",res.getName());
        i.putExtra("add",res.getAddress());
        i.putExtra("lat",res.getLat());
        i.putExtra("lon",res.getLon());
        startActivity(i);
    }
});
            }
        }
    }

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my, container, false);
        button = v.findViewById(R.id.button);
        recyclerView = v.findViewById(R.id.recyclerview1);
        resturants = new ArrayList<Resturant>();
        myAdapter = new MyAdapter();
        myTask = new MyTask();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //establish all links
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternet() == true) {
                    manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(getActivity(), "location permission denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 100, listener);

                } else {
                    Toast.makeText(getActivity(), "no internet", Toast.LENGTH_SHORT).show();
                    Log.d("internet", "not connected");
                }
            }
        });

        return v;
    }

}
