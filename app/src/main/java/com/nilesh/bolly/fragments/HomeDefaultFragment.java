package com.nilesh.bolly.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nilesh.bolly.MainActivity;
import com.nilesh.bolly.MovieGridActivity;
import com.nilesh.bolly.R;
import com.nilesh.bolly.adapter.MovieConciseAdapter;
import com.nilesh.bolly.adapter.MovieGenreAdapter;
import com.nilesh.bolly.adapter.MovieYearAdapter;
import com.nilesh.bolly.models.MovieNowPlaying;
import com.nilesh.bolly.models.MovieSearch;
import com.nilesh.bolly.models.MovieTopRated;
import com.nilesh.bolly.models.Result;
import com.nilesh.bolly.networking.RetrofitSingleton;
import com.nilesh.bolly.networking.TmdbService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nilesh.bolly.constants.Tmdb.APIKEY;

public class HomeDefaultFragment extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView rvMovieNowpalying, rvMovieToprated, rvMovieConciseYear, rvMovieConciseGenre;
    MovieConciseAdapter movieNowplayingAdapter, movieTopratedAdapter;
    MovieGenreAdapter movieGenreAdapter;
    TextView tvShowingMore, tvRatedMore;
    private String RESPONSE = "Response";




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_default, container, false);

        tvShowingMore = view.findViewById(R.id.tv_more_showing);
        tvRatedMore = view.findViewById(R.id.tv_more_rated);
        rvMovieNowpalying = view.findViewById(R.id.rv_movie_concise_nowplaying);
        rvMovieToprated = view.findViewById(R.id.rv_movie_concise_popular);
        rvMovieConciseYear = view.findViewById(R.id.rv_movie_concise_year);
        rvMovieConciseGenre = view.findViewById(R.id.rv_movie_concise_genre);
        rvMovieNowpalying.setHasFixedSize(true);
        rvMovieToprated.setHasFixedSize(true);
        rvMovieConciseYear.setAdapter(new MovieYearAdapter(getContext()));

        movieGenreAdapter = new MovieGenreAdapter(getContext());
        rvMovieConciseGenre.setHasFixedSize(true);
        rvMovieConciseGenre.setAdapter(movieGenreAdapter);

        tvShowingMore.setOnClickListener(this);
        tvRatedMore.setOnClickListener(this);


        createSerivces();



        return view;
    }

    private void createSerivces() {

        TmdbService service = RetrofitSingleton.getTmdbService();
        service.nowPlaying(APIKEY, "hi", "IN", 1).enqueue(new Callback<MovieNowPlaying>() {
            @Override
            public void onResponse(Call<MovieNowPlaying> call, Response<MovieNowPlaying> response) {

                movieNowplayingAdapter = new MovieConciseAdapter(response.body().getResults(), getActivity());
                rvMovieNowpalying.setAdapter(movieNowplayingAdapter);

            }

            @Override
            public void onFailure(Call<MovieNowPlaying> call, Throwable t) {

            }
        });


        service.topRated(APIKEY, "hi", "IN", 1).enqueue(new Callback<MovieTopRated>() {
            @Override
            public void onResponse(Call<MovieTopRated> call, Response<MovieTopRated> response) {
                List<Result> movies = new ArrayList<>();

                for(Result result : response.body().getResults()){
                    if(Integer.parseInt(result.getReleaseDate().split("-")[0])>= 2000)
                        movies.add(result);

                }

                movieTopratedAdapter = new MovieConciseAdapter(movies, getActivity());
                rvMovieToprated.setAdapter(movieTopratedAdapter);
            }

            @Override
            public void onFailure(Call<MovieTopRated> call, Throwable t) {

            }
        });


        String encodedQuery = "";

        try {
            encodedQuery = URLEncoder.encode("street dancer", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        service.searchMovie(APIKEY, encodedQuery, "hi", "IN", true, 1).enqueue(new Callback<MovieSearch>() {
            @Override
            public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {
                for(Result result : response.body().getResults()){
                    Log.d(RESPONSE, result.getTitle());
                }
            }

            @Override
            public void onFailure(Call<MovieSearch> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), MovieGridActivity.class);
        switch (view.getId()){

            case R.id.tv_more_showing:
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
