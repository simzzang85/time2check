package com.simstudious.time2check;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-5527371243503810/3052770999";

    private Button refresh;
    private CheckBox startVideoAdsMuted;
    private TextView videoStatus;
    private RadioGroup rg1;
    private RadioButton rb1, rb2, rb3;
    private UnifiedNativeAd nativeAd;
    private Context mContext;
    private String strMethod;
    private String strRtn;
    private String Url= "";
    private Boolean rtnBool;
    private TimeZone tz = TimeZone.getDefault();

    private ArrayList<HashMap<String, String>> arryList;
    private ArrayList<String> arryList2;
    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";

    //Thread controlling
    private Handler handler = new Handler();
    private Runnable runnable;

    //dialog
    String[] val = {};
    ListView list;
    Dialog listDialog;

    Time2CheckFavorite tc = new Time2CheckFavorite(this);
    JSONParser req = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        loadAds(); // Google ads
        final TextView timeZone = findViewById(R.id.txtTimezone);
        final TextView textView = findViewById(R.id.txtDisplay);
        final EditText editText = findViewById(R.id.editText);
        final RadioGroup rg1 = findViewById(R.id.rdoGrp1);
        timeZone.setText(tz.getDisplayName());
        mContext = getApplicationContext();
//Request server reponse
        Button btnReq = findViewById(R.id.btnReq);
        btnReq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int id = rg1.getCheckedRadioButtonId();
                RadioButton rb = rg1.findViewById(id);
                strMethod = rb.getText().toString();
                //Log.d("METHODDDDD:::", strMethod);
                Url = (strMethod.equals("Else")) ? editText.getText().toString() : strMethod + editText.getText().toString();
                // Toast.makeText(getApplicationContext(), strMethod, Toast.LENGTH_LONG).show();
                // Initialize a new RequestQueue instance
                strRtn = req.UrlHeaderResponse(Url);
                //Toast.makeText(getApplicationContext(),convertTimezone(req.UrlHeaderResponse(Url)),Toast.LENGTH_LONG);
                try{
                    if (strRtn.contains("MalformedURL")){
                        textView.setText("Please, input correct protocol(FTP, or else)");
                    }else{
                        CheckBox chkBox = findViewById(R.id.chkFavorite);
                        if(chkBox.isChecked()){
                            boolean aa = tc.insertServerUrl (editText.getText().toString(), editText.getText().toString());
                            if(aa == false){
                                Log.d("Insert result::::::::","FAILED TO INSERT");
                            }
                        }
                        //textView.setText(req.UrlHeaderResponse(Url));
                        String rtnTime = convertTimezone(req.UrlHeaderResponse(Url));
                        textView.setText(rtnTime);
                        serverThread(rtnTime);
                    }
                }catch (Exception e){
                    textView.setText("Please, input correct protocol(FTP, or else)");
                }
            }
        });

        Button btnDonate = findViewById(R.id.btnDonate);
        btnDonate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //FrameLayout fl = findViewById(R.id.fl_adplaceholder);
                refreshAd();
            }
        });

        Button btnFavorite = findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog();
            }
        });

    }

    /**
     * ==================== Dialog listview
     */
    private void showdialog()
    {
        populateList();
        listDialog = new Dialog( MainActivity.this);
        listDialog.setTitle("Select Item");
        LayoutInflater li = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialog_listview, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
        listDialog.show();
        //there are a lot of settings, for dialog, check them all out!
        final ListView list1 = listDialog.findViewById(R.id.lvFavorite);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                //listDialog.dismiss();
                View parentView = v;
                final Button btnDel = parentView.findViewById(R.id.btnDel);
                final Button btnPick = parentView.findViewById(R.id.btnPick);
                final EditText editText = findViewById(R.id.editText);
                final String textview1 = ((TextView) parentView.findViewById(R.id.tvRowId)).getText().toString();
                final String textview2 = ((TextView) parentView.findViewById(R.id.tvUrl)).getText().toString();
                //final String textview3 = ((TextView) parentView.findViewById(R.id.btnDel)).getText().toString();
                //Delete controlling
                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.d("aaaaaaaaaaaDEL", textview1);
                        tc.deleteServerUrl(textview1);
                        listDialog.dismiss();
                        String rtnStr = (tc.deleteServerUrl(textview1)<0)?"Failed to Delete":"Suceeded to Delete";
                        Toast.makeText(MainActivity.this, rtnStr , Toast.LENGTH_SHORT).show();
                    }
                });
                btnPick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.d("aaaaaaaaaaaPICK", textview1);
                        editText.setText(textview2);
                        listDialog.dismiss();
                        String rtnStr = "You've picked " + textview2;
                        Toast.makeText(MainActivity.this, rtnStr , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //list1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arryList));
        list1.setAdapter(new UrlAdapter(MainActivity.this, arryList));
        //now that the dialog is set up, it's time to show it
    }

    private void populateList() {
        // TODO Auto-generated method stub
        arryList = new ArrayList<HashMap<String,String>>();
        arryList = tc.selectAll();
    }
    /**
     * Timezone conversion==============================
     */
    private String convertTimezone(String serverTime){
        //'Wed, 19 Feb 2020 04:44:53 GMT' E, dd MMM yyyy HH:mm:ss z
        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss z");
        String rtnTime = "";
        Date inputDate = null;
        try{
            inputDate = df.parse(serverTime);
            rtnTime = df.format(inputDate);
            ZoneId zoneId = ZoneId.of(tz.getID());
            LocalDateTime ldt = LocalDateTime.parse(rtnTime,  formatter);
            ZonedDateTime zonedDateTime = ldt.atZone(zoneId);
            rtnTime = zonedDateTime.format( formatter);
        }catch (Exception e){
            rtnTime = e.toString();
        }
        return rtnTime;
    }

    private void serverThread(String iputTime){
        final TextView tv = findViewById(R.id.txtDisplay);
        handler.removeCallbacks(runnable);
        tv.setText("");
        blink(iputTime);
    }
    /**
     * Theard for time running
     * @param inputTime
     */

    private void blink(String inputTime){
        final TextView tv = findViewById(R.id.txtDisplay);
        //Log.d("inputTime::::::::::::::", inputTime.toString());
        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss z");
        String rtnTime = "";
        Date inputDate = null;
        try{
            inputDate = df.parse(inputTime);
            inputDate.setTime(inputDate.getTime()+1000);
            rtnTime = df.format(inputDate);
            ZoneId zoneId = ZoneId.of(tz.getID());
            LocalDateTime ldt = LocalDateTime.parse(rtnTime,  formatter);
            ZonedDateTime zonedDateTime = ldt.atZone(zoneId);
            rtnTime = zonedDateTime.format( formatter);
            tv.setText(rtnTime);
        }catch (Exception e){
            rtnTime = e.toString();
        }
        try{
            final String input = rtnTime;
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public  void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            blink(input);
                        }
                    });
                }
            },1000);
        }
        catch (Exception e){
            Log.d("blink:::", e.toString());
        }
    }

    /**
     * Confirmation function
     */
    private boolean confirmYN(Context _context){
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Stuff to do
                rtnBool = true;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Stuff to do
                rtnBool = false;
            }
        });

        builder.setMessage("Do you want to save to favorite list?");
        builder.setTitle("SAVE URL");

        AlertDialog d = builder.create();
        d.show();
        return rtnBool;
    }



    /**============================================================================================
     * ==================================  below ADS function  ====================================
     ============================================================================================*/
    private void loadAds(){
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        refresh = findViewById(R.id.btn_refresh);
        startVideoAdsMuted = findViewById(R.id.cb_start_muted);
        videoStatus = findViewById(R.id.tv_video_status);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                refreshAd();
            }
        });
        refreshAd();
    }
    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            videoStatus.setText("Video status: Ad does not contain a video asset.");
            refresh.setEnabled(true);
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private void refreshAd() {
        refresh.setEnabled(false);

        AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout =
                        findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(startVideoAdsMuted.isChecked())
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                refresh.setEnabled(true);
                Toast.makeText(MainActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

        videoStatus.setText("");
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }


}
