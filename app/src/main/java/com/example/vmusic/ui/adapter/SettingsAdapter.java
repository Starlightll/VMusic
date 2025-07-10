package com.example.vmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.helper.SessionManager;

import java.util.List;

public class SettingsAdapter  extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    List<String> items;

    public SettingsAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_profile, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_logout, parent, false);
            return new FooterViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_option, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).textSetting.setText(items.get(position - 1)); // -1 vì header chiếm vị trí 0
        } else if (holder instanceof HeaderViewHolder) {
            // Gắn avatar, tên người dùng v.v.
        } else if (holder instanceof FooterViewHolder) {
            // Gắn xử lý click "Đăng xuất"
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        else if (position == getItemCount() - 1) return TYPE_FOOTER;
        else return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items.size() + 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textSetting;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSetting = itemView.findViewById(R.id.textSetting);
        }
    }

    public class HeaderViewHolder extends ViewHolder {
        TextView userName;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textUserName);
            // Gắn tên người dùng từ SessionManager hoặc ViewModel
            SessionManager sessionManager = new SessionManager(itemView.getContext());
            userName.setText(sessionManager.getUsername() != null ? sessionManager.getUsername() : "Guest");
        }
    }

    public class ItemViewHolder extends ViewHolder {
        TextView textSetting;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textSetting = itemView.findViewById(R.id.textSetting);
        }
    }

    public class FooterViewHolder extends ViewHolder {
        Button logoutButton;
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            logoutButton = itemView.findViewById(R.id.btnLogout);
            logoutButton.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(), "Đăng xuất", Toast.LENGTH_SHORT).show();
            });
        }
    }


}
