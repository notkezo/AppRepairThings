package com.example.apprepairthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable; // NEW: Импорт
import android.text.TextWatcher; // NEW: Импорт
import android.widget.Button;
import android.widget.EditText; // NEW: Импорт
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.example.apprepairthings.data.RepairRequest;
import com.example.apprepairthings.data.RepairViewModel;

import java.util.Calendar;

public class CreateReqBonus extends AppCompatActivity {
    private String selectedDate = "";
    private RepairViewModel repairViewModel;
    private boolean isEditMode = false;
    private int editItemId = -1;
    private String currentStatus = "ожидание статуса"; // Default status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_req_bonus);

        // Initialize ViewModel
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);

        // NEW: Настраиваем маску для телефона
        setupPhoneMask();

        // Check if we're in edit mode
        if (getIntent().hasExtra("EDIT_MODE")) {
            isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
            if (isEditMode) {
                editItemId = getIntent().getIntExtra("ITEM_ID", -1);
                prefillFormData();

                // Change button text to "Update" in edit mode
                Button submitButton = findViewById(R.id.button5);
                submitButton.setText("Обновить доп. заявку");
            }
        }

        // Date Picker Button
        Button dateButton = findViewById(R.id.button8);
        dateButton.setOnClickListener(v -> showDatePicker());

        // Submit Button
        Button submitButton = findViewById(R.id.button5);
        submitButton.setOnClickListener(v -> submitBonusRequest());

        // Back button to return to MainActivity
        Button backButton = findViewById(R.id.button4);
        backButton.setOnClickListener(v -> {
            finish(); // Just finish the activity to go back
        });
    }

    // NEW: Метод добавления маски
    private void setupPhoneMask() {
        TextInputLayout phoneLayout = findViewById(R.id.textInputLayout2);
        EditText phoneEditText = phoneLayout.getEditText();

        if (phoneEditText != null) {
            phoneEditText.addTextChangedListener(new TextWatcher() {
                boolean isFormatting;
                boolean backspacing;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    backspacing = count > after;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isFormatting) return;

                    isFormatting = true;

                    // Убираем всё кроме цифр
                    String digits = s.toString().replaceAll("\\D", "");

                    // Если начали с 7 или 8, убираем первую цифру, чтобы формат всегда был +7
                    if (!digits.isEmpty() && (digits.startsWith("7") || digits.startsWith("8"))) {
                        digits = digits.substring(1);
                    }

                    StringBuilder formatted = new StringBuilder();

                    if (!digits.isEmpty()) {
                        formatted.append("+7");

                        if (digits.length() > 0) {
                            formatted.append(" (");
                            int len = Math.min(digits.length(), 3);
                            formatted.append(digits.substring(0, len));
                        }

                        if (digits.length() > 3) {
                            formatted.append(") ");
                            int len = Math.min(digits.length(), 6);
                            formatted.append(digits.substring(3, len));
                        }

                        if (digits.length() > 6) {
                            formatted.append("-");
                            int len = Math.min(digits.length(), 8);
                            formatted.append(digits.substring(6, len));
                        }

                        if (digits.length() > 8) {
                            formatted.append("-");
                            int len = Math.min(digits.length(), 10);
                            formatted.append(digits.substring(8, len));
                        }
                    }

                    phoneEditText.setText(formatted.toString());
                    phoneEditText.setSelection(phoneEditText.getText().length());

                    isFormatting = false;
                }
            });
        }
    }

    private void prefillFormData() {
        TextInputLayout nameLayout = findViewById(R.id.textInputLayout);
        TextInputLayout phoneLayout = findViewById(R.id.textInputLayout2);
        TextInputLayout deviceLayout = findViewById(R.id.textInputLayout3);
        Button dateButton = findViewById(R.id.button8);

        nameLayout.getEditText().setText(getIntent().getStringExtra("NAME"));
        phoneLayout.getEditText().setText(getIntent().getStringExtra("PHONE"));
        deviceLayout.getEditText().setText(getIntent().getStringExtra("DEVICE"));
        selectedDate = getIntent().getStringExtra("DATE");

        dateButton.setText(selectedDate);

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
                    // Update button text with selected date
                    Button dateButton = findViewById(R.id.button8);
                    dateButton.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // NEW: Запрещаем выбирать прошедшие даты
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePicker.show();
    }

    private void submitBonusRequest() {
        // Get input values
        TextInputLayout nameLayout = findViewById(R.id.textInputLayout);
        TextInputLayout phoneLayout = findViewById(R.id.textInputLayout2);
        TextInputLayout deviceLayout = findViewById(R.id.textInputLayout3);

        String name = nameLayout.getEditText().getText().toString();
        String phone = phoneLayout.getEditText().getText().toString();
        String device = deviceLayout.getEditText().getText().toString();

        // Validate inputs
        if (name.isEmpty() || phone.isEmpty() || device.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // NEW: Проверка формата телефона
        if (phone.length() < 18) {
            Toast.makeText(this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            // Update existing item - preserve the current status
            RepairRequest updatedRequest = new RepairRequest(name, phone, device, selectedDate, "", "bonus", currentStatus);
            updatedRequest.setId(editItemId);
            repairViewModel.update(updatedRequest);
            Toast.makeText(this, "Bonus request обновлена!", Toast.LENGTH_SHORT).show();
        } else {
            // Create new item with default status "pending"
            RepairRequest newRequest = new RepairRequest(name, phone, device, selectedDate, "", "bonus", "ожидание статуса");
            repairViewModel.insert(newRequest);
            Toast.makeText(this, "Bonus request добавлена!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}