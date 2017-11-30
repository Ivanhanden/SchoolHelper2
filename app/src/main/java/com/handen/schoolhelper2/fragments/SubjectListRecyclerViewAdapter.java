package com.handen.schoolhelper2.fragments;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Settings;
import com.handen.schoolhelper2.Subject;
import com.handen.schoolhelper2.Tests;
import com.handen.schoolhelper2.fragments.SubjectListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * Класс адаптера и ViewHolder`а для фрагмента со списком предметов
 */
public class SubjectListRecyclerViewAdapter extends RecyclerView.Adapter<SubjectListRecyclerViewAdapter.ViewHolder> {

    private final List<Subject> subjectList; //Список элементов
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public SubjectListRecyclerViewAdapter(List<Subject> items, OnListFragmentInteractionListener listener) {
        subjectList = items;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_subject, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.subject = subjectList.get(holder.getAdapterPosition());

        if(Tests.getCount() > 0 && position == 0) {
            onBindTest(holder);
            return;
        }

        String subjectText = subjectList.get(holder.getAdapterPosition()).getName();
        double average = (double) holder.subject.getAverage();
        String advise = holder.subject.getNotesToNextLevel();

        if (average != -1.0) {
            average = (int) (average * 100) / 100.0;
            if ((int) average == average)
                subjectText += "   (" + Integer.toString((int) average) + ")";
            else
                subjectText += "   (" + Double.toString(average) + ")";

            if (average < Settings.YELLOW_COLOR)
                holder.subjectCard.setBackgroundColor(context.getResources().getColor(R.color.subjectRed));
            else
                if (average < Settings.GREEN_COLOR)
                    holder.subjectCard.setBackgroundColor(context.getResources().getColor(R.color.subjectYellow));
                else
                    holder.subjectCard.setBackgroundColor(context.getResources().getColor(R.color.subjectGreen));
        }
        else
            holder.subjectCard.setBackgroundColor(context.getResources().getColor(R.color.subjectGreen));

        if (advise.length() != 0) {
            holder.adviseTV.setText(advise);
            holder.arrowImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.adviseTV.setText("");
            holder.arrowImageView.setVisibility(View.INVISIBLE);
        }
        holder.subjectName.setText(subjectText);
        String sdv = holder.adviseTV.getText().toString();

        if(holder.subject.getClassroom() == null)
        {
            holder.subject.setClassroom("");
        }

        holder.teacherName.setText(holder.subject.getTeacherName());
        if(holder.subject.getClassroom().equals(""))
            holder.classroom.setText("");
        else
            holder.classroom.setText(holder.mView.getResources().getString(R.string.classs) + holder.subject.getClassroom());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    mListener.startNotesFragment(holder.getAdapterPosition());

                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.startEdition(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public void onBindTest(ViewHolder holder) {
        String subjectText = subjectList.get(holder.getAdapterPosition()).getName();
        double average = (double) holder.subject.getAverage();

        if (average != -1.0) {
            average = (int) (average * 100) / 100.0;
            if ((int) average == average)
                subjectText += "   (" + Integer.toString((int) average) + ")";
            else
                subjectText += "   (" + Double.toString(average) + ")";
        }
        holder.subjectName.setText(subjectText);
        String testDate = holder.mView.getContext().getResources().getString(R.string.testDate);
       // Tests tests =
        holder.teacherName.setText(testDate + Tests.getTest(holder.subject).getDate());
 //       notifyItemInserted(0);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        CardView subjectCard;
        public final TextView subjectName;
        public TextView teacherName;
        public Subject subject;
        public TextView classroom;
        TextView adviseTV;
        ImageView arrowImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            subjectCard = (CardView) view.findViewById(R.id.subjectCard);
            subjectName = (TextView) view.findViewById(R.id.subjectName);
            teacherName = (TextView) view.findViewById(R.id.teacherName);
            adviseTV = (TextView) view.findViewById(R.id.advise);
            classroom = (TextView) view.findViewById(R.id.classroom);
            arrowImageView = (ImageView) view.findViewById(R.id.arrowImageView);
            arrowImageView.setVisibility(View.INVISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + subjectName.getText() + "'";
        }

    }
}
