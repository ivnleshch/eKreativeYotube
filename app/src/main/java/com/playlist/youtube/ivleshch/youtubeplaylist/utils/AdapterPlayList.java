package com.playlist.youtube.ivleshch.youtubeplaylist.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.playlist.youtube.ivleshch.youtubeplaylist.R;
import com.playlist.youtube.ivleshch.youtubeplaylist.activities.PlayerActivity;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.Constants;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.VideoClip;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ivleshch on 10.03.2017.
 */

public class AdapterPlayList extends RecyclerView.Adapter<AdapterPlayList.ViewHolder> {

    static private Context context;
    static private List<VideoClip> playlist;
    private LayoutInflater inflater;

    public AdapterPlayList(Context context, String listID) {

        Realm realm;
        realm = Realm.getDefaultInstance();

        this.context = context;

        RealmResults<VideoClip> videoClips = realm.where(VideoClip.class)
                .equalTo("playlistId", listID)
                .findAll();

        List<VideoClip> listClips = realm.copyFromRealm(videoClips);


        this.playlist = listClips;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return ViewHolder.create(inflater, parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(playlist.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private ImageView image;
        private TextView title;
        private TextView description;
        private TextView duration;


        private static ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
        }

        private ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.list_item_image);
            title = (TextView) itemView.findViewById(R.id.list_item_title);
            description = (TextView) itemView.findViewById(R.id.list_item_description);
            duration = (TextView) itemView.findViewById(R.id.list_item_duration);
            itemView.setOnClickListener(this);
        }

        private void bind(VideoClip videoClip) {
            final VideoClip videoClipPicasso = videoClip;
            title.setText(videoClip.getTitle());
            description.setText(videoClip.getDescription());
            duration.setText(convertDuration(videoClip.getDuration()));
            Picasso.with(context)
                .load(videoClip.getThumbnail())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        // OK, do nothing
                    }

                    @Override
                    public void onError() {
                        // Try to get online image if cache does not exist
                        Picasso.with(context)
                                .load(videoClipPicasso.getThumbnail())
                                .error(R.drawable.ic_remove_circle_outline_black_24dp)
                                .into(image);
                    }
                });
        }


        @Override
        public void onClick(View view) {
            VideoClip videoClip = playlist.get(getAdapterPosition());
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(Constants.VIDEO_CLIP_ID_CARRIER, videoClip.getItemId());
            context.startActivity(intent);
        }
    }


    public void setPlaylist(List<VideoClip> playlist) {
        this.playlist = playlist;
    }


    @Override
    public int getItemCount() {
        return playlist.size();
    }


    /**
     * Convert insane duration format into something readable format
     * @return time
     */
    public static String convertDuration(String duration) {
        duration = duration.substring(2);  // del. PT-symbols
        String H, M, S;
        // Get Hours:
        int indOfH = duration.indexOf("H");  // position of H-symbol
        if (indOfH > -1) {  // there is H-symbol
            H = duration.substring(0, indOfH);      // take number for hours
            duration = duration.substring(indOfH); // del. hours
            duration = duration.replace("H", "");   // del. H-symbol
        } else {
            H = "";
        }
        // Get Minutes:
        int indOfM = duration.indexOf("M");  // position of M-symbol
        if (indOfM > -1) {  // there is M-symbol
            M = duration.substring(0, indOfM);      // take number for minutes
            duration = duration.substring(indOfM); // del. minutes
            duration = duration.replace("M", "");   // del. M-symbol
            // If there was H-symbol and there are less than 10 minutes
            // then add left "0" to the minutes
            if (H.length() > 0 && M.length() == 1) {
                M = "0" + M;
            }
        } else {
            // If there was H-symbol then set "00" for the minutes
            // otherwise set "0"
            if (H.length() > 0) {
                M = "00";
            } else {
                M = "0";
            }
        }
        // Get Seconds:
        int indOfS = duration.indexOf("S");  // position of S-symbol
        if (indOfS > -1) {  // there is S-symbol
            S = duration.substring(0, indOfS);      // take number for seconds
            duration = duration.substring(indOfS); // del. seconds
            duration = duration.replace("S", "");   // del. S-symbol
            if (S.length() == 1) {
                S = "0" + S;
            }
        } else {
            S = "00";
        }
        if (H.length() > 0) {
            return H + ":" + M + ":" + S;
        } else {
            return M + ":" + S;
        }
    }
}
