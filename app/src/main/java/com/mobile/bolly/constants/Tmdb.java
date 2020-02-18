package com.mobile.bolly.constants;

import com.mobile.bolly.R;

public class Tmdb {

    public static final String APIKEY = "8639997256e31f020aba3a811f0bfbb2";
    public static final String POSTER_DOMAIN_185 = "https://image.tmdb.org/t/p/w185";
    public static final String POSTER_DOMAIN_500 = "https://image.tmdb.org/t/p/w500";
    public static final String BACKDROP_URL_1280 = "https://image.tmdb.org/t/p/w1280";
    public static final String SERVER_BASE_URL = "https://bollyy.herokuapp.com/";
    public static final String TMDB_BASE_URL = "https://api.themoviedb.org/";
    public static final String UPDATE_SERVICE_BASE_URL = "https://api.npoint.io/";




    public  static final String [] genres = new String[]{
            "ACTION",
            "ADVENTURE",
            "ANIMATION",
            "COMEDY",
            "CRIME",
            "DOCUMENTARY",
            "DRAMA",
            "FAMILY",
            "FANTACY",
            "HISTORY",
            "HORROR",
            "MUSICAL",
            "MYSTERY",
            "ROMANCE",
            "SCI-FI",
            "THRILLER",
            "WAR"
    };

    public static final Integer [] genreIds = new Integer[] { 28, 12, 16, 35, 80, 99, 18, 10751, 14, 36, 27, 10402, 9648, 10749, 878, 53, 10752};
    public static final int [] genreDrawables = new int[] {R.drawable.action, R.drawable.adventure, R.drawable.animation, R.drawable.comedy, R.drawable.crime,
            R.drawable.documentary, R.drawable.drama, R.drawable.family, R.drawable.fantasy, R.drawable.history, R.drawable.horror, R.drawable.musical,
            R.drawable.mystery, R.drawable.romance, R.drawable.science_fiction, R.drawable.thriller,R.drawable.war};
}
