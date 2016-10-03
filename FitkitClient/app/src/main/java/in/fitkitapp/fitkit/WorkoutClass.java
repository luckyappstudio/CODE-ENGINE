package in.fitkitapp.fitkit;

/**
 * Created by Akhilkamsala on 6/4/2016.
 */
public class WorkoutClass {


    String url,name;

    public WorkoutClass()
    {


    }

    public WorkoutClass(String url,String name)
    {
        this.url=url;

        this.name=name;
    }

    String getUrl()
    {
        return url;
    }

    String getName()
    {
        return name;
    }




}
