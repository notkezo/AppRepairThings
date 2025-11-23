package com.example.apprepairthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable; // NEW: импорт для маски
import android.text.TextWatcher; // NEW: импорт для маски
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // NEW
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
    private String currentStatus = "ожидание статуса";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_req_repair);

        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);

        // Инициализация полей ввода для настройки маски
        setupPhoneMask(); // NEW: Вызываем настройку маски

        if (getIntent().hasExtra("EDIT_MODE")) {
            isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
            if (isEditMode) {
                editItemId = getIntent().getIntExtra("ITEM_ID", -1);
                prefillFormData();
                Button submitButton = findViewById(R.id.button5);
                submitButton.setText("Обновить заявку");
            }
        }

        Button dateButton = findViewById(R.id.button6);
        dateButton.setOnClickListener(v -> showDatePicker());

        Button timeButton = findViewById(R.id.button7);
        timeButton.setOnClickListener(v -> showTimePicker());

        Button submitButton = findViewById(R.id.button5);
        submitButton.setOnClickListener(v -> submitRepairRequest());

        Button backButton = findViewById(R.id.button4);
        backButton.setOnClickListener(v -> finish());
    }

    // NEW: Метод для настройки маски телефона
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
                    // ничего не делаем
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isFormatting) return;

                    isFormatting = true;

                    // Удаляем все, кроме цифр
                    String digits = s.toString().replaceAll("\\D", "");

                    // Если пользователь начал вводить 8 или 7, обрезаем первую цифру,
                    // чтобы жестко задать +7 в начале (стандарт РФ)
                    if (!digits.isEmpty() && (digits.startsWith("7") || digits.startsWith("8"))) {
                        digits = digits.substring(1);
                    }

                    StringBuilder formatted = new StringBuilder();

                    // Формируем маску +7 (XXX) XXX-XX-XX
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
                    // Ставим курсор в конец текста
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
        Button dateButton = findViewById(R.id.button6);
        Button timeButton = findViewById(R.id.button7);

        nameLayout.getEditText().setText(getIntent().getStringExtra("NAME"));
        phoneLayout.getEditText().setText(getIntent().getStringExtra("PHONE"));
        deviceLayout.getEditText().setText(getIntent().getStringExtra("DEVICE"));
        selectedDate = getIntent().getStringExtra("DATE");
        selectedTime = getIntent().getStringExtra("TIME");

        dateButton.setText(selectedDate);
        timeButton.setText(selectedTime);

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

        // NEW: Блокируем выбор прошедших дат
        // Устанавливаем минимальную дату на текущий момент (минус 1 секунда, чтобы сегодня было доступно)
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

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
        TextInputLayout nameLayout = findViewById(R.id.textInputLayout);
        TextInputLayout phoneLayout = findViewById(R.id.textInputLayout2);
        TextInputLayout deviceLayout = findViewById(R.id.textInputLayout3);

        String name = nameLayout.getEditText().getText().toString();
        String phone = phoneLayout.getEditText().getText().toString();
        String device = deviceLayout.getEditText().getText().toString();

        if (name.isEmpty() || phone.isEmpty() || device.isEmpty() ||
                selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // NEW: Проверка длины номера (опционально), +7 (XXX) XXX-XX-XX - это 18 символов
        if (phone.length() < 18) {
            Toast.makeText(this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            RepairRequest updatedRequest = new RepairRequest(name, phone, device, selectedDate, selectedTime, "repair", currentStatus);
            updatedRequest.setId(editItemId);
            repairViewModel.update(updatedRequest);
            Toast.makeText(this, "Repair request обновлена!", Toast.LENGTH_SHORT).show();
        } else {
            RepairRequest newRequest = new RepairRequest(name, phone, device, selectedDate, selectedTime, "repair", "ожидание статуса");
            repairViewModel.insert(newRequest);
            Toast.makeText(this, "Repair request добавлена!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}