package com.opensource.ganada;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;

    ArrayList<PostItem> items = new ArrayList<PostItem>();

    OnItemClickListener listener;

    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public PostAdapter(Context context) { this.context = context; }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.post_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostItem item = items.get(position);
        holder.setItem(item);

        holder.setOnItemClickListener(listener);
    }

    public void addItem(PostItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<PostItem> items) { this.items = items; }

    public PostItem getItem(int position) {
        return items.get(position);
    }

    public void clearItem() {
        items.clear();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView post_title;
        TextView post_writer;
        TextView post_date;
        OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_writer = (TextView) itemView.findViewById(R.id.post_writer);
            post_date = (TextView) itemView.findViewById(R.id.post_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }
        public void setItem(PostItem item) {
            post_title.setText(item.getTitle());
            if(!item.isAnnoymity())
                post_writer.setText(item.getWriter());
            else
                post_writer.setText("anonymous");
            post_date.setText(item.getDate());
        }
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
