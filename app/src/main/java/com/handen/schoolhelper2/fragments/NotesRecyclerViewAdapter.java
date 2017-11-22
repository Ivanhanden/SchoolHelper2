package com.handen.schoolhelper2.fragments;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.Note;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Subject;



import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private final List<Note> notes;
    private final NotesFragment.ShowDialog mListener;
    private Subject mSubject;

    public NotesRecyclerViewAdapter(Subject subject, NotesFragment.ShowDialog listener) {
        notes = subject.getNotes();
        mListener = listener;
        mSubject = subject;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_note, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.note = notes.get(position);
//        String noteText = holder.note.getNote();
        if(holder.note.getNote().equals(""))
            holder.noteTextView.setText("");
        holder.noteTextView.setText(holder.note.getNote());
//            noteTextView.setText("10");

        //holder.noteTextView.setText(notes.get(position).getNote());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.showAlertDialog(holder.note, mSubject,holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView noteTextView;
        public Note note;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            noteTextView = (TextView) view.findViewById(R.id.note);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + noteTextView.getText() + "'";
        }
    }
}
