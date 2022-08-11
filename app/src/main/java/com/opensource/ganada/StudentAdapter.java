package com.opensource.ganada;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    Context context;

    ArrayList<StudentItem> items = new ArrayList<StudentItem>();


    public StudentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.student_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentItem item = items.get(position);
        holder.setItem(item);
    }

    public void addItem(StudentItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<StudentItem> items) {
        this.items = items;
    }

    public StudentItem getItem(int position) {
        return items.get(position);
    }

    static  class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentAge;
        TextView studentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentAge = (TextView) itemView.findViewById(R.id.studentAge);
            studentName = (TextView) itemView.findViewById(R.id.studentName);
        }

        public void setItem(StudentItem item) {
            studentAge.setText(Integer.toString(item.getAge()));
            studentName.setText(item.getName());
        }

    }
}
