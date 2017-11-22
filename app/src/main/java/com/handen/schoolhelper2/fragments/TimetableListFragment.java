package com.handen.schoolhelper2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;

/**
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */

public class TimetableListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private saveTimetable mListener;
    private FloatingActionButton fab;
    TimetableListRecyclerViewAdapter timetableListRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public TimetableListFragment() {
    }

    @SuppressWarnings("unused")
    public static TimetableListFragment newInstance(int columnCount) {
        TimetableListFragment fragment = new TimetableListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_list, container, false);

        timetableListRecyclerViewAdapter = new TimetableListRecyclerViewAdapter(MainActivity.timetables, mListener);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(timetableListRecyclerViewAdapter);

//            View layout = inflater.inflate(R.layout.fragment_timetable, container, false);
//            ListView listView = (ListView) layout.findViewById(R.id.lessonsLV);
//        listView.setAdapter(null);
//            TimetableListRecyclerViewAdapter.TimetableAdapter timetableAdapter = new TimetableListRecyclerViewAdapter.TimetableAdapter();
//            listView.setAdapter(timetableAdapter);
//            recyclerView.getAdapter().notifyDataSetChanged();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof saveTimetable) {
            mListener = (saveTimetable) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.timetable));
        fab.setVisibility(View.VISIBLE);
    }
  //  @Override
  //  public void onPause()
//    {
//        super.onPause();
//        fab.setVisibility(View.INVISIBLE);
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.saveTimetable();
        mListener = null;
    }

    public void setFab(FloatingActionButton fab)
    {
        this.fab = fab;
//        this.fab.setOnClickListener(new View.OnClickListener() {
 //           @Override
//            public void onClick(View v) {
//                Timetable newTimetable = new Timetable();
//                MainActivity.timetables.add(newTimetable);
//                TimetableContent.addItem(newTimetable);
//                timetableListRecyclerViewAdapter.notifyDataSetChanged();
//            }
//        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface saveTimetable {

        void saveTimetable();
        void showDaysDialog(int timetableIndex, TextView textView);

    }
}
