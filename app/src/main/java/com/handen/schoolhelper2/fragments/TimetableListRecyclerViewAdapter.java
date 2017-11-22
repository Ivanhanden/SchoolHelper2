package com.handen.schoolhelper2.fragments;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Settings;
import com.handen.schoolhelper2.fragments.Timetable.Timetable;
import com.handen.schoolhelper2.fragments.Week.Day;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Timetable} and makes a call to the
 */

public class TimetableListRecyclerViewAdapter extends RecyclerView.Adapter<TimetableListRecyclerViewAdapter.ViewHolder> {


    private List<Timetable> mValues;
    private final TimetableListFragment.saveTimetable mListener;

    public TimetableListRecyclerViewAdapter(List<Timetable> items, TimetableListFragment.saveTimetable listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_timetable, parent, false);
        ListView listView = (ListView) view.findViewById(R.id.lessonsLV);
//        listView.setAdapter(null);
//        notifyDataSetChanged();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.days.setText(mValues.get(position).content);
        holder.daysNames = "";
        TimetableAdapter timetableAdapter = new TimetableAdapter(holder.getAdapterPosition());
        holder.listView.setAdapter(timetableAdapter);

//        holder.timetable = MainActivity.timetables.get(position);

        for (Day day : MainActivity.days) {
            if(day.getTimetableIndex() == holder.getAdapterPosition()) {
                if (holder.daysNames.length() != 0)
                    holder.daysNames += ", ";
                holder.daysNames += day.getName();
            }
        }
        holder.days.setText(holder.daysNames);

        holder.days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showDaysDialog(holder.getAdapterPosition(), holder.days);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Показываем диалог с подтверждением удаления

                boolean isFound = false;

                for(Day day : MainActivity.days) {
                    if (day.getTimetableIndex() == position) {
                        isFound = true;
                    }
                }

                if(isFound){
                    Toast.makeText(holder.listView.getContext(), R.string.beforeRemoving, Toast.LENGTH_LONG).show();
                }

                else {
                    final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(holder.listView.getContext());
                    alertDialogBuilder.setNegativeButton(R.string.cancel, null);
                    alertDialogBuilder.setTitle(R.string.confirmation);
                    alertDialogBuilder.setMessage(R.string.areYouSure);
                    alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (MainActivity.timetables.size() > 1) {
                                MainActivity.deleteTimetable(holder.getAdapterPosition());
                                notifyDataSetChanged();
                                mListener.saveTimetable();
                            } else
                                Toast.makeText(holder.listView.getContext(), R.string.lastTimetable, Toast.LENGTH_SHORT).show();
                        }

                    });
                    alertDialogBuilder.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ListView listView;
        public TextView days;
        public String daysNames = "";
        private ImageButton deleteButton;

        public ViewHolder(View view) {

            super(view);
            mView = view;
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            days = (TextView) view.findViewById(R.id.daysNames);
            listView = (ListView) view.findViewById(R.id.lessonsLV);
            deleteButton = (ImageButton) view.findViewById(R.id.deleteButton);
//            timetable = mValues.get(getAdapterPosition());

            // for()
        }

        @Override
        public String toString() {
            return super.toString() + " '" + days.getText() + "'";
        }
    }

    /**
     * Класс адаптера, отвечающего за список дат начал и концов уроков
     * 1. 8.00 - 8.45
     */


    public class TimetableAdapter extends BaseAdapter {

        private int timetableIndex;

        public TimetableAdapter(int timetableIndex) {
            this.timetableIndex = timetableIndex;
        }

        @Override
        public int getCount() {
            return Settings.TOTALLESSONS;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("H.mm");

            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_timetable, parent, false);

 //           final Timetable timetable = MainActivity.timetables.get(position);

            TextView positionTV = (TextView) view.findViewById(R.id.positionTV);
            positionTV.setText(position + 1 + ". ");

            final TextView time1 = (TextView) view.findViewById(R.id.time1);
            //final Date date1 = timetable.getDates().get(position * 2);

            final Date date1 = MainActivity.getLessonBegin(timetableIndex, position);

            time1.setText(dateFormat.format(date1));
            time1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TimePickerDialog timePickerDialog;
                    timePickerDialog = new TimePickerDialog(parent.getContext(), new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
    //                        if (timePicker.isShown()) {
                                try {

                                    Date newDate = dateFormat.parse(Integer.toString(selectedHour) + "." + Integer.toString(selectedMinute));
                                    if (position == 0) //Если дата первая, и она после даты конца урока
                                    {
                                        if (newDate.before(MainActivity.getLessonEnd(timetableIndex, position))) {
                                            MainActivity.setLessonBegin(newDate, timetableIndex, position);
                                            mListener.saveTimetable();
                                            notifyDataSetChanged();
                                        }
                                        else
                                            Toast.makeText(view.getContext(), view.getResources().getString(R.string.invalidDate), Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (newDate.after(MainActivity.getLessonBegin(timetableIndex, position - 1)) &&
                                                newDate.before(MainActivity.getLessonEnd(timetableIndex, position))) { //Если дата после конца предыдущего и перед концом текущего
                                            MainActivity.setLessonBegin(newDate, timetableIndex, position);
                                            mListener.saveTimetable();
                                            notifyDataSetChanged();
                                        }
                                        else
                                            Toast.makeText(view.getContext(), view.getResources().getString(R.string.invalidDate), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
//                            }
                        }
                    }, date1.getHours(), date1.getMinutes(), true);
                    timePickerDialog.show();
                }
            });

            TextView time2 = (TextView) view.findViewById(R.id.time2);
            //final Date date2 = timetable.getDates().get(position * 2 + 1);
            final Date date2 = MainActivity.getLessonEnd(timetableIndex, position);

            time2.setText(dateFormat.format(date2));
            time2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TimePickerDialog timePickerDialog;
                    timePickerDialog = new TimePickerDialog(parent.getContext(), new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (timePicker.isShown()) {
                                try {
                                    Date newDate = dateFormat.parse(Integer.toString(selectedHour) + "." + Integer.toString(selectedMinute));
                                    if (position == Settings.TOTALLESSONS - 1) {
                                        if (newDate.after(MainActivity.getLessonBegin(timetableIndex, position))) {
                                            MainActivity.setLessonEnd(newDate, timetableIndex, position);
                                            mListener.saveTimetable();
                                            notifyDataSetChanged();
                                        }
                                        else
                                            Toast.makeText(view.getContext(), view.getResources().getString(R.string.invalidDate), Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (newDate.after(MainActivity.getLessonBegin(timetableIndex, position)) && newDate.before(MainActivity.getLessonBegin(timetableIndex, position + 1))) {
                                            MainActivity.setLessonEnd(newDate, timetableIndex, position);
                                            mListener.saveTimetable();
                                            notifyDataSetChanged();
                                        }
                                        else
                                            Toast.makeText(view.getContext(), view.getResources().getString(R.string.invalidDate), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, date2.getHours(), date2.getMinutes(), true);
                    timePickerDialog.show();
                }
            });

            return view;
        }
    }
}




