package com.example.poster1019;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Displays the Movie Spotlight page with a movie image
 * and a description of the featured film.
 */
public class SpotlightFragment extends Fragment {

    /**
     * Creates the fragment using the spotlight XML layout.
     */
    public SpotlightFragment() {
        super(R.layout.fragment_spotlight);
    }

    /**
     * Called when the fragment's view has been created.
     * The spotlight content is defined in its XML layout.
     */
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
