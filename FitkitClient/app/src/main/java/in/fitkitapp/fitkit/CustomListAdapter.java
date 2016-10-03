package in.fitkitapp.fitkit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import static in.fitkitapp.fitkit.R.drawable.explore;

/**
 * Created by Akhilkamsala on 5/28/2016.
 */
public class CustomListAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<FitnessCenter> fitnessCenters;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public CustomListAdapter(Activity activity,List<FitnessCenter> fitnessCenters)
    {
        this.activity=activity;
        this.fitnessCenters=fitnessCenters;

    }




    @Override
    public int getCount() {
        return fitnessCenters.size();
    }

    @Override
    public Object getItem(int position) {
        return fitnessCenters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbnail= (NetworkImageView) convertView.findViewById(R.id.image);
        TextView title= (TextView) convertView.findViewById(R.id.name);
        TextView location= (TextView) convertView.findViewById(R.id.location_city);
        TextView price= (TextView) convertView.findViewById(R.id.price);
        TextView distance= (TextView) convertView.findViewById(R.id.distance);
        TextView rating= (TextView) convertView.findViewById(R.id.rating);
        CardView cardView= (CardView) convertView.findViewById(R.id.cardview);


        FitnessCenter f=fitnessCenters.get(position);

        thumbnail.setImageUrl(f.getUrl(),imageLoader);
        title.setText(f.getName());
        location.setText(f.getArea());
        price.setText(f.getPrice());
        distance.setText(f.getDistance()+"\tkm");
        rating.setText(f.getRating());
        return convertView;
    }
}
