package com.alejandrocaralt.icarus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alejandrocaralt.icarus.Models.Event;
import com.alejandrocaralt.icarus.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context mCtx;
    private List<Event> eventList;
    private final OnItemClickListener listener;
    private Integer position;

    public EventAdapter(Context mCtx, List<Event> eventList, OnItemClickListener listener) {
        this.mCtx = mCtx;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_item, viewGroup, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int position) {
        final Event event = eventList.get(position);
        eventViewHolder.bind(event, listener);

        eventViewHolder.textEventName.setText(event.getName());
        eventViewHolder.textEventKm.setText(String.valueOf(event.getKm()) + " Km");
        eventViewHolder.textEventCity.setText(event.getCity());
        eventViewHolder.textEventOwner.setText(event.getOwner());
        eventViewHolder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ciclismo));

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class  EventViewHolder extends  RecyclerView.ViewHolder {
        TextView textEventName, textEventKm, textEventCity, textEventOwner;
        ImageView imageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    position = getAdapterPosition() ;
                    return false;
                }
            });


            textEventName = itemView.findViewById(R.id.rec_event_name);
            textEventKm = itemView.findViewById(R.id.rec_event_km);
            textEventCity = itemView.findViewById(R.id.rec_event_city);
            textEventOwner = itemView.findViewById(R.id.rec_event_owner);
            imageView = itemView.findViewById(R.id.rec_img);

        }

        public void bind(final Event event,final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(event);
                }
            });
        }
    }

    public Integer getPosition() {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }
}
