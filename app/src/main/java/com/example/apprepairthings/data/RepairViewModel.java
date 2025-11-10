package com.example.apprepairthings.data;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class RepairViewModel extends AndroidViewModel {
    private RepairRepository repository;
    private LiveData<List<RepairRequest>> repairRequests;
    private LiveData<List<RepairRequest>> bonusRequests;

    public RepairViewModel(Application application) {
        super(application);
        repository = new RepairRepository(application);
        repairRequests = repository.getRepairRequests();
        bonusRequests = repository.getBonusRequests();
    }

    public LiveData<List<RepairRequest>> getRepairRequests() {
        return repairRequests;
    }

    public LiveData<List<RepairRequest>> getBonusRequests() {
        return bonusRequests;
    }

    public void insert(RepairRequest request) {
        repository.insert(request);
    }

    public void delete(RepairRequest request) {
        repository.delete(request);
    }

    public void update(RepairRequest request) {
        repository.update(request);
    }
}