package com.example.apprepairthings.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepairRepository {
    private RepairRequestDao repairRequestDao;
    private ExecutorService executorService;

    public RepairRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        repairRequestDao = database.repairRequestDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(RepairRequest request) {
        executorService.execute(() -> repairRequestDao.insert(request));
    }

    public void delete(RepairRequest request) {
        executorService.execute(() -> repairRequestDao.delete(request));
    }

    public void update(RepairRequest request) {
        executorService.execute(() -> repairRequestDao.update(request));
    }

    public LiveData<List<RepairRequest>> getRepairRequests() {
        return repairRequestDao.getRepairRequests();
    }

    public LiveData<List<RepairRequest>> getBonusRequests() {
        return repairRequestDao.getBonusRequests();
    }
}