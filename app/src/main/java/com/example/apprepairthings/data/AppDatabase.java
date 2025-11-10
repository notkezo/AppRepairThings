package com.example.apprepairthings.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {RepairRequest.class}, version = 2, exportSchema = false) // Changed from 1 to 2
public abstract class AppDatabase extends RoomDatabase {
    public abstract RepairRequestDao repairRequestDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "repair_database"
                            ).fallbackToDestructiveMigration() // This will clear old data on schema change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}