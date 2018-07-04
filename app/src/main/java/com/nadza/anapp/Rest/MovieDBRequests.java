package com.nadza.anapp.Rest;

import com.nadza.anapp.Models.MovieResults;
import com.nadza.anapp.Models.TVShow;
import com.nadza.anapp.Models.TVShowResults;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBRequests {
    @GET("discover/movie")
    Observable<MovieResults> getMovies(@Query("api_key") String api_key);

    @GET("search/movie")
    Observable<MovieResults> getMovieSearchResults(@Query("api_key") String api_key, @Query("query") String que);

    @GET("movie/{movie_id}")
    Observable<MovieResults> movieDetails(@Path("movie_id") int movieID, @Query("api_key") String apiKey);

    @GET("discover/tv")
    Observable<TVShowResults> getTVShows(@Query("api_key") String api_key);

    @GET("search/tv")
    Observable<TVShowResults> getTVSearchResults(@Query("api_key") String api_key, @Query("query") String que);

    @GET("tv/{tv_id}")
    Observable<TVShow> tvDetails(@Path("tv_id") int movieID, @Query("api_key") String apiKey);
}