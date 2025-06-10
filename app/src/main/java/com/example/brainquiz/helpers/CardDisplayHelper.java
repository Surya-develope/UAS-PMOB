package com.example.brainquiz.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brainquiz.R;

/**
 * Helper class untuk membuat dan menampilkan card UI yang konsisten
 */
public class CardDisplayHelper {
    
    private Context context;
    private float density;
    private int screenWidth;
    private int cardWidth;
    
    public interface CardActionListener {
        void onEditClick(Object item);
        void onDeleteClick(Object item);
        void onDeleteSuccess();
    }
    
    public CardDisplayHelper(Context context) {
        this.context = context;
        this.density = context.getResources().getDisplayMetrics().density;
        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.cardWidth = (screenWidth / 2) - (int)(32 * density);
    }
    
    /**
     * Membuat card dengan layout yang konsisten
     */
    public LinearLayout createCard() {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        
        // Layout Parameters
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cardWidth;
        params.height = (int)(160 * density);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        params.setMargins(
                (int) (8 * density),
                (int) (8 * density),
                (int) (8 * density),
                (int) (8 * density)
        );
        card.setLayoutParams(params);
        
        // Styling
        card.setPadding(
                (int) (16 * density),
                (int) (16 * density),
                (int) (16 * density),
                (int) (16 * density)
        );
        card.setBackgroundResource(R.drawable.bg_tingkatan_card);
        
        return card;
    }
    
    /**
     * Membuat content layout dengan icon dan text
     */
    public LinearLayout createContentLayout(int iconResource, String text) {
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentLayout.setLayoutParams(contentParams);
        
        // ImageView
        ImageView icon = new ImageView(context);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (64 * density),
                (int) (64 * density)
        );
        iconParams.gravity = Gravity.CENTER;
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconResource);
        icon.setColorFilter(Color.WHITE);
        contentLayout.addView(icon);
        
        // TextView nama
        TextView tvNama = new TextView(context);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.CENTER;
        textParams.topMargin = (int) (12 * density);
        tvNama.setLayoutParams(textParams);
        
        String nama = text != null ? text : "Nama tidak tersedia";
        tvNama.setText(nama);
        tvNama.setTextColor(Color.WHITE);
        tvNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvNama.setTypeface(null, Typeface.BOLD);
        contentLayout.addView(tvNama);
        
        return contentLayout;
    }
    
    /**
     * Membuat menu icon dengan action listener
     */
    public ImageView createMenuIcon(Object item, String itemName, CardActionListener listener) {
        ImageView menuIcon = new ImageView(context);
        menuIcon.setImageResource(R.drawable.ic_more_vert);
        menuIcon.setColorFilter(Color.WHITE);
        
        LinearLayout.LayoutParams menuParams = new LinearLayout.LayoutParams(
                (int) (24 * density),
                (int) (24 * density)
        );
        menuParams.gravity = Gravity.END | Gravity.TOP;
        menuIcon.setLayoutParams(menuParams);
        
        // Set click listener for menu
        menuIcon.setOnClickListener(view -> showMenuDialog(item, itemName, listener));
        
        return menuIcon;
    }
    
    /**
     * Menampilkan dialog menu dengan opsi Edit dan Hapus
     */
    private void showMenuDialog(Object item, String itemName, CardActionListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        // Opsi Edit
        LinearLayout itemEdit = dialog.findViewById(R.id.menu_edit);
        if (itemEdit != null) {
            itemEdit.setOnClickListener(v -> {
                listener.onEditClick(item);
                dialog.dismiss();
            });
        }
        
        // Opsi Hapus
        LinearLayout itemHapus = dialog.findViewById(R.id.itemHapus);
        if (itemHapus != null) {
            itemHapus.setOnClickListener(v -> {
                showDeleteConfirmation(item, itemName, listener);
                dialog.dismiss();
            });
        }
        
        dialog.show();
    }
    
    /**
     * Menampilkan konfirmasi hapus
     */
    private void showDeleteConfirmation(Object item, String itemName, CardActionListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus " + itemName + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    listener.onDeleteClick(item);
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    /**
     * Membuat TextView untuk "no data" message
     */
    public TextView createNoDataMessage(String message) {
        TextView noDataText = new TextView(context);
        noDataText.setText(message);
        noDataText.setTextSize(16);
        noDataText.setTextColor(Color.GRAY);
        noDataText.setGravity(Gravity.CENTER);
        noDataText.setPadding(32, 64, 32, 64);
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(0, 2); // Span 2 columns
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        noDataText.setLayoutParams(params);
        
        return noDataText;
    }
    
    /**
     * Setup grid layout dengan column count
     */
    public void setupGrid(GridLayout gridLayout) {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(2);
    }
}
