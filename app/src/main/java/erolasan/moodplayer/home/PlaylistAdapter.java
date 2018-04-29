package erolasan.moodplayer.home;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import erolasan.moodplayer.R;
import kaaes.spotify.webapi.android.models.Track;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Track> mDataset;
    private static OnItemClicked listener;
    public static int selectedPosition;


    public void setmDataset(List<Track> mDataset) {
        this.mDataset = mDataset;
        selectedPosition = 0;
    }

    public static void setSelectedPosition(int selectedPosition) {
        PlaylistAdapter.selectedPosition = selectedPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView track,artist;


        public ViewHolder(View v) {
            super(v);
            track = v.findViewById(R.id.track);
            artist = v.findViewById(R.id.artist);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectedPosition = getLayoutPosition();
                    listener.itemClicked(selectedPosition);


                }
            });
        }
    }



    public PlaylistAdapter(List<Track> myDataset, OnItemClicked listener)
    {
        mDataset = myDataset;
        PlaylistAdapter.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track track = mDataset.get(position);
        holder.track.setText(track.name);
        holder.artist.setText(track.artists.get(0).name);
        if(position == selectedPosition){
            holder.track.setTextColor(Color.parseColor("#EC9461"));
            holder.itemView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.track.setTextColor(Color.parseColor("#444444"));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface OnItemClicked{
        void itemClicked(int postition);
    }
}
