package in.fitkitapp.fitkit;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Akhilkamsala on 6/3/2016.
 */
public class NearbyAdapter extends BaseAdapter {


        private Activity activity;
        private LayoutInflater inflater;
        private List<WorkoutClass> classes;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();


        public NearbyAdapter(Activity activity,List<WorkoutClass> classes)
        {
            this.activity=activity;
            this.classes=classes;

        }




        @Override
        public int getCount() {
            return classes.size();
        }

        @Override
        public Object getItem(int position) {
            return classes.get(position);
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
                convertView = inflater.inflate(R.layout.near_by_row, null);

            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            NetworkImageView pro= (NetworkImageView) convertView.findViewById(R.id.pro_image);
            TextView type= (TextView) convertView.findViewById(R.id.type);
            WorkoutClass w=classes.get(position);
            pro.setImageUrl(w.getUrl(),AppController.getInstance().getImageLoader());
            type.setText(w.getName());
             return convertView;
        }
}


