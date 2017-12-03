package com.handen.schoolhelper2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Subject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Фрагмент списка с предметами
 */
public class SubjectListFragment extends Fragment{

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    FloatingActionButton fab;
    private CardView testCard;
    private TextView testSubjectName;
    private TextView testDateTextView;
    private ImageButton hideTestButton;


    /**
     * Пустой конструктор
     */
    public SubjectListFragment() {
    }

    @SuppressWarnings("unused")
    public static SubjectListFragment newInstance(int columnCount) {
        SubjectListFragment fragment = new SubjectListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Метод, который вызывается при создании фрагмента
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /**
     * Назначаем адаптер и заполняем View(виджет RecyclerView)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_subject_list, container, false);
        TextView averageTextView = (TextView) fragmentView.findViewById(R.id.averageTextView);

        double average = 0;
        float total = 0;
        float counter = 0;
        for(Subject subject : MainActivity.subjectArrayList) {
            if(subject.getAverage() != -1)
            {
                total += subject.getAverage();
                counter ++;
            }
        }

        if(counter != 0)
            average = (double) total / counter;

        average = (int) (average * 100) / 100.0;
        if((int) average == average)
            averageTextView.setText(getResources().getString(R.string.yourAverage) + " " + Integer.toString((int) average));
        else
            averageTextView.setText(getResources().getString(R.string.yourAverage) + " " + average);
        View view = fragmentView.findViewById(R.id.subjectList);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new SubjectListRecyclerViewAdapter(MainActivity.subjectArrayList, mListener));

        }

        testCard = (CardView) fragmentView.findViewById(R.id.testCardView);
        testDateTextView = (TextView) fragmentView.findViewById(R.id.testDateTextView);
        testSubjectName = (TextView) fragmentView.findViewById(R.id.testSubjectName);
        hideTestButton = (ImageButton) fragmentView.findViewById(R.id.hideTestButton);
        hideTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = testCard.getLayoutParams();
                layoutParams.height = 0;
                testCard.setLayoutParams(layoutParams);
            }
        });

        //MainActivity.subjectArrayList

        if(MainActivity.getTestCount() > 0) {
            bindTest();
        }
        else {
            ViewGroup.LayoutParams layoutParams = testCard.getLayoutParams();
            layoutParams.height = 0;
            testCard.setLayoutParams(layoutParams);
        }

        return fragmentView;
    }

    private void bindTest() {
        //Tests.Test test = Tests.getClosest();

        Subject testSubject = MainActivity.getClosestTestSubject();
        Date testDate = testSubject.getClosestTestDate();
        testSubjectName.setText(testSubject.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateText = getActivity().getString(R.string.testDate);
        testDateTextView.setText(dateText + dateFormat.format(testDate));
    }
    public void setTestHeight() {
        testCard.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        bindTest();
    }

    /**
     * Метод, который вызывается при создании фрагмента
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.subjects));
        fab.setVisibility(View.VISIBLE);
    }

    /**
     * Метод, который вызывается при уничтожении фрагмента
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setFab(FloatingActionButton fab)
    {
        this.fab = fab;
    }

    /**
     * Интерфейс, который связывает активити, класс фрагмента и адаптера, отвечающего за предметы
     */
    public interface OnListFragmentInteractionListener {

        void startNotesFragment(int clickIndex);
        void startEdition(int clickIndex);
    }


}
