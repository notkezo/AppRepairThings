package com.example.apprepairthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;
    private Handler clockHandler;
    private Runnable clockRunnable;
    private ImageView repairSortAlphabet, repairSortDate;
    private ImageView bonusSortAlphabet, bonusSortDate;
    private String repairCurrentSort = "date";
    private String bonusCurrentSort = "date";
    private RepairRequests repairFragment;
    private bonusrequests bonusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the clock TextView
        clockTextView = findViewById(R.id.textView);

        // Initialize sorting icons with null checks
        try {
            repairSortAlphabet = findViewById(R.id.repairSortAlphabet);
            repairSortDate = findViewById(R.id.repairSortDate);
            bonusSortAlphabet = findViewById(R.id.bonusSortAlphabet);
            bonusSortDate = findViewById(R.id.bonusSortDate);
        } catch (Exception e) {
            System.out.println("Sorting icons not found: " + e.getMessage());
        }

        // Start the real-time clock
        startRealTimeClock();

        // Load both fragments
        loadRepairRequestsFragment();
        loadBonusRequestsFragment();

        // Set up sorting icons if they exist
        if (repairSortAlphabet != null && repairSortDate != null &&
                bonusSortAlphabet != null && bonusSortDate != null) {
            setupSortingIcons();
            updateSortIconAppearance();
        }

        // Existing button to open CreateReqRepair
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateReqRepair.class);
                startActivity(intent);
            }
        });

        // Button2 to open Stats activity
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Stats.class);
                startActivity(intent);
            }
        });

        // Button3 to open CreateReqBonus activity
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateReqBonus.class);
                startActivity(intent);
            }
        });
    }

    private void startRealTimeClock() {
        clockHandler = new Handler();
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                // Get current date and time
                Date currentDate = new Date();

                // Format date as dd.MM.yyyy
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);

                // Format time as HH:mm:ss
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedTime = timeFormat.format(currentDate);

                // Update the TextView in one line format: 00.00.0000 00:00:00
                String clockText = formattedDate + " " + formattedTime;
                clockTextView.setText(clockText);

                // Schedule the next update in 1 second
                clockHandler.postDelayed(this, 1000);
            }
        };

        // Start the clock immediately
        clockHandler.post(clockRunnable);
    }

    private void loadRepairRequestsFragment() {
        repairFragment = new RepairRequests();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.repairlist, repairFragment);
        transaction.commit();
    }

    private void loadBonusRequestsFragment() {
        bonusFragment = new bonusrequests();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bonuslist, bonusFragment);
        transaction.commit();
    }

    private void setupSortingIcons() {
        // Repair sorting icons
        repairSortAlphabet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repairCurrentSort = "alphabet";
                updateSortIconAppearance();
                if (repairFragment != null) {
                    repairFragment.sortItems(repairCurrentSort);
                }
            }
        });

        repairSortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repairCurrentSort = "date";
                updateSortIconAppearance();
                if (repairFragment != null) {
                    repairFragment.sortItems(repairCurrentSort);
                }
            }
        });

        // Bonus sorting icons
        bonusSortAlphabet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bonusCurrentSort = "alphabet";
                updateSortIconAppearance();
                if (bonusFragment != null) {
                    bonusFragment.sortItems(bonusCurrentSort);
                }
            }
        });

        bonusSortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bonusCurrentSort = "date";
                updateSortIconAppearance();
                if (bonusFragment != null) {
                    bonusFragment.sortItems(bonusCurrentSort);
                }
            }
        });
    }

    private void updateSortIconAppearance() {
        // Update repair icons
        if (repairCurrentSort.equals("alphabet")) {
            repairSortAlphabet.setBackgroundTintList(getColorStateList(R.color.md_teal_200));
            repairSortDate.setBackgroundTintList(getColorStateList(R.color.md_teal_500));
        } else {
            repairSortAlphabet.setBackgroundTintList(getColorStateList(R.color.md_teal_500));
            repairSortDate.setBackgroundTintList(getColorStateList(R.color.md_teal_200));
        }

        // Update bonus icons
        if (bonusCurrentSort.equals("alphabet")) {
            bonusSortAlphabet.setBackgroundTintList(getColorStateList(R.color.md_teal_200));
            bonusSortDate.setBackgroundTintList(getColorStateList(R.color.md_teal_500));
        } else {
            bonusSortAlphabet.setBackgroundTintList(getColorStateList(R.color.md_teal_500));
            bonusSortDate.setBackgroundTintList(getColorStateList(R.color.md_teal_200));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh both fragments when returning to MainActivity
        loadRepairRequestsFragment();
        loadBonusRequestsFragment();

        // Make sure the clock is running
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
            clockHandler.post(clockRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the clock to save resources when activity is not visible
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up the handler to prevent memory leaks
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
    }
}