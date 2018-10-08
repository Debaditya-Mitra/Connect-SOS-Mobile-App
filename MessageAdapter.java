package com.chatdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MessageVo> messageList;
    private OnItemClickInterface onItemClickInterface;
    private String userId = "";
    Double lat,longi;


    public MessageAdapter(Context context, ArrayList<MessageVo> messageList, OnItemClickInterface onItemClickInterface) {
        this.context = context;
        this.messageList = messageList;
        this.onItemClickInterface = onItemClickInterface;
         userId = SharedPreferenceUtil.getString("UserId", "");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.message_out_layout, parent, false);
            return new SendMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_in_layout, parent, false);
            return new ReceiveMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MessageVo messageVo = messageList.get(position);

        if (userId.equalsIgnoreCase(messageVo.getUserId())) { //sender
            if (messageVo.getMessage().contains("https:")) {
                Picasso.with(context)
                        .load(messageVo.getMessage())
                        .into(((SendMessageViewHolder) holder).imgViewOutChat);
                ((SendMessageViewHolder)holder).imgViewOutChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickInterface.onItemClick(messageVo.getMessage());

                    }
                });

                ((SendMessageViewHolder) holder).imgViewOutChat.setVisibility(View.VISIBLE);
                ((SendMessageViewHolder) holder).txtViewOutMsg.setVisibility(View.GONE);
                //((SendMessageViewHolder) holder).mapViewOut.setVisibility(View.GONE);

            } else if(messageVo.getMessage().contains("LatLong")) {
                List<String> strtList = Arrays.asList(messageVo.getMessage().split(","));
                lat = Double.valueOf(strtList.get(1));
                longi = Double.valueOf(strtList.get(2));
                if (((SendMessageViewHolder) holder).mapViewOut != null) {
                  //  SupportMapFragment supportMapFragment= ((chat)context).getSupportFragmentManager().findFragmentById(R.id.mapViewOut);
                     /*supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                         @Override
                         public void onMapReady(GoogleMap googleMap) {
                             GoogleMap map = googleMap;
                             googleMap.addMarker(new MarkerOptions()
                                     //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag))
                                     .anchor(0.0f, 1.0f)
                                     .position(new LatLng(lat,longi)));
                             googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                             googleMap.setMyLocationEnabled(true);
                             googleMap.getUiSettings().setZoomControlsEnabled(true);
                             // MapsInitializer.initialize(this.getActivity());
                             LatLngBounds.Builder builder = new LatLngBounds.Builder();
                             builder.include(new LatLng(lat, longi));
                             LatLngBounds bounds = builder.build();
                             int padding = 0;
                             // Updates the  location and zoom of the MapView
                             CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                             googleMap.moveCamera(cameraUpdate);
                         }
                     });*/
                   /* MapView mapView = ((SendMessageViewHolder) holder).mapViewOut;
                   ((SendMessageViewHolder) holder).mapViewOut.getMapAsync(new OnMapReadyCallback() {
                       @Override
                       public void onMapReady(GoogleMap googleMap) {
                           GoogleMap map = googleMap;
                           googleMap.addMarker(new MarkerOptions()
                                   //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag))
                                   .anchor(0.0f, 1.0f)
                                   .position(new LatLng(lat,longi)));
                           googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                           googleMap.setMyLocationEnabled(true);
                           googleMap.getUiSettings().setZoomControlsEnabled(true);
                           // MapsInitializer.initialize(this.getActivity());
                           LatLngBounds.Builder builder = new LatLngBounds.Builder();
                           builder.include(new LatLng(lat, longi));
                           LatLngBounds bounds = builder.build();
                           int padding = 0;
                           // Updates the location and zoom of the MapView
                           CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                           googleMap.moveCamera(cameraUpdate);
                       }
                   });*/
                }
                ((SendMessageViewHolder) holder).imgViewOutChat.setVisibility(View.GONE);
                ((SendMessageViewHolder) holder).txtViewOutMsg.setVisibility(View.GONE);
                //((SendMessageViewHolder) holder).mapViewOut.setVisibility(View.VISIBLE);

            }else{
                ((SendMessageViewHolder) holder).txtViewOutMsg.setText(messageVo.getMessage());
                ((SendMessageViewHolder) holder).imgViewOutChat.setVisibility(View.GONE);
                ((SendMessageViewHolder) holder).txtViewOutMsg.setVisibility(View.VISIBLE);
//                ((SendMessageViewHolder) holder).mapViewOut.setVisibility(View.GONE);

            }


        } else  { //receiver
            if (messageVo.getMessage().contains("https:")) {
                Picasso.with(context)
                        .load(messageVo.getMessage())
                       // .centerCrop()
                        .into(((ReceiveMessageViewHolder) holder).imgViewInChat);
                ((ReceiveMessageViewHolder) holder).txtViewInMsg.setVisibility(View.GONE);
                ((ReceiveMessageViewHolder) holder).mapViewIn.setVisibility(View.GONE);
                ((ReceiveMessageViewHolder) holder).imgViewInChat.setVisibility(View.VISIBLE);
                ((ReceiveMessageViewHolder)holder).imgViewInChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickInterface.onItemClick(messageVo.getMessage());

                    }
                });


            } else {
                ((ReceiveMessageViewHolder) holder).txtViewInMsg.setText(messageVo.getMessage());
                ((ReceiveMessageViewHolder) holder).imgViewInChat.setVisibility(View.GONE);
                ((ReceiveMessageViewHolder) holder).txtViewInMsg.setVisibility(View.VISIBLE);
                ((ReceiveMessageViewHolder) holder).mapViewIn.setVisibility(View.VISIBLE);

            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageVo messageVo = messageList.get(position);
        if (messageVo.getUserId().equalsIgnoreCase(userId)) {
            return 1;
        } else
            return 2;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView chat_screen;
        public TextView txtViewOutMsg;
        public ImageView imgViewOutChat;
        public SupportMapFragment mapViewOut;


        public SendMessageViewHolder(View itemView) {
            super(itemView);
            txtViewOutMsg = itemView.findViewById(R.id.txtViewOutMsg);
            imgViewOutChat = itemView.findViewById(R.id.imgViewOutChat);
            //mapViewOut = (R.id.mapViewOut);


        }
    }

    class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView chat_screen;
        public MapView mapViewIn;
        public TextView txtViewInMsg;
        public ImageView imgViewInChat;

        public ReceiveMessageViewHolder(View itemView) {
            super(itemView);
            txtViewInMsg = itemView.findViewById(R.id.txtViewInMsg);
            imgViewInChat = itemView.findViewById(R.id.imgViewInChat);
            mapViewIn = itemView.findViewById(R.id.mapViewIn);




        }
    }
}