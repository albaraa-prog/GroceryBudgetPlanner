// Generated by view binder compiler. Do not edit!
package com.app.grocerybudgetplanner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.app.grocerybudgetplanner.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class GroceryItemLayoutBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView itemDetailsText;

  @NonNull
  public final TextView itemNameText;

  @NonNull
  public final TextView itemPriceText;

  private GroceryItemLayoutBinding(@NonNull LinearLayout rootView,
      @NonNull TextView itemDetailsText, @NonNull TextView itemNameText,
      @NonNull TextView itemPriceText) {
    this.rootView = rootView;
    this.itemDetailsText = itemDetailsText;
    this.itemNameText = itemNameText;
    this.itemPriceText = itemPriceText;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static GroceryItemLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static GroceryItemLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.grocery_item_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static GroceryItemLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.itemDetailsText;
      TextView itemDetailsText = ViewBindings.findChildViewById(rootView, id);
      if (itemDetailsText == null) {
        break missingId;
      }

      id = R.id.itemNameText;
      TextView itemNameText = ViewBindings.findChildViewById(rootView, id);
      if (itemNameText == null) {
        break missingId;
      }

      id = R.id.itemPriceText;
      TextView itemPriceText = ViewBindings.findChildViewById(rootView, id);
      if (itemPriceText == null) {
        break missingId;
      }

      return new GroceryItemLayoutBinding((LinearLayout) rootView, itemDetailsText, itemNameText,
          itemPriceText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
