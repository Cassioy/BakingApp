package br.cassioy.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import br.cassioy.bakingapp.model.Ingredient;

/**
 * Created by cassioimamura on 2/1/18.
 */

public class StepDetailsFragment extends Fragment {

    private SimpleExoPlayer player;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private SimpleExoPlayerView playerView;
    private TextView stepDetails;
    private ArrayList<Ingredient.Step> mRecipeStep = new ArrayList<>();
    private int position;
    private Uri videoThumbUri;


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
        }

        playerView = view.findViewById(R.id.step_video_view);
        stepDetails = (TextView) view.findViewById(R.id.step_description_details);
        stepDetails.setText(mRecipeStep.get(position).getDescription());
        videoThumbUri = Uri.parse(mRecipeStep.get(position).getVideoURL());


        initializePlayer();

    }

    private void initializePlayer() {

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();

        Context context = getContext();

        playerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);


        player.setPlayWhenReady(shouldAutoPlay);

        mediaDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "BakingApp"), (TransferListener<? super DataSource>) bandwidthMeter);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                mediaDataSourceFactory).createMediaSource(videoThumbUri);

        player.prepare(mediaSource);
        playerView.setPlayer(player);

    }



    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
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
}