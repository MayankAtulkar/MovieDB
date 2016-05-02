package zyloon.main.com.retrofit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import zyloon.main.com.retrofit.POJO.*;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.*;

public class MainActivity extends AppCompatActivity {

    TextView originalTitle, title;

    String url = "https://api.themoviedb.org/3";
    private static final String API_URL = "https://api.themoviedb.org/3";
    private static final String API_KEY = "2f5187a4f608e83ffa7fa15f5dd82be0";
    TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originalTitle = (TextView) findViewById(R.id.originaltitle);
        title = (TextView) findViewById(R.id.title);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FetchMovie fetchMovie = retrofit.create(FetchMovie.class);

        Call<MovieList> call = fetchMovie.loadQuestions("Morgan");
        call.enqueue(this);

        BackgroundTask task = new BackgroundTask();
        task.execute();

    }


    private class BackgroundTask extends AsyncTask<Void, Void,
            Curator> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .build();
        }

        @Override
        protected Curator doInBackground(Void... params) {
            RestInterface restInterface = restAdapter.create(RestInterface.class);
            Curator curators = restInterface.getCurators(API_KEY);

            restInterface.getPojo(new Callback<POJO>() {
                @Override
                public void success(POJO pojo, Response response) {
                    originalTitle.setText("city :"+pojo.getOriginalTitle());
                    title.setText("Status :"+pojo.getTitle());
                }

                @Override
                public void failure(RetrofitError error) {

                    String merror = error.getMessage();

                }
            });

            return curators;
        }

        @Override
        protected void onPostExecute(Curator curators) {
            textView.setText(curators.title + "\n\n");
            for (Curator.Dataset dataset : curators.dataset) {
                textView.setText(textView.getText() + dataset.curator_title +
                        " - " + dataset.curator_tagline + "\n");
            }
        }

    }


}