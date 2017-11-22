package com.handen.schoolhelper2.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Settings;
import com.handen.schoolhelper2.fragments.LessonListFragment.OnListFragmentInteractionListener;
import com.handen.schoolhelper2.fragments.Week.DayContent;
import java.util.ArrayList;
import java.util.List;

public class LessonListRecyclerViewAdapter extends RecyclerView.Adapter<LessonListRecyclerViewAdapter.ViewHolder> {

    private final List<DayContent.DayItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public LessonListRecyclerViewAdapter(List<DayContent.DayItem> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        holder.mIdView.setText((position + 1) + ".");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
        holder.mSpinner.setAdapter(spinnerAdapter);

        if(MainActivity.schedule.get(MainActivity.choosedDay).get(holder.getAdapterPosition()) != -1)
        {
            holder.mSpinner.setSelection(MainActivity.schedule.get(MainActivity.choosedDay).get(position));
        }
        else
            holder.mSpinner.setSelection(MainActivity.subjectArrayList.size());

        holder.mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int adapterPosition, long id) {

                if (MainActivity.schedule.size() > 0 && MainActivity.choosedDay >= 0 && MainActivity.choosedDay < MainActivity.schedule.size()) {
                    ArrayList<Integer> list = MainActivity.schedule.get(MainActivity.choosedDay);
                    if(adapterPosition != MainActivity.subjectArrayList.size())
                        MainActivity.schedule.get(MainActivity.choosedDay).set(position,adapterPosition);
                    else
                        MainActivity.schedule.get(MainActivity.choosedDay).set(position, -1);

                    MainActivity.schedule.set(MainActivity.choosedDay, list);
                 mListener.saveSchedule();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return Settings.TOTALLESSONS;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final Spinner mSpinner;
        public DayContent.DayItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.lessonNumber);
            mSpinner = (Spinner) view.findViewById(R.id.lessonSpinner);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + " " + "'";
        }
    }

    public class SpinnerAdapter extends BaseAdapter {

        public void SpinnerAdapter() {

        }

        @Override
        public int getCount() {
            return MainActivity.subjectArrayList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, null);

            TextView nameTextView = (TextView) view.findViewById(R.id.textView);
            String text = "";
            if (position != getCount() - 1)
                text = MainActivity.subjectArrayList.get(position).getName();
            else
                nameTextView.setText(R.string.defaultLessonName);
            if (!text.equals(""))
                nameTextView.setText(text);
            return view;
        }
    }
}
