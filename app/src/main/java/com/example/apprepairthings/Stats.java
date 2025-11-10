package com.example.apprepairthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.apprepairthings.data.RepairRequest;
import com.example.apprepairthings.data.RepairViewModel;

import java.util.ArrayList;
import java.util.List;

public class Stats extends AppCompatActivity {

    private RepairViewModel repairViewModel;
    private TextView allRequestsText, requestsInWorkText, requestsDoneText;
    private TextView allBonusText, bonusInWorkText, bonusDoneText;
    private RecyclerView allItemsRecyclerView;
    private MyItemRecyclerViewAdapter allItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Initialize ViewModel
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);

        // Initialize TextViews for repair requests
        allRequestsText = findViewById(R.id.allrequests);
        requestsInWorkText = findViewById(R.id.requestsinwork);
        requestsDoneText = findViewById(R.id.requestsdone);

        // Initialize TextViews for bonus requests
        allBonusText = findViewById(R.id.allbonus);
        bonusInWorkText = findViewById(R.id.bonusinwork);
        bonusDoneText = findViewById(R.id.bonusdone);

        // Initialize All Items RecyclerView with existing adapter
        allItemsRecyclerView = findViewById(R.id.allitems);
        allItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use the existing repair request adapter
        allItemsAdapter = new MyItemRecyclerViewAdapter();
        allItemsRecyclerView.setAdapter(allItemsAdapter);

        // Remove click listeners since we're just displaying (read-only)
        allItemsAdapter.setOnItemClickListener(new MyItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, RepairRequest item) {
                // Optional: You can keep this empty or show details
            }

            @Override
            public void onItemLongClick(int position, RepairRequest item) {
                // Optional: You can keep this empty or show details
            }
        });

        // Observe repair requests and update stats in real-time
        repairViewModel.getRepairRequests().observe(this, new Observer<List<RepairRequest>>() {
            @Override
            public void onChanged(List<RepairRequest> repairRequests) {
                updateRepairStats(repairRequests);
                updateAllItemsList();
            }
        });

        // Observe bonus requests and update stats in real-time
        repairViewModel.getBonusRequests().observe(this, new Observer<List<RepairRequest>>() {
            @Override
            public void onChanged(List<RepairRequest> bonusRequests) {
                updateBonusStats(bonusRequests);
                updateAllItemsList();
            }
        });

        // Existing button to open MainActivity
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Stats.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateRepairStats(List<RepairRequest> repairRequests) {
        int totalRepairs = repairRequests.size();
        int repairsInWork = 0;
        int repairsDone = 0;

        // Count requests by status - UPDATED STATUS NAMES
        for (RepairRequest request : repairRequests) {
            if ("заявка в обработке".equals(request.getStatus())) {
                repairsInWork++;
            } else if ("заявка выполнена".equals(request.getStatus())) {
                repairsDone++;
            }
        }

        // Update UI
        allRequestsText.setText("Общее количество заявок на ремонт: " + totalRepairs);
        requestsInWorkText.setText("Заявки на ремонт в работе: " + repairsInWork);
        requestsDoneText.setText("Заявки на ремонт выполнены: " + repairsDone);

        System.out.println("Repair Stats - Total: " + totalRepairs +
                ", In Work: " + repairsInWork +
                ", Done: " + repairsDone);
    }

    private void updateBonusStats(List<RepairRequest> bonusRequests) {
        int totalBonus = bonusRequests.size();
        int bonusInWork = 0;
        int bonusDone = 0;

        // Count requests by status - UPDATED STATUS NAMES
        for (RepairRequest request : bonusRequests) {
            if ("заявка в обработке".equals(request.getStatus())) {
                bonusInWork++;
            } else if ("заявка выполнена".equals(request.getStatus())) {
                bonusDone++;
            }
        }

        // Update UI
        allBonusText.setText("Общее количество заявок на доп. услуги: " + totalBonus);
        bonusInWorkText.setText("Заявки на доп. услуги в работе: " + bonusInWork);
        bonusDoneText.setText("Заявки на доп. услуги выполнены: " + bonusDone);

        System.out.println("Bonus Stats - Total: " + totalBonus +
                ", In Work: " + bonusInWork +
                ", Done: " + bonusDone);
    }

    private void updateAllItemsList() {
        // Get both repair and bonus requests
        List<RepairRequest> repairRequests = repairViewModel.getRepairRequests().getValue();
        List<RepairRequest> bonusRequests = repairViewModel.getBonusRequests().getValue();

        List<RepairRequest> allItems = new ArrayList<>();

        if (repairRequests != null) {
            allItems.addAll(repairRequests);
        }
        if (bonusRequests != null) {
            allItems.addAll(bonusRequests);
        }

        // Update the existing adapter with all items
        allItemsAdapter.setItems(allItems);

        System.out.println("All items updated: " + allItems.size() + " total items");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Stats activity resumed - observing data...");
    }
}