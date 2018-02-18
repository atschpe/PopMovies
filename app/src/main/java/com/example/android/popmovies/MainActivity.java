package com.example.android.popmovies;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.android.popmovies.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MovieAdapter movieAdapter;
    private ActivityMainBinding mainBinding;
    private ArrayList<Movie> movieArray;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        movieArray = new ArrayList<Movie>();
        movieArray.add(new Movie("/q0R4crx2SehcEEQEkYObktdeFy.jpg", 211672));
        movieArray.add(new Movie("/qey0tdcOp9kCDdEZuJ87yE3crSe.jpg", 254128));
        movieArray.add(new Movie("/tWqifoYuwLETmmasnGHO7xBjEtt.jpg", 321612));
        movieArray.add(new Movie("/47pLZ1gr63WaciDfHCpmoiXJlVr.jpg", 460793));
        movieArray.add(new Movie("/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg", 335984));
        movieArray.add(new Movie("/xOfdQHNF9TlrdujyAjiKfUhxSXy.jpg", 335777));

        movieAdapter = new MovieAdapter(this, movieArray);
        mainBinding.gv.setAdapter(movieAdapter);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
