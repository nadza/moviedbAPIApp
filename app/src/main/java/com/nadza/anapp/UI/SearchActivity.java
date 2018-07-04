package com.nadza.anapp.UI;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nadza.anapp.Adapters.MovieAdapter;
import com.nadza.anapp.Adapters.TVShowAdapter;
import com.nadza.anapp.Models.Movie;
import com.nadza.anapp.Models.MovieResults;
import com.nadza.anapp.Models.TVShow;
import com.nadza.anapp.Models.TVShowResults;
import com.nadza.anapp.R;
import com.nadza.anapp.Rest.MovieDBClient;
import com.nadza.anapp.Rest.MovieDBRequests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class SearchActivity extends AppCompatActivity {

    private ListView resultsList;
    private ImageView backArrow;
    TextInputEditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent= getIntent();
        String searchType= intent.getStringExtra("search_type");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initialise the UI elements
        backArrow=(ImageView)findViewById(R.id.back_arrow);
        resultsList=(ListView)findViewById(R.id.search_result_list);
        search=(TextInputEditText)findViewById(R.id.search_bar);
        //determine the type of search first
        if(searchType.equals("movies")){
            getMovieSearchResults(search);
        }else {
            getTVSearchResults(search);
        }
        //click on listview item
        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Intent i = new Intent(SearchActivity.this, InfoActivity.class);
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
        //toolbar back arrow click behaviour
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //if search type is "movies"
   public void displayMovieResult(MovieResults movieResponse) {
       ArrayList<Movie> searchResults = new ArrayList<Movie>(movieResponse.getResults().subList(0,3));
       MovieAdapter adapter = new MovieAdapter(SearchActivity.this, searchResults);
       resultsList.setAdapter(adapter);
   }
   //in case of "tv" search type
    public void displayTVResult(TVShowResults TVResponse) {
        ArrayList<TVShow> searchResults = new ArrayList<TVShow>(TVResponse.getResults().subList(0,3));
        TVShowAdapter adapter = new TVShowAdapter(SearchActivity.this, searchResults);
        resultsList.setAdapter(adapter);
    }
    //Toast to display error msg
    public void displayError(String str) {
        Toast.makeText(SearchActivity.this,str,Toast.LENGTH_LONG).show();
    }
    //for movies
    public void getMovieSearchResults(TextInputEditText search) {
        getQuery(search)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String str) throws Exception {
                        if(str==null){
                            return false;
                        }else{
                            return true;
                        }
                    }
                })
                .debounce(1, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<MovieResults>>() {
                    public Observable<MovieResults> apply(@NonNull String str) throws Exception {
                        return MovieDBClient.getRetrofit().create(MovieDBRequests.class).getMovieSearchResults("7cd480610e3e97fb3b2a6ab91b7fa8d1",str);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver1());
    }
    //for tv
    public void getTVSearchResults(TextInputEditText search) {
        getQuery(search)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String str) throws Exception {
                        if(str==null){
                            return false;
                        }else{
                            return true;
                        }
                    }
                })
                .debounce(1, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<TVShowResults>>() {
                    public Observable<TVShowResults> apply(@NonNull String str) throws Exception {
                        return MovieDBClient.getRetrofit().create(MovieDBRequests.class).getTVSearchResults("7cd480610e3e97fb3b2a6ab91b7fa8d1",str);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver2());
    }
    //for retriving query string from input text widgeta
    private Observable<String> getQuery(final TextInputEditText search){
        final PublishSubject<String> publishSubject = PublishSubject.create();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String input= search.getText().toString();
                if(input.length()>=3) {
                    publishSubject.onNext(input);
                }
            }
        });
        return publishSubject;
    }
    //for movies
    public DisposableObserver<MovieResults> getObserver1(){
        return new DisposableObserver<MovieResults>() {
            @Override
            public void onNext(@NonNull MovieResults MovieResponse) {
                Log.d("SearchActivity","OnNext"+MovieResponse.getTotalResults());
                displayMovieResult(MovieResponse);
            }
            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("SearchActivity","Error"+throwable);
                throwable.printStackTrace();
                displayError("Error fetching data");
            }
            @Override
            public void onComplete() {
                Log.d("SearchActivity","Completed");
            }
        };
    }
    //for tv shows
    public DisposableObserver<TVShowResults> getObserver2(){
        return new DisposableObserver<TVShowResults>() {
            @Override
            public void onNext(@NonNull TVShowResults TVResponse) {
                Log.d("SearchActivity","OnNext"+TVResponse.getTotalResults());
                displayTVResult(TVResponse);
            }
            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("SearchActivity","Error"+throwable);
                throwable.printStackTrace();
                displayError("Error fetching data");
            }
            @Override
            public void onComplete() {
                Log.d("SearchActivity","Completed");
            }
        };
    }

}
