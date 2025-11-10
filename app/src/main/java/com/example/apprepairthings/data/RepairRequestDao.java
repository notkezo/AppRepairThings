package com.example.apprepairthings.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Update;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import java.util.List;

@Dao
public interface RepairRequestDao {

    @Insert
    void insert(RepairRequest request);

    @Delete
    void delete(RepairRequest request);

    @Update
    void update(RepairRequest request);

    @Query("SELECT * FROM repair_requests WHERE type = 'repair' ORDER BY id DESC")
    LiveData<List<RepairRequest>> getRepairRequests();

    @Query("SELECT * FROM repair_requests WHERE type = 'bonus' ORDER BY id DESC")
    LiveData<List<RepairRequest>> getBonusRequests();

    @Query("SELECT * FROM repair_requests ORDER BY id DESC")
    LiveData<List<RepairRequest>> getAllRequests();
}