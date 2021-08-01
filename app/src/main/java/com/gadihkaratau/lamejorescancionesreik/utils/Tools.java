package com.gadihkaratau.lamejorescancionesreik.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.BuildConfig;
import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.adapters.ListNamePlaylistAdapter;
import com.gadihkaratau.lamejorescancionesreik.asynTask.LoadNamePlaylistAsync;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.NamePlaylistHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.PlaylistSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadNamePlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnAddPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnDialogCallback;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Tools {

    public static Bitmap bitmap;

    public static void setSystemBarColor(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(color));
        }
    }

    public static void setSystemBarLight(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = act.findViewById(android.R.id.content);
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }


    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static boolean toggleArrow(boolean show, View view, boolean delay) {
        if (show) {
            view.animate().setDuration(delay ? 200 : 0).rotation(180);
            return true;
        } else {
            view.animate().setDuration(delay ? 200 : 0).rotation(0);
            return false;
        }
    }

    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) continue;
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }


    public static void rateAction(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void redirectAction(Activity activity, String urlRedirect) {
        Uri uri = Uri.parse(urlRedirect);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromURL(Context context, String url) {
        bitmap = null;
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        return bitmap;
    }

    public static Bitmap getBitmapFromURL(Context context, String url, int resize) {
        bitmap = null;
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().override(resize, resize))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        return bitmap;
    }

    public static MediaDescriptionCompat getMediaDescription(Context context, Song song) {
        Bundle extras = new Bundle();
        Bitmap bitmap = getBitmapFromURL(context, song.getImg(), 50);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
        return new MediaDescriptionCompat.Builder()
                .setIconBitmap(bitmap)
                .setTitle(song.getTitle())
                .setSubtitle(song.getArtist())
                .setExtras(extras)
                .build();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String convertLongDurationToTimeAudio(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static void showDialogPlaylist(Context context, Song song, OnAddPlaylistCallback onAddPlaylistCallback) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_playlist);

        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        RecyclerView recyclerViewPlaylist = dialog.findViewById(R.id.recyclerView);
        AppCompatButton btnCreateNewPlaylist = dialog.findViewById(R.id.btnCreateNew);
        LinearLayout ll_add_playlist = dialog.findViewById(R.id.ll_add_playlist);
        EditText edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);
        AppCompatButton btnAddNamePlaylist = dialog.findViewById(R.id.btnAddNamePlaylist);

        ListNamePlaylistAdapter listNamePlaylistAdapter = new ListNamePlaylistAdapter();
        recyclerViewPlaylist.setAdapter(listNamePlaylistAdapter);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewPlaylist.setHasFixedSize(true);

        new LoadNamePlaylistAsync(new LoadNamePlaylistCallback() {
            @Override
            public void preExecute() {

            }

            @Override
            public void postExecute(ArrayList<NamePlaylist> namePlaylists) {
                if (namePlaylists.size() > 0)
                    listNamePlaylistAdapter.setNamePlaylists(namePlaylists);
                else {

                    btnCreateNewPlaylist.setVisibility(View.GONE);
                    ll_add_playlist.setVisibility(View.VISIBLE);

                }

            }
        }, NamePlaylistHelper.getInstance(context)).execute();

        listNamePlaylistAdapter.setOnPlaylistAdapterClicked(new ListNamePlaylistAdapter.OnPlaylistAdapterClicked() {
            @Override
            public void onClicked(NamePlaylist namePlaylist) {
                boolean isExist = PlaylistSongHelper.getInstance(context).isExist(namePlaylist.getId(), song.getId());
                if (isExist) {
                    Toast.makeText(context, song.getTitle() + " is ready in " + namePlaylist.getName(), Toast.LENGTH_SHORT).show();
                    return;
                }
                long insertSong = PlaylistSongHelper.getInstance(context).insert(song, namePlaylist.getId());
                if (insertSong > 0) {
                    Toast.makeText(context, "Success add to playlist", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onAddPlaylistCallback.onSuccessAddPlaylist(song);
                } else
                    Toast.makeText(context, "Failed add to playlist", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDialogCreateNewPlaylist(context, song, new OnAddPlaylistCallback() {
                    @Override
                    public void onSuccessAddPlaylist(Song song) {
                        onAddPlaylistCallback.onSuccessAddPlaylist(song);
                    }
                });
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAddNamePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = edtPlaylistName.getText().toString();
                if (playlistName.isEmpty())
                    edtPlaylistName.setError(context.getString(R.string.name_playlist_cannot_be_empty));
                else {
                    long resultInsert = NamePlaylistHelper.getInstance(context).insert(playlistName);
                    long insertSong = PlaylistSongHelper.getInstance(context).insert(song, String.valueOf(resultInsert));
                    if (resultInsert > 0 && insertSong > 0) {
                        Toast.makeText(context, "Success add to playlist", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        onAddPlaylistCallback.onSuccessAddPlaylist(song);
                    } else
                        Toast.makeText(context, "Failed add to playlist", Toast.LENGTH_SHORT).show();

                }


            }
        });
        dialog.show();
    }

    public static void showDialogCreateNewPlaylist(Context context, Song song, OnAddPlaylistCallback onAddPlaylistCallback) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_playlist);

        EditText edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);
        AppCompatButton btnAdd = dialog.findViewById(R.id.btnAdd);
        ImageView imgClose = dialog.findViewById(R.id.img_close);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = edtPlaylistName.getText().toString();
                if (playlistName.isEmpty())
                    edtPlaylistName.setError(context.getString(R.string.name_playlist_cannot_be_empty));
                else {
                    long resultInsert = NamePlaylistHelper.getInstance(context).insert(playlistName);
                    long insertSong = PlaylistSongHelper.getInstance(context).insert(song, String.valueOf(resultInsert));
                    if (resultInsert > 0 && insertSong > 0) {
                        Toast.makeText(context, "Success add to playlist", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        onAddPlaylistCallback.onSuccessAddPlaylist(song);
                    } else
                        Toast.makeText(context, "Failed add to playlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showDialogEditPlaylist(Context context, NamePlaylist namePlaylist, OnAddPlaylistCallback onAddPlaylistCallback) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_playlist);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        EditText edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);
        AppCompatButton btnAdd = dialog.findViewById(R.id.btnAdd);
        ImageView imgClose = dialog.findViewById(R.id.img_close);

        tvTitle.setText("Edit Playlist");
        edtPlaylistName.setText(namePlaylist.getName());
        btnAdd.setText("Edit");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = edtPlaylistName.getText().toString();
                if (playlistName.isEmpty())
                    edtPlaylistName.setError(context.getString(R.string.name_playlist_cannot_be_empty));
                else {
                    long resultInsert = NamePlaylistHelper.getInstance(context).update(namePlaylist.getId(), playlistName);
                    if (resultInsert > 0) {
                        Toast.makeText(context, "Success Edit playlist", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        onAddPlaylistCallback.onSuccessAddPlaylist(null);
                    } else
                        Toast.makeText(context, "Failed add to playlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showAlertDialog(AlertDialog.Builder alertDialog, String title, String message, OnDialogCallback onDialogCallback) {
        alertDialog.setTitle(title);
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogCallback.onNoClicked();
            }
        });
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogCallback.onOkClicked();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void showAlertDialog(AlertDialog.Builder alertDialog, String title, String message, String txtPositiveButton, String txtNegativeButton, OnDialogCallback onDialogCallback) {
        alertDialog.setTitle(title);
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setNegativeButton(txtNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogCallback.onNoClicked();
            }
        });
        alertDialog.setPositiveButton(txtPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogCallback.onOkClicked();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void shareApp(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


}

