package com.nadza.anapp.UI;

import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nadza.anapp.Models.Movie;
import com.nadza.anapp.Models.MovieResults;
import com.nadza.anapp.Models.TVShow;
import com.nadza.anapp.Models.TVShowResults;
import com.nadza.anapp.Adapters.MovieAdapter;
import com.nadza.anapp.Rest.MovieDBClient;
import com.nadza.anapp.Rest.MovieDBRequests;
import com.nadza.anapp.R;
import com.nadza.anapp.Adapters.TVShowAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ImageView searchIcon;
    private ListView listview;
    MovieAdapter topMoviesAdapter;
    TVShowAdapter topShowsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise UI components
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchIcon=(ImageView)findViewById(R.id.search_icon);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        listview=(ListView) findViewById(R.id.listview);

        //toolbar search icon click behaviour depends on which tab is selected
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(v.getContext(), SearchActivity.class);
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        i.putExtra("search_type", "movies");
                        Log.d("SelectedTab", "movies");
                        break;
                    case 1:
                        i.putExtra("search_type", "tv");
                        Log.d("SelectedTab", "tv");
                        break;
                    default:
                        i.putExtra("search_type", "movies");
                        Log.d("SelectedTab", "movies");
                }
                startActivity(i);
            }
        });
        getMovieList();
        //select tab to display top 10 list
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        getMovieList();
                    case 1:
                        getShowList();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //listview item click
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, InfoActivity.class);
                if(item instanceof Movie){
                    Movie clickedMovie=(Movie)parent.getItemAtPosition(position);
                    i.putExtra("title", clickedMovie.getTitle());
                    i.putExtra("img", clickedMovie.getBackdropPath());
                    i.putExtra("overview", clickedMovie.getOverview());
                }else{
                    TVShow clickedTVShow=(TVShow)parent.getItemAtPosition(position);
                    i.putExtra("title", clickedTVShow.getName());
                    i.putExtra("img", clickedTVShow.getBackdropPath());
                    i.putExtra("overview", clickedTVShow.getOverview());
                }
                startActivity(i);
            }
        });
    }
    private void getMovieList() {
        getObservable1().subscribeWith(getObserver1());
    }
    private void getShowList() {
        getObservable2().subscribeWith(getObserver2());
    }
    //set adapter for listview with results
    public void displayMovies(MovieResults movieResults) {
        if(movieResults !=null) {
            Log.d("MainActivity", movieResults.getResults().get(1).getTitle());
            ArrayList<Movie> top10 = new ArrayList<Movie>(movieResults.getResults().subList(0,10));
            topMoviesAdapter = new MovieAdapter(MainActivity.this, top10);
            listview.setAdapter(topMoviesAdapter);
        }else{
            Log.d("MainActivity","no movies response");
        }
    }
    public void displayTVShows(TVShowResults tvshowResults) {
        if(tvshowResults!=null) {
            Log.d("MainActivity",tvshowResults.getResults().get(1).getName());
            ListView top10listview=(ListView) findViewById(R.id.listview);

            ArrayList<TVShow> top10 = new ArrayList<TVShow>(tvshowResults.getResults().subList(0,10));
            topShowsAdapter = new TVShowAdapter(MainActivity.this, top10);
            top10listview.setAdapter( topShowsAdapter);
        }else{
            Log.d("MainActivity","no tvshows response");
        }
    }
    //in case of an error
    public void displayError(String str) {
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();

    }
    //for movies
    public Observable<MovieResults> getObservable1(){
        return MovieDBClient.getRetrofit().create(MovieDBRequests.class)
                .getMovies("7cd480610e3e97fb3b2a6ab91b7fa8d1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //for tv
    public Observable<TVShowResults> getObservable2(){
        return MovieDBClient.getRetrofit().create(MovieDBRequests.class)
                .getTVShows("7cd480610e3e97fb3b2a6ab91b7fa8d1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //for movies
    public DisposableObserver<MovieResults> getObserver1(){
        return new DisposableObserver<MovieResults>() {

            @Override
            public void onNext(@NonNull MovieResults movieResponse) {
                Log.d("MainActivity","OnNext"+movieResponse.getTotalResults());
                displayMovies(movieResponse);
            }

            @Override
            public void onError(@NonNull Throwable t) {
                Log.d("MainActivity","Error"+t);
                t.printStackTrace();
                displayError("Error fetching Movie Data");
            }

            @Override
            public void onComplete() {
                Log.d("MainActivity","Completed");
            }
        };
    }
    //for tv
    public DisposableObserver<TVShowResults> getObserver2(){
        return new DisposableObserver<TVShowResults>() {

            @Override
            public void onNext(@NonNull TVShowResults tvResponse) {
                Log.d("MainActivity","OnNext"+tvResponse.getTotalResults());
                displayTVShows(tvResponse);
            }

            @Override
            public void onError(@NonNull Throwable t) {
                Log.d("MainActivity","Error"+t);
                t.printStackTrace();
                displayError("Error fetching Movie Data");
            }

            @Override
            public void onComplete() {
                Log.d("MainActivity","Completed");
            }
        };
    }

}
