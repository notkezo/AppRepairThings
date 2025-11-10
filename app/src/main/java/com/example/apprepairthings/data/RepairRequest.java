package com.example.apprepairthings.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "repair_requests")
public class RepairRequest {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String phone;
    public String device;
    public String date;
    public String time;
    public String type; // "repair" or "bonus"
    public String status; // New field: "pending", "request in work", "request completed"

    public RepairRequest(String name, String phone, String device, String date, String time, String type, String status) {
        this.name = name;
        this.phone = phone;
        this.device = device;
        this.date = date;
        this.time = time;
        this.type = type;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getDevice() { return device; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getType() { return type; }
    public String getStatus() { return status; } // New getter

    // Setter for id (required by Room)
    public void setId(int id) { this.id = id; }

    // Setter for status
    public void setStatus(String status) { this.status = status; }
}