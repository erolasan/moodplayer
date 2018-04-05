package erolasan.moodplayer.quiz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import erolasan.moodplayer.R;

/**
 * Created by Erol Asan on 2/11/2018.
 */

public class ArtistAdapter extends ArrayAdapter<QuizArtist> {
    Context context;

    public ArtistAdapter(@NonNull Context context) {
        super(context, 0);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_quiz_artist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QuizArtist artist = getItem(position);

        holder.name.setText(artist.getName());
        int drawableResourceId = context.getResources().getIdentifier(artist.getName().toLowerCase().replace(" ", "_"), "drawable", context.getPackageName());
        Picasso.with(context).load(drawableResourceId).into(holder.picture);

        return convertView;
    }

    private static class ViewHolder {
        public TextView name;
        public ImageView picture;

        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.name);
            this.picture = (ImageView) view.findViewById(R.id.picture);
        }
    }
}
