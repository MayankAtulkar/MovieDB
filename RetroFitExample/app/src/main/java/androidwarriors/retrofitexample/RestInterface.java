package androidwarriors.retrofitexample;

import androidwarriors.retrofitexample.POJO.Model;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by kundan on 8/8/2015.
 */
public interface RestInterface {

    @GET("/weather?q=London,uk")
    void getWheatherReport(Callback<Model>cb);

}
