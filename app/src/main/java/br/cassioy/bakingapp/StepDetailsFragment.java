package br.cassioy.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;
import br.cassioy.bakingapp.model.Ingredient;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cassioimamura on 2/1/18.
 */

public class StepDetailsFragment extends Fragment {

    private SimpleExoPlayer player;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private ArrayList<Ingredient.Step> mRecipeStep = new ArrayList<>();
    private int position;
    private int totalSteps;
    private double progress;

    private Uri videoThumbUri;
    private String stepActionBarTitle;

    @BindView(R.id.step_description_details) TextView stepDetails;
    @BindView(R.id.step_video_view) SimpleExoPlayerView playerView;
    @BindView(R.id.step_progress) ProgressBar stepProgress;

    @Nullable
    @BindView(R.id.step_next) Button nextButton;

    @Nullable
    @BindView(R.id.step_previous) Button previousButton;

    @Nullable
    private CustomIdlingResource mIdlingResource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRecipeStep = bundle.getParcelableArrayList("step");
            position = bundle.getInt("position");
            stepActionBarTitle = bundle.getString("recipe name");
        }

        //Set step number on ActionBar Title
        if(position > 0) {
            String title = stepActionBarTitle + " - Step " + position;
            ((RecipeMainActivity) getActivity()).getSupportActionBar().setTitle(title);
            ((RecipeMainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }else {
            String title = stepActionBarTitle + " - Introduction";
            ((RecipeMainActivity) getActivity()).getSupportActionBar().setTitle(title);
            ((RecipeMainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        ButterKnife.bind(this,view);

        //Remove numbers on recipe details
        String detailsNotFormatted = mRecipeStep.get(position).getDescription();
        String positionString = Integer.toString(position) + ".";
        String formattedDetails = detailsNotFormatted.replace(positionString, "");


        stepDetails.setText(formattedDetails);

        if(mRecipeStep.get(position).getVideoURL() != null) {
            videoThumbUri = Uri.parse(mRecipeStep.get(position).getVideoURL());
        }

        if(mRecipeStep.get(position).getVideoURL().isEmpty()){
            playerView.setVisibility(View.GONE);
        }

        //Set progress bar
        totalSteps = (mRecipeStep.size()) - 1;
        progress = ((position*1.0)/(totalSteps*1.0));
        progress *= 100;

        int roundedProgress = (int) Math.round(progress);
        stepProgress.setProgress(roundedProgress);

        boolean tabletSize = getResources().getBoolean(R.bool.is_tablet);

        if(!tabletSize){
            //Hiding navigation button when it's not necessary
            if(position == 0){
                previousButton.setVisibility(View.INVISIBLE);
            }else if(position == totalSteps){
                nextButton.setVisibility(View.INVISIBLE);
            }

            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passFragBundle(previousButton, position);
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passFragBundle(nextButton, position);
                }
            });

        }
    }

    private void initializePlayer() {
        Context context = getContext();

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);


        playerView.setPlayer(player);

        mediaDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "BakingApp"), (TransferListener<? super DataSource>) bandwidthMeter);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                mediaDataSourceFactory).createMediaSource(videoThumbUri);


        player.prepare(mediaSource);

        player.setPlayWhenReady(shouldAutoPlay);
        playerView.requestFocus();

    }



    private void releasePlayer() {
        if (player != null) {
            playerView.clearFocus();
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private void passFragBundle(Button button, int position){

        if(button == previousButton && position > 0){
            position -= 1;
        }

        if(button == nextButton && position < totalSteps){
            position += 1;
        }

        Bundle bundleStepButton = new Bundle();
        bundleStepButton.putParcelableArrayList("step", mRecipeStep);
        //Log.d("Checking Arraylist", "onItemClicked: " + mRecipeStep.get(position));
        bundleStepButton.putInt("position", position);
        bundleStepButton.putString("recipe name", stepActionBarTitle);


        StepDetailsFragment stepDetailsFragment2 = new StepDetailsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        stepDetailsFragment2.setArguments(bundleStepButton);

        transaction.setCustomAnimations(R.animator.trans_left_in, R.animator.trans_left_out);
        transaction.replace(R.id.recipe_main_fragment, stepDetailsFragment2);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }


    }

    /**
     * Only called from test, creates and returns a new {@link CustomIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new CustomIdlingResource();
        }
        return mIdlingResource;
    }
}