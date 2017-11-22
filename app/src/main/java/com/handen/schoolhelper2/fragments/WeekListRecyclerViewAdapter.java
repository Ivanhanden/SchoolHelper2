package com.handen.schoolhelper2.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Settings;
import com.handen.schoolhelper2.fragments.Week.Day;


import java.util.List;

/**
 Класс адаптера фрагмента с днями недели
 */
public class WeekListRecyclerViewAdapter extends RecyclerView.Adapter<WeekListRecyclerViewAdapter.ViewHolder> {

    private final List<Day> days;
    private final WeekListFragment.startLessonsListFragment mListener;

    public WeekListRecyclerViewAdapter(List<Day> items, WeekListFragment.startLessonsListFragment listener) {
        days = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_week, parent, false);
        return new ViewHolder(view);
    }

    /**
     *Биндим ViewHolder
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.day = days.get(position);
        //holder.mTotalView.setText(mValues.get(position).id);
//        holder.mTotalView.setText( MainActivity.schedule.get(position).size());
        //holder.mNameView.setText(mValues.get(position).content);
        holder.mNameView.setText(MainActivity.days.get(position).getName());
        int total = 0;
        for(int i = 0; i < Settings.TOTALLESSONS; i ++)
        {
            int currentLesson = MainActivity.schedule.get(position).get(i);
            if(currentLesson != -1)
                total++;
        }
//        for(int a : MainActivity.schedule.get(position))
//        {
//            if(a != -1)
//                total++;
//        }
        holder.mTotalView.setText(Integer.toString(total));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.startLessonsListFragment(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        /**
         * TV с количеством уроков в этот день
         */
        public final TextView mTotalView;
        /**
         * Название дня недели
         */
        public final TextView mNameView;
        public Day day;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTotalView = (TextView) view.findViewById(R.id.totalLessons);
            mNameView = (TextView) view.findViewById(R.id.dayName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
