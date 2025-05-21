package com.app.grocerybudgetplanner.adapter;

import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.model.GroceryItem;

public class CartAdapter extends ArrayAdapter<GroceryItem> {
    private final Context context;
    private final List<GroceryItem> items;
    private final OnCartItemListener listener;

    public interface OnCartItemListener {
        void onQuantityChanged(GroceryItem item, int newQuantity);
        void onRemoveItem(GroceryItem item);
    }
    public CartAdapter(Context context, List<GroceryItem> items, OnCartItemListener listener) {
        super(context, R.layout.cart_item_layout, items);
        this.context = context;
        this.items = items;
        this.listener = listener;
    }
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.cartItemNameText);
            holder.priceTextView = convertView.findViewById(R.id.cartItemPriceText);
            holder.quantityTextView = convertView.findViewById(R.id.cartItemQuantityText);
            holder.totalTextView = convertView.findViewById(R.id.cartItemTotalText);
            holder.increaseButton = convertView.findViewById(R.id.increaseQuantityButton);
            holder.decreaseButton = convertView.findViewById(R.id.decreaseQuantityButton);
            holder.removeButton = convertView.findViewById(R.id.removeItemButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GroceryItem item = items.get(position);
        if (item != null) {
            holder.nameTextView.setText(item.getName());
            holder.priceTextView.setText(String.format(Locale.getDefault(), "OMR %.3f", item.getPrice()));
            holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
            holder.totalTextView.setText(String.format(Locale.getDefault(), "Total: OMR %.3f", item.getTotalCost()));

            holder.increaseButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                listener.onQuantityChanged(item, newQuantity);
            });
            holder.decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    int newQuantity = item.getQuantity() - 1;
                    listener.onQuantityChanged(item, newQuantity);
                }
            });
            holder.removeButton.setOnClickListener(v -> listener.onRemoveItem(item));
        }
        return convertView;
    }
    static class ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        TextView totalTextView;
        Button increaseButton;
        Button decreaseButton;
        Button removeButton;
    }
}