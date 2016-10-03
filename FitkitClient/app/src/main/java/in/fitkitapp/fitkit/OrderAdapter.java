package in.fitkitapp.fitkit;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Akhilkamsala on 6/23/2016.
 */
public class OrderAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Order> orders;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public OrderAdapter(Activity activity,List<Order> orders)
    {
        this.activity=activity;
        this.orders=orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
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
            convertView = inflater.inflate(R.layout.orders, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        TextView order_id= (TextView) convertView.findViewById(R.id.order);
        TextView location= (TextView) convertView.findViewById(R.id.location);
        TextView expiry= (TextView) convertView.findViewById(R.id.expiry);
        TextView price= (TextView) convertView.findViewById(R.id.paidprice);

        Order o=orders.get(position);

        order_id.setText(o.getOrder_id());
        location.setText(o.getLocation());
        expiry.setText(o.getExpirydate());
        price.setText(o.getPrice());


        return convertView;


    }
}
