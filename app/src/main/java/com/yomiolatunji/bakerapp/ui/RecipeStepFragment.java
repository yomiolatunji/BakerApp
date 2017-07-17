package com.yomiolatunji.bakerapp.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.data.entities.RecipeStep;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepActivity}
 * on handsets.
 */
public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_STEP_POS = "step_pos";
    public static final String ARG_RECIPE = "recipe";
    private static final String TAG = "RecipeStepFragment";
    private static MediaSessionCompat mMediaSession;
    Recipe recipe;
    int currentPosition;
    RecipeStep currentStep;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private ImageView stepImage;
    private TextView recipeStep;
    private Button next;
    private Button previous;
    private ChangeStepListener mChangeStepListener;

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_STEP_POS)) {
            currentPosition = getArguments().getInt(ARG_STEP_POS);
            recipe = getArguments().getParcelable(ARG_RECIPE);
            currentStep = recipe.getRecipeSteps().get(currentPosition);
            Activity activity = this.getActivity();
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.detail_toolbar);
            if (toolbar != null) {
                toolbar.setTitle(currentStep.getShortDescription());
            }


        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChangeStepListener)
            mChangeStepListener = (ChangeStepListener) context;
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        //mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "BakerApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);
        stepImage = (ImageView) rootView.findViewById(R.id.stepImage);
        recipeStep = (TextView) rootView.findViewById(R.id.recipeStep);
        next = (Button) rootView.findViewById(R.id.next);
        previous = (Button) rootView.findViewById(R.id.previous);

        if (TextUtils.isEmpty(currentStep.getVideoUrl())) {
            mPlayerView.setVisibility(View.GONE);
        } else {
            mPlayerView.setVisibility(View.VISIBLE);
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.ic_play_arrow));
            initializeMediaSession();
            initializePlayer(Uri.parse(currentStep.getVideoUrl()));

        }
        if (TextUtils.isEmpty(currentStep.getVideoUrl()) && !TextUtils.isEmpty(currentStep.getThumbnailUrl())) {
            stepImage.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(currentStep.getThumbnailUrl()).into(stepImage);
        } else {
            stepImage.setVisibility(View.GONE);

        }
        if (recipeStep != null)
            recipeStep.setText(currentStep.getDescription());
        if (next != null)
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChangeStepListener.onNext(currentPosition);
                }
            });
        if (previous != null)
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChangeStepListener.onPrevious(currentPosition);
                }
            });
        return rootView;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onStop() {
        super.onStop();

        releasePlayer();
        mMediaSession.setActive(false);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    interface ChangeStepListener {
        void onNext(int currentPos);

        void onPrevious(int currentPos);
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}