package com.example.riva.excitweet;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TweetActivity extends Activity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Bundle extras = getIntent().getExtras();
        int nid = extras.getInt("nid");
        notificationManager.cancel(nid);

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ExciTweet");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("ExciTweet", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private static final int TWEET_COMPOSER_REQUEST_CODE = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                // Image captured and saved to fileUri specified in the Intent

                Intent intent = new TweetComposer.Builder(this)
                        .text("#cs160excited")
                        .image(fileUri)
                        .createIntent();
                startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);

            } if (requestCode == TWEET_COMPOSER_REQUEST_CODE) {
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                SearchService service = twitterApiClient.getSearchService();

                service.tweets("#cs160excited", null, null, null, null, null, null, null, null, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        //Do something with result, which provides a Tweet inside of result.data
                        final List<Tweet> tweets = result.data.tweets;
                        List<MediaEntity> images;
                        Tweet tweet;
                        MediaEntity image;
                        String urlString = "http://static.tumblr.com/fed6e26835d3a4eb87b0865969b87339/vbwpeaq/nzomxbgij/tumblr_static_chibi-smiley-face-evigsu.jpg";
                        for (int i = 0; i < tweets.size(); i++) {
                            tweet = tweets.get(0);
                            images = tweet.entities.media;
                            if (images.size() > 0) {
                                image = images.get(0);
                                urlString = image.mediaUrl;
                                break;
                            }
                        }
                        System.out.println(urlString);

                        new GetPictureTask().execute(urlString);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        //Do something on failure
                    }
                });
            }
        }
    }

    class GetPictureTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.jump);
            }
            NotificationManagerCompat notificationManager;
            int notificationId = 2;

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getBaseContext(),
                    0,
                    new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ic_thumbsup);

            NotificationCompat.Builder nBuilder =
                    new NotificationCompat.Builder(getBaseContext())
                            .setSmallIcon(R.drawable.ic_thumbsup)
                            .setLargeIcon(icon)
                            .setContentTitle(":O LOOK!!")
                            .setContentText("Somebody else was excited!")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .extend(new NotificationCompat.WearableExtender()
                                    .setBackground(bitmap)
                                    .setHintHideIcon(true)
                                    .setContentIcon(R.drawable.ic_thumbsup)
                                    .setContentIconGravity(Gravity.START));


            Notification notification = nBuilder.build();

            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;

            notificationManager = NotificationManagerCompat.from(getBaseContext());
            notificationManager.notify(notificationId, notification);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
