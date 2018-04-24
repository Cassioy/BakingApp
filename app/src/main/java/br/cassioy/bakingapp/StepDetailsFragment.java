package br.cassioy.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;
import br.cassioy.bakingapp.model.Ingredient;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by cassioimamura on 2/1/18.
 */

public class StepDetailsFragment extends Fragment {

    private SimpleExoPlayer player;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private BandwidthMeter bandwidthMeter;
    private boolean shouldAutoPlay;
    private long trackPosition;
    private ArrayList<Ingredient.Step> mRecipeStep = new ArrayList<>();
    private int position;
    private int totalSteps;
    private double progress;
    private boolean tabletSize;
    private int screenOrientation;
    private int resId;

    private Uri videoThumbUri;
    private String thumbnailUri;
    private String stepActionBarTitle;

    private static final String BUNDLE_STEP = "br.cassioy.bakingapp.bundle_step";
    private static final String BUNDLE_NAME = "br.cassioy.bakingapp.bundle_name";
    private static final String BUNDLE_POSITION = "br.cassioy.bakingapp.bundle_position";
    private static final String TRACK_POSITION = "Player Track Position";
    private static final String PLAYSTATE = "Playstate";

    @BindView(R.id.step_description_details) TextView stepDetails;
    @BindView(R.id.step_video_view) SimpleExoPlayerView playerView;
    @BindView(R.id.step_progress) ProgressBar stepProgress;
    @BindView(R.id.relative_layout_video) View relativeLayoutVideo;
    @BindView(R.id.layout_bottom_navigation) View linearLayoutBottom;
    @BindView(R.id.progress_bar_title) TextView progressBarTitle;
    @BindView(R.id.thumbnail_image_view) ImageView noVideo;

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


        if(savedInstanceState != null) {

            trackPosition = savedInstanceState.getLong(TRACK_POSITION);
            shouldAutoPlay = savedInstanceState.getBoolean(PLAYSTATE);
            mRecipeStep = savedInstanceState.getParcelableArrayList(BUNDLE_STEP);
            position = savedInstanceState.getInt(BUNDLE_POSITION);
            stepActionBarTitle = savedInstanceState.getString(BUNDLE_NAME);

        }else{

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mRecipeStep = bundle.getParcelableArrayList(BUNDLE_STEP);
                position = bundle.getInt(BUNDLE_POSITION);
                stepActionBarTitle = bundle.getString(BUNDLE_NAME);
            }
            //set autoplay as true
            shouldAutoPlay = true;
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

        //Setting position string to the step number from description
        String positionString = Integer.toString(mRecipeStep.get(position).getId()) + ".";
        String formattedDetails = detailsNotFormatted.replace(positionString, "");
        stepDetails.setText(formattedDetails);

        screenOrientation = getResources().getConfiguration().orientation;
        tabletSize = getResources().getBoolean(R.bool.is_tablet);

        //Check if there's a video to show
        if(!mRecipeStep.get(position).getVideoURL().isEmpty()) {
            if(!isInternetOn()){
                Toast.makeText(getContext(), getResources().getString(R.string.no_internet_alert),Toast.LENGTH_LONG);
            };

            videoThumbUri = Uri.parse(mRecipeStep.get(position).getVideoURL());

            //phone with landscape mode has different configuration
            if(screenOrientation == 2 && !tabletSize){

                stepDetails.setVisibility(View.GONE);
                linearLayoutBottom.setVisibility(View.GONE);

                View decorView = getActivity().getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = ((RecipeMainActivity) getActivity()).getSupportActionBar();
                actionBar.hide();
            }

        }else {

            //Layout customizations

            playerView.setVisibility(View.GONE);
            relativeLayoutVideo.setVisibility(View.VISIBLE);
            stepDetails.setVisibility(View.VISIBLE);
            noVideo.setVisibility(View.VISIBLE);


            //only show thumbnail on portrait mode for phones
            if(screenOrientation == 2 && !tabletSize){
                relativeLayoutVideo.setVisibility(View.GONE);
            }

            //Setting a mock thumbnail in case no thumbnail is shown
            switch (stepActionBarTitle) {
                case "Nutella Pie":
                    resId = R.drawable.nutella_pie;
                    break;

                case "Brownies":
                    resId = R.drawable.brownies;
                    break;

                case "Yellow Cake":
                    resId = R.drawable.yellowcake;
                    break;

                case "Cheesecake":
                    resId = R.drawable.cheesecake;
                    break;
            }

            thumbnailUri = mRecipeStep.get(position).getThumbnailURL();

            Picasso.get().load(thumbnailUri.trim().isEmpty()? null: thumbnailUri)
                    .error(resId)
                    .placeholder(resId)
                    .into(noVideo);
        }

        //Set progress bar
        totalSteps = (mRecipeStep.size()) - 1;
        progress = ((position*1.0)/(totalSteps*1.0));
        progress *= 100;

        int roundedProgress = (int) Math.round(progress);
        stepProgress.setProgress(roundedProgress);

        //Only for phones
        if(!tabletSize){
            //Hiding next and back buttons when it's not necessary
            if(position == 0){
                previousButton.setVisibility(View.INVISIBLE);
            }else if(position == totalSteps){
                nextButton.setVisibility(View.INVISIBLE);
            }

            if(!tabletSize) {

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
    }

    private void initializePlayer() {

        Context context = getContext();
        bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        playerView.setPlayer(player);

        mediaDataSourceFactory = new DefaultDataSourceFactory(context, Util
                .getUserAgent(context, getResources().getString(R.string.app_name)),
                (TransferListener<? super DataSource>) bandwidthMeter);

        //Check Internet
        if(!isInternetOn()){
            Toast.makeText(getContext(),getResources().getString(R.string.no_internet_alert), Toast.LENGTH_SHORT).show();
        }

        MediaSource mediaSource = new ExtractorMediaSource
                .Factory(mediaDataSourceFactory)
                .createMediaSource(videoThumbUri);

        player.prepare(mediaSource);

        //
        if(trackPosition > 0){
            player.seekTo(trackPosition);
            player.setPlayWhenReady(shouldAutoPlay);

        }else{
            player.setPlayWhenReady(shouldAutoPlay);
        }

        playerView.requestFocus();
    }

    private void releasePlayer() {
        if (player != null) {
            playerView.clearFocus();
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            trackSelector = null;
        }
    }

    //method to pass bundle to previous and next button - only phone navigation
    private void passFragBundle(Button button, int position){

        if(button == previousButton && position > 0){
            position -= 1;
        }

        if(button == nextButton && position < totalSteps){
            position += 1;
        }

        Bundle bundleStepButton = new Bundle();
        bundleStepButton.putParcelableArrayList(BUNDLE_STEP, mRecipeStep);
        bundleStepButton.putInt(BUNDLE_POSITION, position);
        bundleStepButton.putString(BUNDLE_NAME, stepActionBarTitle);


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

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(BUNDLE_STEP, mRecipeStep);
        outState.putInt(BUNDLE_POSITION, position);
        outState.putString(BUNDLE_NAME, stepActionBarTitle);

        if(player != null) {
            trackPosition = player.getCurrentPosition();
            outState.putLong(TRACK_POSITION, trackPosition);
            boolean isPlayWhenReady = player.getPlayWhenReady();
            outState.putBoolean(PLAYSTATE, isPlayWhenReady);
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