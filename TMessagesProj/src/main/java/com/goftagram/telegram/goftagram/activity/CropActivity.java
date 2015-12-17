package com.goftagram.telegram.goftagram.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.goftagram.telegram.goftagram.myconst.Keys;
import com.goftagram.telegram.goftagram.util.Alerts;
import com.goftagram.telegram.goftagram.util.FileUtils;
import com.goftagram.telegram.goftagram.util.ImageUtils;
import com.goftagram.telegram.goftagram.util.Utils;
import com.goftagram.telegram.goftagram.view.LoadingLayout;
import com.goftagram.telegram.messenger.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;



/**
 * Created by mhossein on 10/10/15.
 */
public class CropActivity extends Activity {
    //================================================================================
    // Props
    //================================================================================

    // TAG
    private final String TAG = "CropActivity";

    // Views
    private CropImageView cropImageView;
    private FrameLayout doneButton;
    private FrameLayout cancelButton;

    // Bitmap
    private Bitmap croppedImage;

    // Files & URIs
    private File outFile;
    Uri imgUri;

    private int defaultWidth;
    private int defaultHeight;

    private boolean sourceIsCamera = false;

    private  boolean mFixedAspectRatio;

    LoadingLayout loadingLayout;

    //================================================================================
    // Methods
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_crop);

        loadingLayout =(LoadingLayout)findViewById(R.id.crop_loading);
        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        cropImageView.setGuidelines(1);

        defaultWidth = getIntent().getIntExtra(Keys.ASPECT_RATIO_X, 1);
        defaultHeight = getIntent().getIntExtra(Keys.ASPECT_RATIO_Y, 1);

        mFixedAspectRatio = getIntent().getBooleanExtra(Keys.ASPECT_RATIO, false);
        cropImageView.setFixedAspectRatio(mFixedAspectRatio);


        sourceIsCamera = getIntent().getBooleanExtra(Keys.SOURCE_CAMERA, false);

        // Set file and uri
        outFile = Utils.GetOutputMediaFile();


        // Show the image
        imgUri = getIntent().getData();

        loadBitmap(imgUri, cropImageView);

        // Set buttons actions
        doneButton = (FrameLayout) findViewById(R.id.btn_done);
        cancelButton = (FrameLayout) findViewById(R.id.btn_cancel);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Checking Loaded Image
                if(loadingLayout.getVisibility() == View.GONE){
                    // Save the cropped image
                    croppedImage = cropImageView.getCroppedImage();
                    SaveCroppedBitmapToFile(outFile);

                    // Result OK'd and finish
                    Intent resIntent = new Intent();
                    setResult(RESULT_OK, resIntent);
                    if(outFile != null){
                        resIntent.putExtra(Keys.IMG_URI, outFile.getAbsolutePath());
                    }
                    finish();
                }else{
                    Alerts.showErrorSnackbar(R.string.error_loading_image_not_terminated, CropActivity.this);
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Result OK'd and finish
                Intent resIntent = new Intent();
                setResult(RESULT_CANCELED, resIntent);
                finish();
            }
        });

    }


    public void loadBitmap(Uri imgUri, CropImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(this, imageView);
        task.execute(imgUri);

    }


    @Override
    public void onStart(){
        super.onStart();
    }



    /**
     * Save the cropped bitmap to file
     */
    private void SaveCroppedBitmapToFile(File outFile) {

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(outFile);
            croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
        private final WeakReference<CropImageView> imageViewReference;
        private Uri data = null;
        ExifInterface exifInterface;
        Activity activity;
        String filePath;
        public BitmapWorkerTask(Activity activity, CropImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<CropImageView>(imageView);
            this.activity = activity;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Uri... params) {
            data = params[0];
            if(data != null){
                filePath = FileUtils.getPath(CropActivity.this, data);
                return ImageUtils.decodeSampledBitmapFromPath(filePath);
            }else{
                return null;
            }

        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final CropImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if(Build.VERSION.SDK_INT >= 19){
                        imageView.setImageBitmap(bitmap);
                    }
                    if(sourceIsCamera){
                        try {
                            if(Build.VERSION.SDK_INT >= 19){
                                ImageUtils.autoCorrectAngleFromCamera(activity, imageView, data);
                            }else{
                                bitmap = ImageUtils.autoCorrectAngleFromCamera(activity, imageView, data);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        bitmap = ImageUtils.autoCorrectAngleFromGallery(activity, data, bitmap);
                    }

                    if(Build.VERSION.SDK_INT < 19){
                        imageView.setImageBitmap(bitmap);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mFixedAspectRatio){
                                cropImageView.setAspectRatio(defaultWidth, defaultHeight);
                            }
                            loadingLayout.setVisibility(View.GONE);

                        }
                    }, 100);
                }
            }
        }
    }
}
