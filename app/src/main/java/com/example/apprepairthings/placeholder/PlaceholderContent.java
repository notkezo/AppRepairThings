package com.example.apprepairthings.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for storing repair and bonus request data.
 */
public class PlaceholderContent {

    /**
     * List for repair request items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * List for bonus request items.
     */
    public static final List<PlaceholderItem> BONUS_ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    // Removed the static block that created sample items
    // Now the lists will start empty

    /**
     * A item representing a repair request.
     */
    public static class PlaceholderItem {
        public final String id;
        public final String name;
        public final String phone;
        public final String device;
        public final String date;
        public final String time;

        public PlaceholderItem(String id, String name, String phone, String device, String date, String time) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.device = device;
            this.date = date;
            this.time = time;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}