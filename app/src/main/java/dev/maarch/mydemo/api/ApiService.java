package dev.maarch.mydemo.api;
import java.util.List;

import dev.maarch.mydemo.api.model.UserDetail;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("users")
    Call<List<UserDetail>> getUser();
}
