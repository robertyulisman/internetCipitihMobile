package com.gadihkaratau.lamejorescancionesreik.restApi;

import com.gadihkaratau.lamejorescancionesreik.models.infoApp.InfoApp;
import com.gadihkaratau.lamejorescancionesreik.models.setting.Setting;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api/apps/audios")
    Call<List<Song>> getSongList(@Header("access-token") String access_token, @Query("packageName") String packageName);

    @GET("api/apps/ads")
    Call<Setting> getSetting(@Header("access-token") String access_token, @Query("packageName") String packageName);

    @GET("/gp/apps/{packageName}")
    Call<InfoApp> getInfoApp(@Path("packageName") String packageName);

    @PATCH("api/musics/counterViews")
    Call<ResponseBody> counterViewApp(@Header("access-token") String access_token, @Query("id") String id);

}
