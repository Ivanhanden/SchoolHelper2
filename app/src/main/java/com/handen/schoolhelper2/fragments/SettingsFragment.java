package com.handen.schoolhelper2.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.Note;
import com.handen.schoolhelper2.R;
import com.handen.schoolhelper2.Settings;
import com.handen.schoolhelper2.Subject;
import com.handen.schoolhelper2.fragments.Timetable.Timetable;
import com.handen.schoolhelper2.fragments.Week.Day;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView maxNote;
    Switch isShowNotifications;
    Switch isMuting;
    TextView redColor;
    TextView yellowColor;
    TextView greenColor;
    ImageButton maxPlus, maxMinus, redPlus, redMinus, yellowPlus, yellowMinus, greenPlus, greenMinus;
    Context mContext;
    private saveSettings mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        maxNote = (TextView) view.findViewById(R.id.maxNote);
        maxNote.setText(Integer.toString(Settings.MAX_NOTE));

        maxPlus = (ImageButton) view.findViewById(R.id.maxPlus);
        maxPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.MAX_NOTE += 1;
                mListener.saveSettings();
                maxNote.setText(Integer.toString(Settings.MAX_NOTE));

            }
        });

        maxMinus = (ImageButton) view.findViewById(R.id.maxMinus);
        maxMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.MAX_NOTE -= 1;
                maxNote.setText(Integer.toString(Settings.MAX_NOTE));
                mListener.saveSettings();
            }
        });

        isShowNotifications = (Switch) view.findViewById(R.id.isShowNotifications);
        if(Settings.IS_SHOW_NOTIFICATIONS)
            isShowNotifications.setChecked(true);
        else
            isShowNotifications.setChecked(false);
        isShowNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.IS_SHOW_NOTIFICATIONS = isChecked;
                mListener.saveSettings();
            }
        });

        isMuting = (Switch) view.findViewById(R.id.isMuting);
        if(Settings.IS_MUTING)
            isMuting.setChecked(true);
        else
            isMuting.setChecked(false);
        isMuting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.IS_MUTING = isChecked;
                mListener.saveSettings();
            }
        });
        redColor = (TextView) view.findViewById(R.id.redColor);

        redPlus = (ImageButton) view.findViewById(R.id.redPlus);
        redPlus.setVisibility(View.INVISIBLE);

        redMinus = (ImageButton) view.findViewById(R.id.redMinus);
        redMinus.setVisibility(View.INVISIBLE);


        yellowColor = (TextView) view.findViewById(R.id.yellowColor);
        yellowColor.setText(getResources().getString(R.string.yellowColor) + " " + Float.toString((float)Settings.YELLOW_COLOR) +  " " +getResources().getString(R.string.points));
        yellowPlus = (ImageButton) view.findViewById(R.id.yellowPlus);
        if(Settings.GREEN_COLOR - Settings.YELLOW_COLOR == 0.5)
            yellowPlus.setVisibility(View.INVISIBLE);
        yellowPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.YELLOW_COLOR += 0.5;
                if(Settings.GREEN_COLOR - Settings.YELLOW_COLOR == 0.5) {
                    yellowPlus.setVisibility(View.INVISIBLE);
                    greenMinus.setVisibility(View.INVISIBLE);
                }

                yellowColor.setText(getResources().getString(R.string.yellowColor) + " " + Float.toString((float)Settings.YELLOW_COLOR) +  " " +getResources().getString(R.string.points));
                mListener.saveSettings();

                if(Settings.YELLOW_COLOR != 0.5)
                    yellowMinus.setVisibility(View.VISIBLE);
            }
        });
        yellowMinus = (ImageButton) view.findViewById(R.id.yellowMinus);
        yellowMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.YELLOW_COLOR -= 0.5;
                if(Settings.YELLOW_COLOR == 0.5)
                    yellowMinus.setVisibility(View.INVISIBLE);

                yellowColor.setText(getResources().getString(R.string.yellowColor) + " " + Float.toString((float)Settings.YELLOW_COLOR) +  " " +getResources().getString(R.string.points));
                mListener.saveSettings();

                if(Settings.GREEN_COLOR - Settings.YELLOW_COLOR != 0.5) {
                    yellowPlus.setVisibility(View.VISIBLE);
                    greenMinus.setVisibility(View.VISIBLE);
                }
            }
        });

        greenColor = (TextView) view.findViewById(R.id.greenColor);
        greenColor.setText(getResources().getString(R.string.greenColor) + " " +  Float.toString((float) Settings.GREEN_COLOR) + " " + getResources().getString(R.string.points));
        greenPlus = (ImageButton) view.findViewById(R.id.greenPlus);
        greenPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.GREEN_COLOR += 0.5;

                yellowPlus.setVisibility(View.VISIBLE);

                greenColor.setText(getResources().getString(R.string.greenColor) + " " +  Float.toString((float) Settings.GREEN_COLOR) + " " + getResources().getString(R.string.points));
                mListener.saveSettings();
                if(Settings.GREEN_COLOR - Settings.YELLOW_COLOR != 0.5)
                    greenMinus.setVisibility(View.VISIBLE);

            }
        });
        greenMinus = (ImageButton) view.findViewById(R.id.greenMinus);
        if(Settings.GREEN_COLOR - Settings.YELLOW_COLOR == 0.5)
            greenMinus.setVisibility(View.INVISIBLE);
        greenMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.GREEN_COLOR -= 0.5;
                if(Settings.GREEN_COLOR - Settings.YELLOW_COLOR == 0.5) {
                    greenMinus.setVisibility(View.INVISIBLE);
                    yellowPlus.setVisibility(View.INVISIBLE);
                }
                greenColor.setText(getResources().getString(R.string.greenColor) + " " +  Float.toString((float) Settings.GREEN_COLOR) + " " + getResources().getString(R.string.points));
                mListener.saveSettings();

 //               if(Settings.MAX_NOTE - Settings.GREEN_COLOR != 0.5)
 //               {
 //                   greenPlus.setVisibility(View.VISIBLE);
 //              }

            }
        });



        TextView reset = (TextView) view.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof saveSettings) {
            mContext = context;
            mListener = (saveSettings) mContext;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.settings));
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.saveSettings();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface saveSettings {

        void saveSettings();
        void saveAll();
        void refreshSettingsFragment();
    }

    public void reset() {
        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reset, null);

        final CheckBox subjects = (CheckBox) view.findViewById(R.id.subjects);
        checkBoxes.add(subjects);

        final CheckBox notes = (CheckBox) view.findViewById(R.id.notes);
        checkBoxes.add(notes);

        final CheckBox teachers = (CheckBox) view.findViewById(R.id.teachersNames);
        checkBoxes.add(teachers);

        final CheckBox timetable = (CheckBox) view.findViewById(R.id.timetable);
        checkBoxes.add(timetable);

        final CheckBox settings = (CheckBox) view.findViewById(R.id.settings);
        checkBoxes.add(settings);

        final CheckBox classes = (CheckBox) view.findViewById(R.id.classes);
        checkBoxes.add(classes);

        final CheckBox schedule = (CheckBox) view.findViewById(R.id.schedule);
        checkBoxes.add(schedule);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
        alertDialogBuilder.setTitle(R.string.reset);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isChecked()) {
                        if (checkBox.equals(subjects)) {
                            MainActivity.subjectArrayList = new ArrayList<Subject>();
                            Subject.initializeSubjects(getContext());
                        }
                        else
                            if (checkBox.equals(notes)) {
                                for (Subject subject : MainActivity.subjectArrayList)
                                {
                                    subject.getNotes().clear();
                                    subject.getNotes().add(new Note());
                                }
                            }
                            else
                                if (checkBox.equals(teachers)) {
                                    for (Subject subject : MainActivity.subjectArrayList)
                                    {
                                        subject.setTeacherName("");
                                    }
                                }
                                else
                                    if (checkBox.equals(timetable)) {
                                        MainActivity.timetables.clear();
                                        MainActivity.timetables.add(new Timetable());
                                        for(Day day : MainActivity.days)
                                        {
                                            day.setTimetableIndex(0);
                                        }
                                    }
                                    else
                                        if (checkBox.equals(settings)) {
                                            MainActivity.settings = new Settings();
                                            mListener.refreshSettingsFragment();
                                        }
                                        else
                                            if(checkBox.equals(classes)) {
                                                for (Subject subject : MainActivity.subjectArrayList)
                                                {
                                                    subject.setClassroom("");
                                                }
                                            }
                                            else
                                                if(checkBox.equals(schedule)){
                                                    MainActivity.schedule = new ArrayList<ArrayList<Integer>>();
                                                    for (int i = 0; i < MainActivity.TOTALDAYS; i++) {
                                                        MainActivity.schedule.add(new ArrayList<Integer>(Arrays.asList(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)));
                                                    }
                                                }
                    }
                }
                if(mListener == null)
                    mListener = (saveSettings) mContext;
                mListener.saveAll();
            }
        });
        alertDialogBuilder.setView(view);
        alertDialogBuilder.create().show();
    }
}
