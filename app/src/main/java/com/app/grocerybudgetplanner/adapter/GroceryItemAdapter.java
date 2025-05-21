package com.app.grocerybudgetplanner.adapter;

import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.app.grocerybudgetplanner.R;
import com.app.grocerybudgetplanner.model.GroceryItem;

public class GroceryItemAdapter extends ArrayAdapter<GroceryItem> {

    private final Context context;
    private final List<GroceryItem> items;

    public GroceryItemAdapter(Context context, List<GroceryItem> items) {
        super(context, R.layout.grocery_item_layout, items);
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grocery_item_layout, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.itemNameText);
            holder.detailsTextView = convertView.findViewById(R.id.itemDetailsText);
            holder.priceTextView = convertView.findViewById(R.id.itemPriceText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GroceryItem item = items.get(position);
        if (item != null) {
            holder.nameTextView.setText(item.getName() != null ? item.getName() : "Unknown Item");
            holder.detailsTextView.setText(String.format(Locale.getDefault(),
                    "Quantity: %d", item.getQuantity()));
            holder.priceTextView.setText(String.format(Locale.getDefault(),
                    "OMR %.3f", item.getPrice()));
        }
        return convertView;
    }
    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }
    @Nullable
    @Override
    public GroceryItem getItem(int position) {
        return items != null && position < items.size() ? items.get(position) : null;
    }
    static class ViewHolder {
        TextView nameTextView;
        TextView detailsTextView;
        TextView priceTextView;
    }
}