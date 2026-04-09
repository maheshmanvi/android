package dev.maarch.mydemo.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import dev.maarch.mydemo.R;
import dev.maarch.mydemo.api.model.UserDetail;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserDetail> userList;

    private OnUserActionListener listener;


    public UserAdapter(List<UserDetail> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button btnAddPhoto;
        private final Button btnLocation;
        public ImageView imgProfile;
        TextView name, email, langLng;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            email = itemView.findViewById(R.id.txtEmail);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            // update location text box
            langLng = itemView.findViewById(R.id.txtLatLng);
            btnAddPhoto = itemView.findViewById(R.id.btnAddPhoto);
            btnLocation = itemView.findViewById(R.id.btnLocation);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserDetail user = userList.get(position);

        holder.name.setText(user.getName());

        // ✅ Show image OR button
        if (user.getImagePath() != null) {
            Log.d(TAG, "onBindViewHolder: " + user.getImagePath());
            holder.imgProfile.setVisibility(View.VISIBLE);
            holder.btnAddPhoto.setVisibility(View.GONE);

            Bitmap bitmap = BitmapFactory.decodeFile(user.getImagePath());
            holder.imgProfile.setImageBitmap(bitmap);

        } else {
            Log.d(TAG, "onBindViewHolder: " + user.getImagePath());

            holder.imgProfile.setVisibility(View.GONE);
            holder.btnAddPhoto.setVisibility(View.VISIBLE);
        }

        holder.langLng.setText(user.getAddress().getGeo().getLng() + ", " + user.getAddress().getGeo().getLat());

        holder.email.setText(user.getEmail());

        // 📸 Add Photo Click
        holder.btnAddPhoto.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddPhotoClick(user);
            }
        });

        // 📍 Location Update Click
        holder.btnLocation.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLocationClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserActionListener {
        void onAddPhotoClick(UserDetail user);
        void onLocationClick(UserDetail user);
    }


}

