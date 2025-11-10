package com.example.apprepairthings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apprepairthings.data.RepairRequest;
import com.example.apprepairthings.data.RepairViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class RepairRequests extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private MyItemRecyclerViewAdapter adapter;
    private RepairViewModel repairViewModel;
    private String currentSort = "date"; // default sort

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepairRequests() {
    }

    public static RepairRequests newInstance(int columnCount) {
        RepairRequests fragment = new RepairRequests();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // Initialize ViewModel
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repair_requests_list, container, false);

        System.out.println("=== RepairRequests onCreateView ===");

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        if (recyclerView != null) {
            System.out.println("RecyclerView found!");

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                System.out.println("Using LinearLayoutManager");
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                System.out.println("Using GridLayoutManager with " + mColumnCount + " columns");
            }

            // Create adapter with empty list initially
            adapter = new MyItemRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);

            // Set item click listener for long-press functionality
            adapter.setOnItemClickListener(new MyItemRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, RepairRequest item) {
                    // Regular click - optional
                    Toast.makeText(getContext(), "Нажато: " + item.getName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemLongClick(int position, RepairRequest item) {
                    showItemOptionsDialog(item, position);
                }
            });

            // Observe the LiveData from ViewModel
            repairViewModel.getRepairRequests().observe(getViewLifecycleOwner(), new Observer<List<RepairRequest>>() {
                @Override
                public void onChanged(List<RepairRequest> repairRequests) {
                    System.out.println("Data changed! Repair items: " + repairRequests.size());

                    // Apply current sorting to the new data
                    List<RepairRequest> sortedList = applySorting(repairRequests);

                    adapter.setItems(sortedList);

                    // Debug: Print all items
                    for (int i = 0; i < sortedList.size(); i++) {
                        RepairRequest item = sortedList.get(i);
                        System.out.println("Item " + i + ": " + item.getName() + " | " + item.getPhone() + " | " + item.getDevice() + " | Status: " + item.getStatus());
                    }

                    // Add some visual debugging
                    if (adapter.getItemCount() == 0) {
                        System.out.println("WARNING: Adapter has 0 items!");
                    } else {
                        System.out.println("Adapter should display " + adapter.getItemCount() + " items");
                    }
                }
            });

            System.out.println("Adapter set up, waiting for data...");

        } else {
            System.out.println("ERROR: RecyclerView not found! Check if fragment_repair_requests_list.xml has a RecyclerView with id 'list'");

            // Let's see what view we actually got
            System.out.println("View class: " + view.getClass().getSimpleName());
            System.out.println("View ID: " + view.getId());
        }

        return view;
    }

    // Sorting method called from MainActivity
    public void sortItems(String sortType) {
        currentSort = sortType;
        List<RepairRequest> currentItems = repairViewModel.getRepairRequests().getValue();
        if (currentItems != null && adapter != null) {
            List<RepairRequest> sortedList = applySorting(currentItems);
            adapter.setItems(sortedList);
        }
    }

    private List<RepairRequest> applySorting(List<RepairRequest> items) {
        List<RepairRequest> sortedList = new ArrayList<>(items);

        if (currentSort.equals("alphabet")) {
            // Sort by name alphabetically
            Collections.sort(sortedList, (item1, item2) ->
                    item1.getName().compareToIgnoreCase(item2.getName()));
        } else {
            // Sort by date (newest first)
            Collections.sort(sortedList, (item1, item2) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = sdf.parse(item1.getDate());
                    Date date2 = sdf.parse(item2.getDate());
                    return date2.compareTo(date1); // newest first
                } catch (ParseException e) {
                    return 0;
                }
            });
        }

        return sortedList;
    }

    private void showItemOptionsDialog(RepairRequest item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Выберите действие");
        builder.setMessage("Что вы хотите сделать с " + item.getName() + "?\nТекущий статус: " + item.getStatus());

        builder.setPositiveButton("Изменить заявку", (dialog, which) -> {
            editRepairRequest(item);
        });

        builder.setNegativeButton("Удалить", (dialog, which) -> {
            deleteRepairRequest(item, position);
        });

        // Add status change button
        builder.setNeutralButton("Изменить статус", (dialog, which) -> {
            showStatusChangeDialog(item);
        });

        builder.show();
    }

    private void showStatusChangeDialog(RepairRequest item) {
        String[] statusOptions = {"ожидание статуса", "заявка в обработке", "заявка выполнена"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Изменить статус");
        builder.setSingleChoiceItems(statusOptions, getCurrentStatusIndex(item.getStatus()), (dialog, which) -> {
            String newStatus = statusOptions[which];
            item.setStatus(newStatus);
            repairViewModel.update(item);
            Toast.makeText(getContext(), "Статус изменен на: " + newStatus, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private int getCurrentStatusIndex(String currentStatus) {
        switch (currentStatus) {
            case "ожидание статуса": return 0;
            case "заявка в обработке": return 1;
            case "заявка выполнена": return 2;
            default: return 0;
        }
    }

    private void editRepairRequest(RepairRequest item) {
        // Open edit activity with the item data
        Intent intent = new Intent(getContext(), CreateReqRepair.class);
        intent.putExtra("EDIT_MODE", true);
        intent.putExtra("ITEM_ID", item.getId());
        intent.putExtra("NAME", item.getName());
        intent.putExtra("PHONE", item.getPhone());
        intent.putExtra("DEVICE", item.getDevice());
        intent.putExtra("DATE", item.getDate());
        intent.putExtra("TIME", item.getTime());
        intent.putExtra("STATUS", item.getStatus()); // Pass the status
        startActivity(intent);
    }

    private void deleteRepairRequest(RepairRequest item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Подтвердить удаление");
        builder.setMessage("Вы уверены, что хотите удалить " + item.getName() + "?");

        builder.setPositiveButton("Удалить", (dialog, which) -> {
            repairViewModel.delete(item);
            Toast.makeText(getContext(), "Заявка удалена", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("=== RepairRequests onResume ===");

        if (adapter != null) {
            System.out.println("Adapter has " + adapter.getItemCount() + " items");
        } else {
            System.out.println("Adapter is null");
        }
    }
}