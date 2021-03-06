package com.handen.schoolhelper2;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handen.schoolhelper2.fragments.AboutFragment;
import com.handen.schoolhelper2.fragments.HelpFragment;
import com.handen.schoolhelper2.fragments.LessonListFragment;
import com.handen.schoolhelper2.fragments.NotesFragment;
import com.handen.schoolhelper2.fragments.SettingsFragment;
import com.handen.schoolhelper2.fragments.SubjectListFragment;
import com.handen.schoolhelper2.fragments.TeachersListFragment;
import com.handen.schoolhelper2.fragments.Timetable.Timetable;
import com.handen.schoolhelper2.fragments.TimetableListFragment;
import com.handen.schoolhelper2.fragments.Week.Day;
import com.handen.schoolhelper2.fragments.WeekListFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.handen.schoolhelper2.WidgetProvider.EXTRA_NUMBER;
/**
 * Created by Vanya on 14.07.2017.
 * <p>
 * Активити, где показываются все фрагменты
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SubjectListFragment.OnListFragmentInteractionListener,
        NotesFragment.ShowDialog, WeekListFragment.startLessonsListFragment, LessonListFragment.OnListFragmentInteractionListener, TeachersListFragment.saveTeacherName,
        TimetableListFragment.saveTimetable, SettingsFragment.saveSettings {
    /**
     * Список предметов
     */
    public static ArrayList<Subject> subjectArrayList = new ArrayList<>();
    /**
     * Расписание уроков
     */
    public static ArrayList<ArrayList<Integer>> schedule = new ArrayList<>();
    /**
     * ArrayList с расписанием звонков
     */
    public static CopyOnWriteArrayList<Timetable> timetables = new CopyOnWriteArrayList<>();
    /**
     * Список дней недели
     */
    public static ArrayList<Day> days = new ArrayList<>();
    /**
     * ArrayList с базовыми названиями дней недели
     */
    public static ArrayList<String> daysNames = new ArrayList<>();

    /**
     * Мапа с днями, используется в daysMap.get(currentDate.getDay());
     */
    public static HashMap<Integer, Day> daysMap = new HashMap<>();
    /**
     * Выбранный день
     */
    public static int choosedDay;
    public static Subject currentSubject; // текущий предмет
    private Date lastUpdateDate;
    FrameLayout fragmentHost; // хост фрагментов
    FloatingActionButton fab; //Плавающая кнопка
    FragmentTransaction fragmentTransaction;
    private TextView sentence;
    private TextView author;

    /**
     * Предмет, который используется в фоновом потоке
     */
    public Subject threadSubject;

    public static ArrayList<String> defaultTimetable;

    //**********Фрагменты**********
    SubjectListFragment subjectListFragment;
    NotesFragment notesFragment;
    WeekListFragment weekListFragment;
    LessonListFragment lessonListFragment;
    TeachersListFragment teachersListFragment;
    TimetableListFragment timetableListFragment;
    SettingsFragment settingsFragment;
    AboutFragment aboutFragment;
    HelpFragment helpFragment;

    /**
     * Переменная, обозначающая, что звук отключён
     */
    public static boolean isMuted = false;

    public static int TOTALDAYS = 7;
    public static int NOTIFICATION_ID = 25;

    /**
     * Экземпляр класса настроек
     */
    public static Settings settings;

    /**********Теги фрагментов**********/
    public static final String TAG_SUBJECT_LIST = "TAG_SUBJECT_LIST";
    public static final String TAG_NOTES = "TAG_NOTES";
    public static final String TAG_WEEK_LIST = "TAG_WEEK_LIST";
    public static final String TAG_LESSONS_LIST = "TAG_LESSONS_LIST";
    public static final String TAG_NAMES_LIST = "TAG_NAMES_LIST";
    public static final String TAG_TIMETABLE_LIST = "TAG_TIMETABLE_LIST";
    public static final String TAG_SETTINGS = "TAG_SETTINGS";
    public static final String TAG_ABOUT = "TAG_ABOUT";
    public static final String TAG_HELP = "TAG_HELP";

    /**
     * Менеджер уведомлений
     */
    NotificationManager notificationManager;

    /**
     * Фоновый поток
     */
    static Thread thread;
    static int threadLocker = 0;

    /**
     * Экзепляр класса, который отвечает за сохранение и загрузку данных из памяти
     */
    com.handen.schoolhelper2.SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.subjects));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //toolbar.setTitle(getResources().getString(R.string.subjects));

//********** Находим и устанавлеваем слушатель на плавающую кнопку**********//

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment currentFragment = MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.FragmentHost);
                if (currentFragment.getTag().equals(TAG_TIMETABLE_LIST)) {
                    if (timetableListFragment != null) {
                        timetables.add(new Timetable());
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.timetableListFragment);
                        Snackbar.make(view, R.string.timetableAdded, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        recyclerView.getAdapter().notifyDataSetChanged();
                        sharedPreferences.saveTimetable();
                    }
                }
                else {
                    if (currentFragment.getTag().equals(TAG_SUBJECT_LIST)) {
                        if (subjectListFragment != null) {

                            subjectArrayList.add(new Subject(getResources().getString(R.string.newSubject)));
                            Snackbar.make(view, R.string.subjectAdded, Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.subjectList);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            sharedPreferences.saveSubjects(MainActivity.subjectArrayList);
                        }
                        else
                            throw new NullPointerException("subjectListFragment is null");
                    }
                }
            }
        });

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        sentence = (TextView) header.findViewById(R.id.sentence);
        author = (TextView) header.findViewById(R.id.author);
        String[] sentencesArray = getResources().getStringArray(R.array.sentences);
        ArrayList<String> sentences = new ArrayList<>(Arrays.asList(sentencesArray));
        int sentenceId = new Random().nextInt(sentences.size());
        String sent = sentences.get(sentenceId);
        sentence.setText(sent.split("\\t")[0]);
        author.setText(sent.split("\\t")[1]);


        defaultTimetable = new ArrayList<>(Arrays.asList(MainActivity.this.getResources().getString(R.string.defaultTimetable).split(";")));

        sharedPreferences =  new com.handen.schoolhelper2.SharedPreferences(this);

        settings = sharedPreferences.loadSettings();

        daysMap = sharedPreferences.loadDaysMap();

        days = sharedPreferences.loadDays();

        timetables = sharedPreferences.loadTimetable();


        //Находим хост
        fragmentHost = (FrameLayout) findViewById(R.id.FragmentHost);

//********** Инициализируем список предметов **********/
        /*
        subjectArrayList = sharedPreferences.loadSubjects();
        if (subjectArrayList.size() == 0) {
            Subject.initializeSubjects(MainActivity.this);
        }
        */

        schedule = sharedPreferences.loadSchedule();

        /********************************************************
         *                    Запускаем поток
         ********************************************************/
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Date currentDate = new Date(); //Текущая дата
                    Subject nextSubject;
                    Timetable timetable;
                    boolean isHaveToBeMuted = false; //Нужно ли отключать звук
                    Day currentDay = daysMap.get(currentDate.getDay()); //Текущий день
                    ArrayList<Integer> currentSchedule = schedule.get(getDayIndex(currentDay));

                    if (currentDay == null) { //Проверка на то, что currentDay успешно загрузился
                        SystemClock.sleep(1000);
                        continue;
                    }
                    if (currentDay.getTimetableIndex() == -1) { //Если у текущего дня нет расписания, то поток усыпляется на минуту
                        SystemClock.sleep(600000);
                        continue;
                    }

                    timetable = MainActivity.timetables.get(currentDay.getTimetableIndex()); //Получаем текущее расписание звонков

                    if (threadSubject == null)
                        threadSubject = new Subject("threadSubject");

                    try {
                        currentDate = new SimpleDateFormat("H.mm.ss").parse(currentDate.getHours() + "." + currentDate.getMinutes() + "." + currentDate.getSeconds());
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sharedPreferences = new SharedPreferences(MainActivity.this);
                    
                    ArrayList<Subject> subjects = sharedPreferences.loadSubjects();

                    for (int i = 0; i < Settings.TOTALLESSONS; i++) {
                        Date begin = timetable.getLessonBegin(i);
                        Date end = timetable.getLessonEnd(i);
                        //Если сейчас идёт урок
                        if (currentDate.after(begin) && currentDate.before(end)) {
                            //Если в расписании указан урок
                            if (currentSchedule.get(i) > -1) {
                                isHaveToBeMuted = true; //Звук должен быть отключён
                                String currentSubjectName = subjects.get(currentSchedule.get(i)).getName();
                                if (!currentSubjectName.equals(threadSubject.getName())) {
                                    updateWidget(i);
                                    threadSubject = subjects.get(currentSchedule.get(i));
                                    //Если урок не последний и не пустой
                                    if (i != Settings.TOTALLESSONS - 1 && currentSchedule.get(i + 1) > -1) {
                                        nextSubject = subjects.get(currentSchedule.get(i + 1));
                                        //Показываем уведомление с информацией о двух уроках
                                        showNotification(threadSubject, nextSubject, begin, end, timetable.getLessonBegin(i + 1), timetable.getLessonEnd(i + 1));
                                    }
                                    else
                                        //Показываем уведомление с информацией об одном уроке
                                        showNotification(threadSubject, null, begin, end, null, null);
                                    break;
                                }
                            }
                        }
                        //Если уроков сегодня больше нет, то скрываем все активные уведомления
                        if (i == Settings.TOTALLESSONS - 1 && currentDate.after(end)) {
                            notificationManager.cancel(NOTIFICATION_ID);
                        }
                    }

                    //Если в настройках указано отключать звук - отключаем, иначе не отключаем
                    if (!isMuted && isHaveToBeMuted) {
                        mute();
                    }
                    else
                        if (isMuted && !isHaveToBeMuted)
                            unMute();
                    SystemClock.sleep(1000);
                }
            }
        });
        thread.setName("Second Thread");
        thread.start();

        weekListFragment = new WeekListFragment();
        lessonListFragment = new LessonListFragment();
        teachersListFragment = new TeachersListFragment();

//********** Запускаем первый фрагмент **********/
        subjectListFragment = new SubjectListFragment();
        subjectListFragment.setFab(fab);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.addToBackStack(TAG_SUBJECT_LIST);
        fragmentTransaction.replace(R.id.FragmentHost, subjectListFragment, TAG_SUBJECT_LIST);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.subjectFragment) {
            displayFragment(subjectListFragment, TAG_SUBJECT_LIST);

        }
        else
            if (id == R.id.weekFragment) {
                displayFragment(weekListFragment, TAG_WEEK_LIST);

            }
            else
                if (id == R.id.namesFragment) {
                    displayFragment(teachersListFragment, TAG_NAMES_LIST);

                }
                else
                    if (id == R.id.timetableFragment) {
                        timetableListFragment = new TimetableListFragment();
                        timetableListFragment.setFab(fab);
                        displayFragment(timetableListFragment, TAG_TIMETABLE_LIST);

                    }
                    else
                        if (id == R.id.settings_Fragment) {
                            settingsFragment = new SettingsFragment();
                            displayFragment(settingsFragment, TAG_SETTINGS);

                        }
                        else
                            if (id == R.id.aboutApp) {
                                aboutFragment = new AboutFragment();
                                displayFragment(aboutFragment, TAG_ABOUT);
                            }
                            else
                                if (id == R.id.help) {
                                    helpFragment = new HelpFragment();
                                    displayFragment(helpFragment, TAG_HELP);

                                }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("OnResume");
        subjectArrayList = new ArrayList<>(sharedPreferences.loadSubjects());

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.FragmentHost);

        if(currentFragment.getTag().equals(TAG_SUBJECT_LIST)) {
            System.out.println("currentSubjectListFragment");
            subjectListFragment = SubjectListFragment.newInstance();
            subjectListFragment.setFab(fab);
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(TAG_SUBJECT_LIST)
                    .replace(R.id.FragmentHost, subjectListFragment, TAG_SUBJECT_LIST)
                    .commit();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.saveSubjects(subjectArrayList);
        sharedPreferences.saveTimetable();
        sharedPreferences.saveSettings();
        sharedPreferences.saveSchedule();
    }

    /**
     * Метод, который отображает или заменяет старый фрагмент на новый фрагмент
     */
    public void displayFragment(Fragment fragment, String TAG) {
        //Получаем текущий фрагмент
        Fragment currentFragment = MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.FragmentHost);
        if (!TAG.equals(TAG_SUBJECT_LIST))
            fab.setVisibility(View.INVISIBLE);
        else
            fab.setVisibility(View.VISIBLE);

        if (!TAG.equals(currentFragment.getTag())) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(TAG)
                    .replace(R.id.FragmentHost, fragment, TAG)
                    .commit();
        }
        if (TAG.equals(TAG_SETTINGS) && TAG.equals(currentFragment.getTag())) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(TAG)
                    .replace(R.id.FragmentHost, fragment, TAG)
                    .commit();
        }
    }

    /**
     * Метод, который запускает фрагмент с отметками по нажатию на элемент списка предметов
     * @param index
     */
    @Override
    public void startNotesFragment(int index) {
        notesFragment = new NotesFragment();
        currentSubject = subjectArrayList.get(index);
        displayFragment(notesFragment, TAG_NOTES);
    }

    /**
     * Метод, который показывает диалог с редактированием предмета
     * @param clickIndex
     */
    @Override
    public void startEdition(final int clickIndex) {
        final Subject subject = subjectArrayList.get(clickIndex);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.editing));
        // создаем view из dialog_adding.xml
        final View view = getLayoutInflater()
                .inflate(R.layout.dialog_subject, null);
        // устанавливаем ее, как содержимое тела диалога
        alertDialogBuilder.setView(view);

        final EditText subjectName = (EditText) view.findViewById(R.id.subjectNameET);
        subjectName.setText(subject.getName());
        final TextView subjectNameTV = (TextView) view.findViewById(R.id.subjectNameTV);
        subjectNameTV.setText(getResources().getString(R.string.subjectName));
        final TextView teacherNameTV = (TextView) view.findViewById(R.id.teacherTV);
        teacherNameTV.setText(getResources().getString(R.string.teacher));
        final EditText teacherNameET = (EditText) view.findViewById(R.id.teacherNameET);
        teacherNameET.setText(subject.getTeacherName());
        final EditText classroom = (EditText) view.findViewById(R.id.classroom);
        classroom.setText(subject.getClassroom());
        final ImageButton deleteImageButton = (ImageButton) view.findViewById(R.id.deleteButton);

        // TextView с датой контрольной работы
        final TextView testTextView = (TextView) view.findViewById(R.id.testTextView);
        boolean hasTest = subject.getTestCount() > 0;
        if (hasTest) {
            Date testDate = subject.getClosestTestDate();
            testTextView.setText(getString(R.string.nextTest) + new SimpleDateFormat("dd.MM.yyyy").format(testDate));
        }
        else {

            testTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //    Date date = new Date();
                    Calendar calendar = GregorianCalendar.getInstance();
                    int myYear = calendar.get(Calendar.YEAR);
                    int myMonth = calendar.get(Calendar.MONTH);
                    int myDay = calendar.get(Calendar.DAY_OF_MONTH);
                    //calendar.get(Calendar.YEAR);
                    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int month,
                                              int day) {
                            //   myYear = year;
                            //   myMonth = monthOfYear;
                            //   myDay = dayOfMonth;
                            Calendar c = new GregorianCalendar(year, month, day);
                            Date testDate = new Date(year - 1900, month, day);
                            if (testDate.getTime() < new Date().getTime() &&
                                    testDate.getDay() != new Date().getDay()) {
                                Toast.makeText(MainActivity.this, R.string.invalidDate, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                testTextView.setText(getResources().getText(R.string.nextTest) + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) +
                                        "." + Integer.toString(c.get(Calendar.MONTH) + 1) + "." +
                                        Integer.toString(c.get(Calendar.YEAR)));

                                subject.addTest(testDate);
                                subjectListFragment.setTestHeight();
                                sharedPreferences.saveSubjects(subjectArrayList);
                            }
                        }
                    };


                    DatePickerDialog tpd = new DatePickerDialog(MainActivity.this, myCallBack, myYear, myMonth, myDay);
                    //    return tpd;
                    //   return super.onCreateDialog(id);
                    tpd.show();
                }
            });
        }

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!subjectName.getText().toString().equals("")) {
                    subject.setName(subjectName.getText().toString());

                }
                //    if (!teacherNameET.getText().toString().equals("")) {
                subject.setTeacherName(teacherNameET.getText().toString());
                //    }
                //        if (!classroom.getText().toString().equals("")) {
                subject.setClassroom(classroom.getText().toString());
                //      }

                sharedPreferences.saveSubjects(subjectArrayList);
          //      RecyclerView recyclerView = (RecyclerView) fragmentHost.findViewById(R.id.subjectList);
           //     recyclerView.getAdapter().notifyDataSetChanged();
                subjectListFragment = SubjectListFragment.newInstance();
                subjectListFragment.setFab(fab);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(TAG_SUBJECT_LIST)
                        .replace(R.id.FragmentHost, subjectListFragment, TAG_SUBJECT_LIST)
                        .commit();
            }
        });
        final android.app.AlertDialog alertDialog = alertDialogBuilder.show();
        deleteImageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setNegativeButton(R.string.cancel, null);
                alertDialogBuilder.setTitle(R.string.confirmation);
                alertDialogBuilder.setMessage(R.string.areYouSure);
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectArrayList.remove(clickIndex);
                        sharedPreferences.saveSubjects(subjectArrayList);
                        RecyclerView recyclerView = (RecyclerView) fragmentHost.findViewById(R.id.subjectList);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    /**
     * Метод, который запускает фрагмент с списком уроков на выбранный день
     */
    @Override
    public void startLessonsListFragment(int dayIndex) {
        choosedDay = dayIndex;
        displayFragment(lessonListFragment, TAG_LESSONS_LIST);

    }


    @Override
    public void saveSchedule() {
        sharedPreferences.saveSchedule();
    }

    @Override
    public void saveTeacherName() {
        sharedPreferences.saveSubjects(subjectArrayList);
    }

    @Override
    public void saveTimetable() {
        sharedPreferences.saveTimetable();
    }

    @Override
    public void showDaysDialog(final int timetableIndex, final TextView daysTV) {
        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
        alertDialogBuilder.setTitle(R.string.shooseDays);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int i = 0;
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isChecked()) {
                        days.get(i).setTimetableIndex(timetableIndex);
                    }
                    else {
                        if (checkBox.isEnabled())
                            days.get(i).setTimetableIndex(-1);
                    }
                    i++;
                }

                sharedPreferences.saveTimetable();
                sharedPreferences.saveDays();

                String daysNames = "";
                for (Day day : days) {
                    if (day.getTimetableIndex() != -1) {
                        if (day.getTimetableIndex() == timetableIndex) {
                            if (daysNames.length() != 0)
                                daysNames += ", ";
                            daysNames += day.getName();
                        }
                    }
                }
                daysTV.setText(daysNames);
            }
        });

        alertDialogBuilder.setAdapter(new ListAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return days.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CheckBox checkBox = (CheckBox) convertView;
                if (checkBox == null) {
                    checkBox = new CheckBox(parent.getContext());
                    Day day = days.get(position);


                    if (day.getTimetableIndex() != -1 && day.getTimetableIndex() == timetableIndex) {
                        checkBox.setChecked(true);
                    }
                    else {
                        checkBox.setChecked(false);
                        if (day.getTimetableIndex() != -1) //TODO
                            checkBox.setEnabled(false);
                    }
                    if (checkBoxes.size() < MainActivity.TOTALDAYS)
                        checkBoxes.add(checkBox);
                    checkBox.setText(day.getName());
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        }
                    });
                }
                return checkBox;
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.create().show();
    }

    /**
     * Метод, который показывает даилоговое окно по нажатию на отметку
     *
     * @param note
     */
    @Override
    public void showAlertDialog(Note note, final Subject subject, final int position) {
        if (note.getNote().replace(" ", "").equals("")) {

            final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.adding));
            View view = getLayoutInflater().inflate(R.layout.dialog_adding, null);
            alertDialogBuilder.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.enterNote);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            //		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (!editText.getText().toString().replace(".", "").equals("")) {
                        if (Integer.parseInt(editText.getText().toString().replace(" ", "")) <= Settings.MAX_NOTE) {
                            subject.getNotes().add(subject.getNotes().size() - 1, new Note(new Date(), editText.getText().toString().replace(" ", "")));
                            sharedPreferences.saveSubjects(subjectArrayList);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notesList);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(MainActivity.this, getString(R.string.noteIsTooBig), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialogBuilder.create().show();
        }
        else {
            final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.editing));
            // создаем view из dialog_adding.xml
            View view = getLayoutInflater()
                    .inflate(R.layout.dialog_editing, null);
            // устанавливаем ее, как содержимое тела диалога
            alertDialogBuilder.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.EditNote);
            editText.setText(subject.getNotes().get(position).getNote());
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            final TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            final ImageButton deleteImageButton = (ImageButton) view.findViewById(R.id.deleteButton);
            Date noteDate = subject.getNotes().get(position).getDate();

            dateTextView.setText(DateFormat.format("HH:mm, dd.MM.yyyy", noteDate));

            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!editText.getText().toString().replace(" ", "").equals("")) {
                        if (Integer.parseInt(editText.getText().toString().replace(".", "")) <= Settings.MAX_NOTE) {
                            subject.getNotes().get(position).setNote(editText.getText().toString().replace(".", ""));
                            sharedPreferences.saveSubjects(subjectArrayList);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notesList);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(MainActivity.this, getString(R.string.noteIsTooBig), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final android.app.AlertDialog alertDialog = alertDialogBuilder.show();
            deleteImageButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setNegativeButton(R.string.cancel, null);
                    alertDialogBuilder.setTitle(R.string.confirmation);
                    alertDialogBuilder.setMessage(R.string.areYouSure);
                    alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            subject.getNotes().remove(position);
                            sharedPreferences.saveSubjects(subjectArrayList);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notesList);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }
            });
        }
    }


    /**
     * Метод, который сохраняет настойки
     */

    @Override
    public void saveSettings() {
        sharedPreferences.saveSettings();
    }

    @Override
    public void saveAll() {
        sharedPreferences.saveTimetable();
        sharedPreferences.saveDays();
        sharedPreferences.saveSchedule();
        sharedPreferences.saveSettings();
        sharedPreferences.saveSubjects(subjectArrayList);
    }

    @Override
    public void refreshSettingsFragment() {
        settingsFragment.onDetach();
        settingsFragment = new SettingsFragment();
        displayFragment(settingsFragment, TAG_SETTINGS);
    }

    public void showNotification(final Subject currentSubject, @Nullable final Subject nextSubject, final Date beginDate1, final Date endDate1,
                                 @Nullable final Date beginDate2, @Nullable final Date endDate2) {

        //Если в настройках указано показывать уведомления
        if (Settings.IS_SHOW_NOTIFICATIONS) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Notification.Builder builder; //Билдер уведомления
                    Context context = getApplicationContext();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("H.mm");
                    String notificationText = "";

                    notificationText += currentSubject.getName() + " " + dateFormat.format(beginDate1) + " - " + dateFormat.format(endDate1);
                    if (!currentSubject.getClassroom().equals("")) {
                        notificationText += " " + getString(R.string.classs) + currentSubject.getClassroom();
                    }
                    //Если указан следующий урок
                    if (nextSubject != null) {
                        notificationText += "\n";
                        notificationText += nextSubject.getName() + " " + dateFormat.format(beginDate2) + " - " + dateFormat.format(endDate2);
                        if (!nextSubject.getClassroom().equals("")) {
                            notificationText += " " + getString(R.string.classs) + nextSubject.getClassroom();
                        }
                    }
                    //Создаём интент
                    Intent notificationIntent = new Intent(context, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context,
                            0, notificationIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    Resources res = context.getResources();

                    builder = new Notification.Builder(MainActivity.this);
                    builder.setStyle(new Notification.BigTextStyle(builder)
                            .bigText(notificationText)
                            .setBigContentTitle(getString(R.string.lessonNow)))
                            .setContentIntent(contentIntent)
                            .setContentTitle(getString(R.string.lessonNow))
                            .setTicker(getResources().getString(R.string.lessonHasStarted))
                            .setContentText(notificationText)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setPriority(Notification.PRIORITY_MAX);
                    //Менеджер уведомлений
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                }
            });
        }
    }

    /**
     * Метод, который выключает звуки устройства
     */
    public void mute() {
        if (Settings.IS_MUTING) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_MUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                    }
                    else {
                        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                        audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
                        audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    }
                    isMuted = true;
                }
            });
        }
    }

    /**
     * Метод, который включает звуки устройства
     */
    public void unMute() {
        if (Settings.IS_MUTING) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_UNMUTE, 0);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                    }
                    else {
                        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
                        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                        audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
                        audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    }
                    isMuted = false;
                }
            });
        }
    }

    public static Date getLessonBegin(int timetableIndex, int lessonIndex) {
        Date begin = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
        try {

            begin = dateFormat.parse(MainActivity.defaultTimetable.get(lessonIndex * 2));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        while (++threadLocker != 1) {
            threadLocker--;
            SystemClock.sleep(5);
        }
        if (timetableIndex >= 0 && timetableIndex < timetables.size())
            begin = MainActivity.timetables.get(timetableIndex).getLessonBegin(lessonIndex);
        threadLocker--;
        return begin;
    }

    public static Date getLessonEnd(int timetableIndex, int lessonIndex) {
        Date end = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
        try {

            end = dateFormat.parse(MainActivity.defaultTimetable.get(lessonIndex * 2));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        while (++threadLocker != 1) {
            threadLocker--;
            SystemClock.sleep(5);
        }
        if (timetableIndex >= 0 && timetableIndex < timetables.size())
            end = MainActivity.timetables.get(timetableIndex).getLessonEnd(lessonIndex);
        threadLocker--;
        return end;
    }

    public static void setLessonBegin(Date begin, int timetableIndex, int lessonIndex) {
        while (++threadLocker != 1) {
            threadLocker--;
            SystemClock.sleep(5);
        }
        if (timetableIndex >= 0 && timetableIndex < timetables.size())
            MainActivity.timetables.get(timetableIndex).setLessonBegin(begin, lessonIndex);
        threadLocker--;
    }

    public static void setLessonEnd(Date end, int timetableIndex, int lessonIndex) {
        while (++threadLocker != 1) {
            threadLocker--;
            SystemClock.sleep(5);
        }
        if (timetableIndex >= 0 && timetableIndex < timetables.size())
            MainActivity.timetables.get(timetableIndex).setLessonEnd(end, lessonIndex);
        threadLocker--;
    }

    public static void deleteTimetable(int timetableIndex) {
        for (Day day : days) {
            if (day.getTimetableIndex() == timetableIndex)
                day.setTimetableIndex(-1);
            if (day.getTimetableIndex() > timetableIndex) {
                int currentIndex = day.getTimetableIndex();
                day.setTimetableIndex(currentIndex - 1);
            }
        }
        MainActivity.timetables.remove(timetableIndex);
    }

    public static int getTestCount() {
        int testCount = 0;
        for (Subject subject : subjectArrayList)
            testCount += subject.getTestCount();
        return testCount;
    }

    public static Subject getClosestTestSubject() {
        Subject closest = null;
        for (Subject subject : subjectArrayList) {
            if (subject.getClosestTestDate() != null) {
                if (closest == null || subject.getClosestTestDate().before(closest.getClosestTestDate()))
                    closest = subject;
            }
        }
        return closest;
    }

    private int getDayIndex(Day day) {
        for(int i = 0; i < days.size(); i ++) {
            Day d = days.get(i);
            if(d.getName().equals(day.getName()))
                return i;
        }
        return -1;
    }

    private void updateWidget(int number) {
        System.err.println("MainActivity updateWidget");
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        intent.putExtra(EXTRA_NUMBER, number);
        sendBroadcast(intent);
    }
}

