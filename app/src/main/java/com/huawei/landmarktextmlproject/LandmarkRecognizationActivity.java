package com.huawei.landmarktextmlproject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLCoordinate;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzerSetting;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class LandmarkRecognizationActivity extends AppCompatActivity {
    private static final String TAG = "StillFaceAnalyse";

    private TextView mTextView;
    private TextView mTextViewSummary;
    private ImageView mImageView;
    public TextView progressBarTextView;
    public ViewGroup mainLayout;
    public ProgressBar progressBarSpin;
    public ImageView expandMoreIcon;
    public ImageView expandLessIcon;

    private MLRemoteLandmarkAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_landmark);

        this.mTextView = this.findViewById(R.id.landmark_result);
        this.mImageView = this.findViewById(R.id.landmark_image);
        progressBarTextView = findViewById(R.id.progress_bar_text);
        progressBarSpin =  findViewById(R.id.spin_loading_kit);
        expandMoreIcon = findViewById(R.id.expand_more_icon);
        expandLessIcon = findViewById(R.id.expand_less_icon);
        mTextViewSummary = findViewById(R.id.landmark_summary_result);

        mainLayout = findViewById(R.id.landmark_main_layout);

        Glide.with(this).asBitmap()
                .load(MainActivity.Companion.getImageBitmap())
                .apply(bitmapTransform(new BlurTransformation(22)))
                .into((mImageView));

        this.analyzer();

        mTextViewSummary.setVisibility(View.INVISIBLE);
        View.OnClickListener expandMoreIconClick = v -> {
            System.out.println("mcmcmc image height is ->" + mImageView.getHeight());
            ObjectAnimator anim = ObjectAnimator.ofFloat(mTextView, "Y", mImageView.getHeight() + 10);
            anim.setDuration(2000);                  // Duration in milliseconds
            // anim.setInterpolator(timeInterpolator);  // E.g. Linear, Accelerate, Decelerate
            anim.start();

            ObjectAnimator animExpandMore = ObjectAnimator.ofFloat(expandMoreIcon, "Y", mImageView.getHeight());
            animExpandMore.setDuration(2000);                  // Duration in milliseconds
            // anim.setInterpolator(timeInterpolator);  // E.g. Linear, Accelerate, Decelerate
            animExpandMore.start();

            ObjectAnimator animAnimLess = ObjectAnimator.ofFloat(expandLessIcon, "Y", mImageView.getHeight());
            animAnimLess.setDuration(2000);                  // Duration in milliseconds
            // anim.setInterpolator(timeInterpolator);  // E.g. Linear, Accelerate, Decelerate
            animAnimLess.start();

            AlphaAnimation animationMoreAlpha = new AlphaAnimation(1f, 0f);
            animationMoreAlpha.setDuration(2000);
            animationMoreAlpha.setFillAfter(true);
            expandMoreIcon.startAnimation(animationMoreAlpha);

            AlphaAnimation animationLessAlpha = new AlphaAnimation(0f, 1f);
            animationLessAlpha.setDuration(2000);
            animationLessAlpha.setFillAfter(true);
            expandLessIcon.startAnimation(animationLessAlpha);
        };
        expandMoreIcon.setOnClickListener(expandMoreIconClick);

        View.OnClickListener expandLessIconClick = v -> {
            ObjectAnimator anim = ObjectAnimator.ofFloat(mTextView, "Y", mTextView.getHeight() - 100);
            anim.setDuration(2000);                  // Duration in milliseconds
            // anim.setInterpolator(timeInterpolator);  // E.g. Linear, Accelerate, Decelerate
            anim.start();

        };
        expandLessIcon.setOnClickListener(expandLessIconClick);

    }

    private void analyzer() {
        MLRemoteLandmarkAnalyzerSetting settings = new MLRemoteLandmarkAnalyzerSetting.Factory()
                .setLargestNumOfReturns(1)
                .setPatternType(MLRemoteLandmarkAnalyzerSetting.STEADY_PATTERN)
                .create();
        this.analyzer = MLAnalyzerFactory.getInstance()
                .getRemoteLandmarkAnalyzer(settings);
        // Create an MLFrame by using android.graphics.Bitmap. Recommended image size: large than 640*640.
        //  Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.landmark_image);
        if (MainActivity.Companion.getImageBitmap() != null) {
            MLFrame mlFrame = new MLFrame.Creator().setBitmap(Objects.requireNonNull(MainActivity.Companion.getImageBitmap())).create();
            Task<List<MLRemoteLandmark>> task = this.analyzer.asyncAnalyseFrame(mlFrame);
            task.addOnSuccessListener(new OnSuccessListener<List<MLRemoteLandmark>>() {
                @Override
                public void onSuccess(List<MLRemoteLandmark> landmarkResults) {
                    // Processing logic for recognition success.
                    LandmarkRecognizationActivity.this.displaySuccess(landmarkResults.get(0));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Processing logic for recognition failur
                    LandmarkRecognizationActivity.this.displayFailure();
                }
            });
        }

    }

    private void displayFailure() {
        this.mTextView.setText("Failure");
    }

    private void displaySuccess(MLRemoteLandmark landmark) {
        Glide.with(this).asBitmap()
                .load(MainActivity.Companion.getImageBitmap())
                .into((mImageView));
        progressBarSpin.setVisibility(View.INVISIBLE);
        progressBarTextView.setVisibility(View.INVISIBLE);

        if (landmark.getLandmark().contains("retCode") || landmark.getLandmark().contains("retMsg") || landmark.getLandmark().contains("fail")) {
            this.mTextView.setText("The landmark was not recognized.");
        } else {
            double longitude = 0;
            double latitude = 0;
            String possibility = "";
            String landmarkName = "";
            StringBuilder result = new StringBuilder();
            if (landmark.getLandmark() != null) {
                result = new StringBuilder("Landmark information\n" + landmark.getLandmark());
                landmarkName = landmark.getLandmark();
            }
            mTextViewSummary.setText("LANDMARK INFORMATION\n" + landmark.getLandmark());
            if (landmark.getPositionInfos() != null) {
                for (MLCoordinate coordinate : landmark.getPositionInfos()) {
                    //           setText(Html.fromHtml("<b>" + myText + "</b>");
                    result.append("\nLatitude: ").append(coordinate.getLat());
                    result.append("\nLongitude: ").append(coordinate.getLng());
                    result.append("\nPossibility: %").append(new DecimalFormat("##.##").format(landmark.getPossibility() * 100));
                    longitude = coordinate.getLng();
                    latitude = coordinate.getLat();
                    possibility = new DecimalFormat("##.##").format(landmark.getPossibility() * 100);
                }
            }
            this.mTextView.setText(Html.fromHtml("<big><b>" + "Landmark Information" + "</b></big> <br><big><b>" + landmarkName + "</b></big><br><b>Latitude: </b>" + latitude + "<br><b>Longitude: </b>" + longitude + "<br><b>Possibility: </b>%" + possibility));
            MainActivity.Companion.setRemoteLandmarkResponse(landmark);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), true);

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String _Location = listAddresses.get(0).getCountryCode();
                    System.out.println("mcmcmc locality->" + listAddresses.get(0).getCountryName());
                    TranslatorActivity.Companion.setCountryName(listAddresses.get(0).getCountryName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.analyzer == null) {
            return;
        }
        try {
            this.analyzer.close();
        } catch (IOException e) {
            Log.e(LandmarkRecognizationActivity.TAG, "Stop failed: " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.Companion.setRemoteLandmarkResponse(null);
        super.onBackPressed();
    }

}