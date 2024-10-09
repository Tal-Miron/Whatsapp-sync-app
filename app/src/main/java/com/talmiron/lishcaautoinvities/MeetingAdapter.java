package com.talmiron.lishcaautoinvities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {
    private List<Meeting> meetingList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Meeting meeting);
    }

    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewTitle, textViewDatetime;

        public MeetingViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDatetime = itemView.findViewById(R.id.textViewDatetime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick((Meeting) v.getTag());
                    }
                }
            });
        }
    }


    public MeetingAdapter(List<Meeting> meetingList, OnItemClickListener listener) {
        this.meetingList = meetingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting, parent, false);
        return new MeetingViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Meeting meeting = meetingList.get(position);
        holder.textViewName.setText(meeting.getName());
        holder.textViewTitle.setText(meeting.getTitle());
        holder.textViewDatetime.setText(meeting.getDate() +"  |  " + meeting.getHour());
        holder.itemView.setTag(meeting);

        // Set item height equal to its width
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = layoutParams.width;
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }
}
