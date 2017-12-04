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
import com.handen.schoolhelper2.fragments.Name.NameContent;
import java.util.List;

/**
 * Класс, отвечающий за фразмент с списком учителей
 */
public class TeachersListFragment extends Fragment {

    private int mColumnCount = 1; //Кол-во колонок
    private saveTeacherName mListener; //Слушатель для событий



    public TeachersListFragment() { //Пустой конструктор
    }

    //Создаём состояние фрагмента
    @SuppressWarnings("unused")
    public static TeachersListFragment newInstance(int columnCount) {
        TeachersListFragment fragment = new TeachersListFragment();
        Bundle args = new Bundle();
        args.putInt("ColumnCount", columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt("ColumnCount");
        }
    }
    //Создаём представление
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new TeachersListRecyclerViewAdapter(NameContent.ITEMS, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof saveTeacherName) {
            mListener = (saveTeacherName) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Устанавливаем название раздела(в ActionBar`е)
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.teachersNames));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.saveTeacherName();
        mListener = null;
    }

    public interface saveTeacherName {
        void saveTeacherName();
    }
}
