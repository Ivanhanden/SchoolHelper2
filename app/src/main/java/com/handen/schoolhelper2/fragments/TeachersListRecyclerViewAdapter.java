package com.handen.schoolhelper2.fragments;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.fragments.Name.NameContent;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NameItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TeachersListRecyclerViewAdapter extends RecyclerView.Adapter<TeachersListRecyclerViewAdapter.ViewHolder> {

    private List<NameContent.Name> mValues;
    private final TeachersListFragment.saveTeacherName mListener;

    public TeachersListRecyclerViewAdapter(List<NameContent.Name> items, TeachersListFragment.saveTeacherName listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.subjectName.setText(MainActivity.subjectArrayList.get(position).getName() + ":");
        holder.teacherName.setText(MainActivity.subjectArrayList.get(position).getTeacherName());
        holder.teacherName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.subjectArrayList.get(holder.getAdapterPosition()).setTeacherName(holder.teacherName.getText().toString());
                holder.mItem.teacherName = holder.teacherName.getText().toString();
                mListener.saveTeacherName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

/*        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.saveTeacherName(holder.mItem);
                }
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final EditText teacherName;
        public final TextView subjectName;
        public com.handen.schoolhelper2.fragments.Name.NameContent.Name mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            teacherName = (EditText) view.findViewById(R.id.teacherName);
            subjectName = (TextView) view.findViewById(R.id.subjectNameFragmentName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + teacherName.getText().toString() + "'";
        }
    }
}
