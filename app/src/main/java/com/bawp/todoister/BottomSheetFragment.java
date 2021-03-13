package com.bawp.todoister;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText todoEditText;
    private ImageButton calenderButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calenderGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    public BottomSheetFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        calenderGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calenderButton = view.findViewById(R.id.today_calendar_button);
        todoEditText = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip);
        Chip tomorrowChp = view.findViewById(R.id.tomorrow_chip);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);

        todayChip.setOnClickListener(this);
        tomorrowChp.setOnClickListener(this);
        nextWeekChip.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedViewModel.getSelectedItem().getValue() != null) {
            // some data has been sent from the main activity
            isEdit = sharedViewModel.isEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            todoEditText.setText(task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // This method is called right after the above method
        // i.e. right after we set up our widgets
        // This is where we fetch information from our views

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderGroup.setVisibility(
                        calenderGroup.getVisibility() == View.GONE ? View.VISIBLE : GONE);
                Utils.hideKeyboard(v);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
                dueDate = calendar.getTime();
            }
        });

        priorityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(v);
                priorityRadioGroup.setVisibility(
                        priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                priorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (priorityRadioGroup.getVisibility() == VISIBLE) {
                            selectedButtonId = checkedId;
                            selectedRadioButton = view.findViewById(selectedButtonId);
                            if (selectedRadioButton.getId() == R.id.radioButton_high) {
                                priority = Priority.HIGH;
                            }
                            else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                                priority = Priority.MEDIUM;
                            }
                            else if (selectedRadioButton.getId() == R.id.radioButton_low) {
                                priority = Priority.LOW;
                            }
                            else {
                                priority = Priority.LOW;
                            }
                        }
                        else {
                            // if the user has'nt even clicked the priority button
                            // the priority by default will be low
                            priority = Priority.LOW;
                        }
                    }
                });
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = todoEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(task) && dueDate != null && priority != null) {
                    Task taskToSave = new Task(task,
                            priority,
                            dueDate,
                            Calendar.getInstance().getTime(),
                            false);
                    if (isEdit) {
                        Task taskToUpdate = sharedViewModel.getSelectedItem().getValue();
                        taskToUpdate.setTask(task);
                        taskToUpdate.setDateCreated(Calendar.getInstance().getTime());
                        taskToUpdate.setPriority(priority);
                        taskToUpdate.setDueDate(dueDate);
                        TaskViewModel.update(taskToUpdate);
                        sharedViewModel.isEdit(false);
                    }
                    else {
                        TaskViewModel.insert(taskToSave);
                    }
                    todoEditText.setText("");
                    if (BottomSheetFragment.this.isVisible()) {
                        BottomSheetFragment.this.dismiss();
                    }
                }
                else {
                    Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.today_chip) {
            //set date for today
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calendar.getTime();
        }
        else if (id == R.id.tomorrow_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calendar.getTime();
        }
        else if (id == R.id.next_week_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            dueDate = calendar.getTime();
        }
    }
}