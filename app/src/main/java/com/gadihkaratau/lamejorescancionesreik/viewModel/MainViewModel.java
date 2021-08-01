package com.gadihkaratau.lamejorescancionesreik.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gadihkaratau.lamejorescancionesreik.BuildConfig;
import com.gadihkaratau.lamejorescancionesreik.models.infoApp.InfoApp;
import com.gadihkaratau.lamejorescancionesreik.models.setting.Setting;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.restApi.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<Song>> listSongMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Setting> settingMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<InfoApp> infoAppMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> counterViewMusic = new MutableLiveData<>();

    public MutableLiveData<InfoApp> getInfoAppMutableLiveData() {
        return infoAppMutableLiveData;
    }

    public void setInfoAppMutableLiveData(String packageName) {
        Call<InfoApp> infoAppCall = ApiClient.serviceApi(1).getInfoApp(packageName);
        infoAppCall.enqueue(new Callback<InfoApp>() {
            @Override
            public void onResponse(Call<InfoApp> call, Response<InfoApp> response) {
                infoAppMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<InfoApp> call, Throwable t) {
                infoAppMutableLiveData.postValue(null);
            }
        });
    }

    public MutableLiveData<List<Song>> getListSongMutableLiveData() {
        return listSongMutableLiveData;
    }

    public void setListSongMutableLiveData(String packageName) {
        Call<List<Song>> listSongCall = ApiClient.serviceApi(1).getSongList(BuildConfig.ACCESS_TOKEN, packageName);
        listSongCall.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                listSongMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                listSongMutableLiveData.postValue(null);
            }
        });
    }

    public MutableLiveData<Setting> getSettingMutableLiveData() {
        return settingMutableLiveData;
    }

    public void setSettingMutableLiveData(String packageName) {
        Call<Setting> settingCall = ApiClient.serviceApi(1).getSetting(BuildConfig.ACCESS_TOKEN, packageName);
        settingCall.enqueue(new Callback<Setting>() {
            @Override
            public void onResponse(Call<Setting> call, Response<Setting> response) {
                settingMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Setting> call, Throwable t) {
                settingMutableLiveData.postValue(null);
            }
        });
    }

    public MutableLiveData<ResponseBody> getCounterViewMusic() {
        return counterViewMusic;
    }

    public void setCounterViewMusic(String idMusic) {
        Call<ResponseBody> responseBodyCall = ApiClient.serviceApi(1).counterViewApp(BuildConfig.ACCESS_TOKEN, idMusic);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                counterViewMusic.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                counterViewMusic.postValue(null);
            }
        });

    }
}
