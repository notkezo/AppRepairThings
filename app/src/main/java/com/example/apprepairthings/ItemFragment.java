package com.example.apprepairthings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apprepairthings.data.RepairRequest;
import com.example.apprepairthings.data.RepairViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private BonusItemRecyclerViewAdapter adapter;
    private RepairViewModel repairViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
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
        View view = inflater.inflate(R.layout.fragment_bonusrequests, container, false);

        System.out.println("=== ItemFragment onCreateView ===");

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Create adapter with empty list initially
            adapter = new BonusItemRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);

            // Observe the LiveData from ViewModel for bonus requests
            repairViewModel.getBonusRequests().observe(getViewLifecycleOwner(), new Observer<List<RepairRequest>>() {
                @Override
                public void onChanged(List<RepairRequest> bonusRequests) {
                    System.out.println("ItemFragment: Bonus data changed! Items: " + bonusRequests.size());
                    adapter.setItems(bonusRequests);
                }
            });

        } else {
            System.out.println("ItemFragment: View is not a RecyclerView!");
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("=== ItemFragment onResume ===");
    }
}