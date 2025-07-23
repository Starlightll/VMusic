package com.example.vmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.MainActivity;
import com.example.vmusic.R;
import com.example.vmusic.helper.SessionManager;

import java.util.List;

public class SettingsAdapter  extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    List<String> items;
    private NavController navController;

    public SettingsAdapter(List<String> items, NavController navController) {
        this.items = items;
        this.navController = navController;
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
            ImageView avatarImageView = holder.itemView.findViewById(R.id.avatar);
            SessionManager sessionManager = new SessionManager(holder.itemView.getContext());
             Glide.with(holder.itemView.getContext())
                     .load(sessionManager.getUserAvatar())
                     .placeholder(R.drawable.ic_launcher_background)
                     .into(avatarImageView);
            holder.itemView.findViewById(R.id.item_settings_profile).setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Chỉnh sửa thông tin cá nhân", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_settingsFragment_to_profileFragment);
            });
        } else if (holder instanceof FooterViewHolder) {
            holder.itemView.findViewById(R.id.btnLogout).setOnClickListener(v ->{
                Toast.makeText(holder.itemView.getContext(), "Đăng xuất", Toast.LENGTH_SHORT).show();
                SessionManager sessionManager = new SessionManager(holder.itemView.getContext());
                sessionManager.logout();
                AppCompatActivity activity = (AppCompatActivity) holder.itemView.getContext();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).switchToAuthNavGraph();
                }
            });
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
