package com.example.apprepairthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.example.apprepairthings.data.RepairRequest;
import com.example.apprepairthings.data.RepairViewModel;

import java.util.Calendar;

public class CreateReqRepair extends AppCompatActivity {
    private String selectedDate = "";
    private String selectedTime = "";
    private RepairViewModel repairViewModel;
    private boolean isEditMode = false;
    private int editItemId = -1;
    private String currentStatus = "ожидание статуса"; // Default status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_req_repair);

        // Initialize ViewModel
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);

        // Check if we're in edit mode
        if (getIntent().hasExtra("EDIT_MODE")) {
            isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
            if (isEditMode) {
                editItemId = getIntent().getIntExtra("ITEM_ID", -1);
                prefillFormData();

                // Change button text to "Update" in edit mode
                Button submitButton = findViewById(R.id.button5);
                submitButton.setText("Обновить заявку");
            }
        }

        // Date Picker Button
        Button dateButton = findViewById(R.id.button6);
        dateButton.setOnClickListener(v -> showDatePicker());

        // Time Picker Button
        Button timeButton = findViewById(R.id.button7);
        timeButton.setOnClickListener(v -> showTimePicker());

        // Submit Button
        Button submitButton = findViewById(R.id.button5);
        submitButton.setOnClickListener(v -> submitRepairRequest());

        // Back button
        Button backButton = findViewById(R.id.button4);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Just finish the activity to go back
            }
        });
    }

    private void prefillFormData() {
        TextInputLayout nameLayout = findViewById(R.id.textInputLayout);
        TextInputLayout phoneLayout = findViewById(R.id.textInputLayout2);
        TextInputLayout deviceLayout = findViewById(R.id.textInputLayout3);
        Button dateButton = findViewById(R.id.button6);
        Button timeButton = findViewById(R.id.button7);

        nameLayout.getEditText().setText(getIntent().getStringExtra("NAME"));
        phoneLayout.getEditText().setText(getIntent().getStringExtra("PHONE"));
        deviceLayout.getEditText().setText(getIntent().getStringExtra("DEVICE"));
        selectedDate = getIntent().getStringExtra("DATE");
        selectedTime = getIntent().getStringExtra("TIME");

        dateButton.setText(selectedDate);
        timeButton.setText(selectedTime);

        // If editing, get the status from the intent (you'll need to pass it from the fragment)
        if (getIntent().hasExtra("STATUS")) {
            currentStatus = getIntent().getStringExtra("STATUS");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    Button dateButton = findViewById(R.id.button6);
                    dateButton.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    Button timeButton = findViewById(R.id.button7);
                    timeButton.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePicker.show();
    }

    private void submitRepairRequest() {
        // Get input values
        TextInputLayout nameLayout = findViewById(R.id.textInputLayout);
        TextInputLayout phoneLayout = findViewById(R.id.textInputLayout2);
        TextInputLayout deviceLayout = findViewById(R.id.textInputLayout3);

        String name = nameLayout.getEditText().getText().toString();
        String phone = phoneLayout.getEditText().getText().toString();
        String device = deviceLayout.getEditText().getText().toString();

        // Validate inputs
        if (name.isEmpty() || phone.isEmpty() || device.isEmpty() ||
                selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            // Update existing item - preserve the current status
            RepairRequest updatedRequest = new RepairRequest(name, phone, device, selectedDate, selectedTime, "repair", currentStatus);
            updatedRequest.setId(editItemId);
            repairViewModel.update(updatedRequest);
            Toast.makeText(this, "Repair request обновлена!", Toast.LENGTH_SHORT).show();
        } else {
            // Create new item with default status "pending"
            RepairRequest newRequest = new RepairRequest(name, phone, device, selectedDate, selectedTime, "repair", "ожидание статуса");
            repairViewModel.insert(newRequest);
            Toast.makeText(this, "Repair request добавлена!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}