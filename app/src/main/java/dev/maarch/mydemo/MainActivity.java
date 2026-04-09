package dev.maarch.mydemo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;

import dev.maarch.mydemo.adapter.UserAdapter;
import dev.maarch.mydemo.api.ApiService;
import dev.maarch.mydemo.api.RetrofitClient;
import dev.maarch.mydemo.api.model.UserDetail;
import dev.maarch.mydemo.database.DBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnFetchData;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private UserDetail selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFetchData = findViewById(R.id.btn_fetch_data);
        recyclerView = findViewById(R.id.recyclerView);

        btnFetchData.setOnClickListener(v -> fetchUserData());

        dbHelper = new DBHelper(MainActivity.this.getApplicationContext());

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, 100);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
        }

        List<UserDetail> users = dbHelper.getUsers();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (!users.isEmpty()) {
            Log.d("DB", "Data already exists!");
            loadFromDB();
        } else {


        }


    }


    private void loadFromDB() {
        List<UserDetail> userList = dbHelper.getUsers();

        if (userList != null && !userList.isEmpty()) {
            adapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
                @Override
                public void onAddPhotoClick(UserDetail user) {
                    selectedUser = user;
                    openCameraX();
                }

                @Override
                public void onLocationClick(UserDetail user) {
                    Log.d(TAG, "onLocationClick: " + user.toString());
                    updateLocation(user);
                }

            });
            recyclerView.setAdapter(adapter);
            Toast.makeText(this, "Loaded from SQLite", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchUserData() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<List<UserDetail>> call = apiService.getUser();

        call.enqueue(new Callback<List<UserDetail>>() {
            @Override
            public void onResponse(Call<List<UserDetail>> call, Response<List<UserDetail>> response) {
                if (response.isSuccessful()) {
//                    User user = response.body();
//                    System.out.println(user.getName());
                    Log.d("TAG", "onResponse: " + response.body().toString());

                    List<UserDetail> users = response.body();

                    dbHelper.insertUsers(users);

                    Log.d("DB", "Data saved successfully!");

                }
            }

            @Override
            public void onFailure(Call<List<UserDetail>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void openCameraX() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {

            String path = data.getStringExtra("path");

            selectedUser.setImagePath(path);

            DBHelper db = new DBHelper(this);
            db.updateUserImage(selectedUser.getId(), path);

            loadFromDB();
        }
    }

    private void updateLocation(UserDetail user) {

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // Save to DB
                        DBHelper db = new DBHelper(this);
                        db.updateLocation(user.getId(), lat, lng);

                        Toast.makeText(this,
                                "Updated: " + lat + ", " + lng,
                                Toast.LENGTH_SHORT).show();

                        loadFromDB();

                    } else {
                        Toast.makeText(this,
                                "Location is null. Turn ON GPS.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}