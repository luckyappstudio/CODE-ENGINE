package in.fitkitapp.fitkit;

/**
 * Created by Akhilkamsala on 5/28/2016.
 */
public class FitnessCenter {

    private String name,price,area,rating,url,distance,id;



    public FitnessCenter()
    {

    }

    public FitnessCenter(String name,String area,String price,String distance,String rating,String url,String id)
    {

        this.area=area;
        this.distance=distance;
        this.name=name;
        this.price=price;
        this.rating=rating;
        this.url=url;
        this.id=id;
    }


    //getter methods


    String getName()
    {
        return name;
    }
    String getPrice()
    {
        return price;
    }
    String getArea()
    {
        return area;
    }
    String getRating()
    {
        return rating;
    }
    String getUrl()
    {
        return url;
    }
    String getDistance()
    {
        return distance;
    }



    String getId()
    {
        return id;
    }








}
