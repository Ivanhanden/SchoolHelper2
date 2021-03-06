package com.handen.schoolhelper2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;


/**
    Класс, со списком дней недели
 */
public class WeekListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private startLessonsListFragment mListener;

    /**
     * Конструктор
     */
    public WeekListFragment() {
    }

    @SuppressWarnings("unused")
    public static WeekListFragment newInstance(int columnCount) {
        WeekListFragment fragment = new WeekListFragment();
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

    /**
     * Создаём представление фрагмента
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_list, container, false);

        // Устанавливаем адаптер
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new WeekListRecyclerViewAdapter(MainActivity.days, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof startLessonsListFragment) {
            mListener = (startLessonsListFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.week));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Интерфейс, который запускает список уроков по нажатию на день недели
     */
    public interface startLessonsListFragment {
        // TODO: Update argument type and name
        void startLessonsListFragment(int a);
    }
}
