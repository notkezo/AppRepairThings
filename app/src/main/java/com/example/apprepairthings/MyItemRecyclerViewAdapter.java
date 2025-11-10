package com.example.apprepairthings;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apprepairthings.data.RepairRequest;

import java.util.ArrayList;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<RepairRequest> mValues;
    private OnItemClickListener mListener;
    private int[] avatarColors;

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(int position, RepairRequest item);
        void onItemLongClick(int position, RepairRequest item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // Updated constructors
    public MyItemRecyclerViewAdapter() {
        mValues = new ArrayList<>();
        initAvatarColors();
    }

    public MyItemRecyclerViewAdapter(List<RepairRequest> items) {
        mValues = items;
        initAvatarColors();
    }

    private void initAvatarColors() {
        // Material Design colors for avatars
        avatarColors = new int[]{
                0xFFF44336, // Red
                0xFFE91E63, // Pink
                0xFF9C27B0, // Purple
                0xFF673AB7, // Deep Purple
                0xFF3F51B5, // Indigo
                0xFF2196F3, // Blue
                0xFF03A9F4, // Light Blue
                0xFF00BCD4, // Cyan
                0xFF009688, // Teal
                0xFF4CAF50, // Green
                0xFF8BC34A, // Light Green
                0xFFCDDC39, // Lime
                0xFFFFC107, // Amber
                0xFFFF9800, // Orange
                0xFFFF5722, // Deep Orange
                0xFF795548, // Brown
                0xFF9E9E9E, // Grey
                0xFF607D8B  // Blue Grey
        };
    }

    // Method to update data from LiveData
    public void setItems(List<RepairRequest> items) {
        mValues = items;
        notifyDataSetChanged();
        System.out.println("Adapter updated with " + items.size() + " items");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repair_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RepairRequest item = mValues.get(position);
        holder.mItem = item;

        // Bind data to all TextViews using getter methods
        holder.mNameView.setText(item.getName());
        holder.mPhoneView.setText("Номер телефона: " + item.getPhone());
        holder.mDeviceView.setText("Устройство: " + item.getDevice());
        holder.mDateView.setText("Дата: " + item.getDate());
        holder.mTimeView.setText("Время: " + item.getTime());
        holder.mStatusView.setText("Статус: " + item.getStatus()); // Add status display

        // Set avatar
        setupAvatar(holder.mAvatarView, item.getName());

        // Set background color based on status - NOW ONLY AFFECTS STATUS TEXT
        setStatusBackground(holder.mStatusView, item.getStatus());

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(position, item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mListener != null) {
                mListener.onItemLongClick(position, item);
                return true; // Consume the long press
            }
            return false;
        });

        System.out.println("Binding: " + item.getName() + " | Status: " + item.getStatus());
    }

    private void setupAvatar(TextView avatarView, String name) {
        if (name != null && !name.isEmpty()) {
            // Get first letter and make it uppercase
            String firstLetter = name.substring(0, 1).toUpperCase();
            avatarView.setText(firstLetter);

            // Get consistent color based on name hash
            int colorIndex = Math.abs(name.hashCode()) % avatarColors.length;
            int backgroundColor = avatarColors[colorIndex];

            // Create a circular background
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(backgroundColor);
            circle.setSize(40, 40);

            avatarView.setBackground(circle);
        } else {
            avatarView.setText("?");
            avatarView.setBackgroundColor(Color.GRAY);
        }
    }

    private void setStatusBackground(TextView statusView, String status) {
        int backgroundColor;
        int textColor = Color.BLACK; // Default text color

        switch (status) {
            case "заявка в обработке":
                backgroundColor = Color.parseColor("#FFF9C4"); // Light yellow
                textColor = Color.parseColor("#F57F17"); // Darker yellow for text
                break;
            case "заявка выполнена":
                backgroundColor = Color.parseColor("#C8E6C9"); // Light green
                textColor = Color.parseColor("#2E7D32"); // Darker green for text
                break;
            case "ожидание статуса":
            default:
                backgroundColor = Color.parseColor("#F5F5F5"); // Light gray
                textColor = Color.parseColor("#616161"); // Darker gray for text
                break;
        }

        // Create a rounded background drawable
        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(backgroundColor);
        roundedBackground.setCornerRadius(12f); // 12dp corner radius

        statusView.setBackground(roundedBackground);
        statusView.setTextColor(textColor);

        // Remove the manual padding since we're using the drawable's padding
        statusView.setPadding(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAvatarView;
        public final TextView mNameView;
        public final TextView mPhoneView;
        public final TextView mDeviceView;
        public final TextView mDateView;
        public final TextView mTimeView;
        public final TextView mStatusView;
        public RepairRequest mItem;

        public ViewHolder(View view) {
            super(view);
            // Initialize all TextViews using findViewById
            mAvatarView = view.findViewById(R.id.avatar);
            mNameView = view.findViewById(R.id.itemNumber);
            mPhoneView = view.findViewById(R.id.content);
            mDeviceView = view.findViewById(R.id.deviceText);
            mDateView = view.findViewById(R.id.dateText);
            mTimeView = view.findViewById(R.id.timeText);
            mStatusView = view.findViewById(R.id.statusText);

            // Make the item clickable and focusable for better visual feedback
            itemView.setClickable(true);
            itemView.setFocusable(true);

            System.out.println("ViewHolder created - found all views: " +
                    (mAvatarView != null && mNameView != null && mPhoneView != null && mDeviceView != null &&
                            mDateView != null && mTimeView != null && mStatusView != null));
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}