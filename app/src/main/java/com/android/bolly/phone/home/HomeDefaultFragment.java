package com.android.bolly.phone.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bolly.R;
import com.android.bolly.phone.grid.MovieGridActivity;
import com.android.bolly.models.MovieDetails;

import com.android.bolly.models.Result;
import com.android.bolly.networking.BollyService;
import com.android.bolly.networking.RetrofitSingleton;
import com.android.bolly.util.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeDefaultFragment extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView rvMovieRecent, rvMovieToprated, rvMovieConciseYear, rvMovieConciseGenre;
    MovieConciseAdapter movieNowplayingAdapter, movieTopratedAdapter;
    MovieGenreAdapter movieGenreAdapter;
    TextView tvRecentMore, tvRatedMore;
    private String RESPONSE = "Response";
    List<Result> ratedMovies = new ArrayList<>();
    List<Result> reccentMovies = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_default, container, false);

        tvRecentMore = view.findViewById(R.id.tv_more_recent);
        tvRatedMore = view.findViewById(R.id.tv_more_rated);
        rvMovieRecent = view.findViewById(R.id.rv_movie_concise_nowplaying);
        rvMovieToprated = view.findViewById(R.id.rv_movie_concise_popular);
        rvMovieConciseYear = view.findViewById(R.id.rv_movie_concise_year);
        rvMovieConciseGenre = view.findViewById(R.id.rv_movie_concise_genre);
        rvMovieRecent.setHasFixedSize(true);
        rvMovieToprated.setHasFixedSize(true);
        rvMovieConciseYear.setAdapter(new MovieYearAdapter(getContext()));

        movieGenreAdapter = new MovieGenreAdapter(getContext());
        rvMovieConciseGenre.setHasFixedSize(true);
        rvMovieConciseGenre.setAdapter(movieGenreAdapter);

        tvRecentMore.setOnClickListener(this);
        tvRatedMore.setOnClickListener(this);



        movieTopratedAdapter = new MovieConciseAdapter(ratedMovies, getActivity());
        rvMovieToprated.setAdapter(movieTopratedAdapter);

        movieNowplayingAdapter = new MovieConciseAdapter(reccentMovies, getActivity());
        rvMovieRecent.setAdapter(movieNowplayingAdapter);


        createSerivces();

        return view;
    }

    private void createSerivces() {

        SharedPrefHelper sph = new SharedPrefHelper(getContext().getApplicationContext());
        if(sph.getSearchResults(SharedPrefHelper.TYPE_RECENTS) != null){
            reccentMovies.addAll(sph.getSearchResults(SharedPrefHelper.TYPE_RECENTS));
            movieNowplayingAdapter.notifyDataSetChanged();
        }
        fetchRecentlyAdded(sph);


        if(sph.getSearchResults(SharedPrefHelper.TYPE_TOPRATED) != null
                && System.currentTimeMillis() - sph.getLastFetchTimeStamp() < 12*60*60*1000){
            ratedMovies.addAll(sph.getSearchResults(SharedPrefHelper.TYPE_TOPRATED));
            movieTopratedAdapter.notifyDataSetChanged();
        }else{
            fetchTopRated(sph);
        }
    }

    private void fetchTopRated(SharedPrefHelper sph) {

        RetrofitSingleton.getBollyService().getTopRated().enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                if(response.body() != null){
                    ratedMovies.addAll(response.body());
                    movieTopratedAdapter.notifyDataSetChanged();
                    sph.putSearchResults(SharedPrefHelper.TYPE_TOPRATED, ratedMovies);
                    sph.putFetchTimeStamp(System.currentTimeMillis());
                }
            }

            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {

            }
        });
    }

    private void fetchRecentlyAdded(SharedPrefHelper sph) {
        BollyService service = RetrofitSingleton.getBollyService();
        service.getRecents().enqueue(new Callback<List<MovieDetails>>() {
            @Override
            public void onResponse(Call<List<MovieDetails>> call, Response<List<MovieDetails>> response) {
                if(response.body() != null){
                    reccentMovies.clear();
                    for(MovieDetails details: response.body()){
                        Result movie = new Result();
                        movie.setVoteAverage(details.getVoteAverage());
                        movie.setTitle(details.getTitle());
                        movie.setReleaseDate(details.getReleaseDate());
                        movie.setOverview(details.getOverview());
                        movie.setOriginalLanguage(details.getOriginalLanguage());
                        movie.setPosterPath((String) details.getPosterPath());
                        movie.setAdult(details.getAdult());
                        movie.setBackdropPath(details.getBackdropPath());
                        movie.setId(details.getId());
                        movie.setPopularity(details.getPopularity());
                        movie.setVideo(details.getVideo());
                        movie.setVoteCount(details.getVoteCount());

                        reccentMovies.add(movie);
                    }
                    movieNowplayingAdapter.notifyDataSetChanged();
                    sph.putSearchResults(SharedPrefHelper.TYPE_RECENTS, reccentMovies);
                    sph.putFetchTimeStamp(System.currentTimeMillis());
                }
            }

            @Override
            public void onFailure(Call<List<MovieDetails>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), MovieGridActivity.class);
        switch (view.getId()){

            case R.id.tv_more_recent:
                intent.putExtra("category", 1);
                startActivity(intent);
                break;

            case R.id.tv_more_rated:
                intent.putExtra("category", 2);
                startActivity(intent);
                break;

        }


    }
}
