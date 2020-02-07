package com.nilesh.bolly;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nilesh.bolly.fragments.BookmarkFragment;
import com.nilesh.bolly.fragments.HomeFragment;
import com.nilesh.bolly.fragments.InfoFragment;
import com.nilesh.bolly.fragments.UpdateFragment;
import com.nilesh.bolly.models.UpdateLog;
import com.nilesh.bolly.networking.RetrofitSingleton;
import com.nilesh.bolly.util.ConnectivityChangeReceiver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    FrameLayout flSwitch;
    BottomNavigationView bottomNav;
    private static int selectedItem = 0;
    ConnectivityChangeReceiver connectivityReceiver;

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        connectivityReceiver = new ConnectivityChangeReceiver(getSupportFragmentManager());
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        flSwitch = findViewById(R.id.fl_switch_main);
        bottomNav = findViewById(R.id.bottom_nav);

        setNavigationListener(bottomNav);
        bottomNav.setSelectedItemId(R.id.home);

        checkUpdate();

    }

    private void checkUpdate() {

        RetrofitSingleton.getUpdateService().checkUpdates().enqueue(new Callback<UpdateLog>() {
            @Override
            public void onResponse(Call<UpdateLog> call, Response<UpdateLog> response) {
                if(response.body().getLatestVersion() != null){

                    if(BuildConfig.VERSION_CODE < response.body().getLatestVersionCode() ){

                        Bundle args = new Bundle();
                        args.putString("downloadUrl", response.body().getUrl());
                        args.putString("version", response.body().getLatestVersion());

                        UpdateFragment updateFragment = new UpdateFragment();
                        updateFragment.setArguments(args);

                        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .add(android.R.id.content, updateFragment, "update")
                                .addToBackStack("stack")
                                .commit();

                    }


                }
            }

            @Override
            public void onFailure(Call<UpdateLog> call, Throwable t) {

            }
        });


    }

    private void setNavigationListener(BottomNavigationView bottomNav) {

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(selectedItem == menuItem.getItemId())
                    return true;
                selectedItem  = menuItem.getItemId();

                switch (menuItem.getItemId()){

                    case R.id.bookmarks:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, BookmarkFragment.getBookmarkFragment()).commit();
                        break;


                    case R.id.info:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, InfoFragment.getInfoFragment()).commit();
                        break;

                    default: //or r.id.home
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, HomeFragment.getHomeFragment()).commit();
                        break;

                }

                return true;
            }
        });
    }

}
