package in.fitkitapp.fitkit;

/**
 * Created by Akhilkamsala on 6/23/2016.
 */
public class Order {
    String order_id,expirydate,location,leftworkouts,price;
    public Order()
    {

    }

    public Order(String order_id,String expirydate,String location,String leftworkouts,String price)
    {
        this.order_id=order_id;
        this.expirydate=expirydate;
        this.location=location;
        this.leftworkouts=leftworkouts;
        this.price=price;
    }


    String getOrder_id()
    {
        return order_id;
    }
    String getExpirydate()
    {
        return expirydate;
    }
    String getLocation()
    {
        return location;
    }
    String getLeftworkouts()
    {
        return leftworkouts;
    }
    String getPrice()
    {
        return price;
    }
}
