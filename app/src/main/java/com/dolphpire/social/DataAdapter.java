package com.dolphpire.social;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

import com.dolphpire.social.preferences.Users;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;

/**
 * Created by rishad on 22/5/18.
 */

public class DataAdapter  extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements Filterable {

    private ArrayList<Users> mArrayList;
    private ArrayList<Users> mFilteredList;

    public DataAdapter(ArrayList<Users> arrayList) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.name.setText(mFilteredList.get(i).getFirst_name());
        viewHolder.email.setText(mFilteredList.get(i).getEmail());

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<Users> filteredList = new ArrayList<>();

                    for (Users users : mArrayList) {

                        if (users.getFirst_name().toLowerCase().contains(charString) || users.getEmail().toLowerCase().contains(charString) ) {

                            filteredList.add(users);
                        }

                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Users>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name,email;
        public ViewHolder(View view) {
            super(view);

            name = (TextView)view.findViewById(R.id.name_text);
            email = (TextView)view.findViewById(R.id.email_text);

        }
    }
}
