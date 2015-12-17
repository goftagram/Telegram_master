package com.goftagram.telegram.goftagram.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.goftagram.telegram.goftagram.adapter.CategorySpinnerAdapter;
import com.goftagram.telegram.goftagram.application.model.Category;
import com.goftagram.telegram.goftagram.myconst.Keys;
import com.goftagram.telegram.goftagram.network.api.IonClient;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.Dialogs;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.util.Utils;
import com.goftagram.telegram.messenger.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.List;


/**
 * Created by mhossein on 10/5/15.
 */
public class NewChannelActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri coverFileUri; // file url to store image/video

    private RoundedImageView  coverImageView;

    private Toolbar toolbar;

    private FloatingActionButton fab;

    private AppCompatSpinner categorySpinner;

    private CategorySpinnerAdapter categorySpinnerAdapter;

    private String[] fields = new String[7];

    private EditText usernameTIL;
    private EditText descriptionTIL;
    private EditText titleTIL;

    private EditText tagsTIL;

    private SwitchCompat accessSwitch;

    private String resImgPath = "";

    private static final int LOADER_ID = 1;

    private CoordinatorLayout container;

    private CircularProgressButton submitBtn;

    private LinearLayout emptyFrame;

    private int mTransactionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_channel);

        Dialogs.getInstanse().showRulesOfAddChannelDialog(this);

        container = (CoordinatorLayout)findViewById(R.id.main_content);

        coverImageView =(RoundedImageView)findViewById(R.id.backdrop);

        emptyFrame = (LinearLayout)findViewById(R.id.empty_frame);
        emptyFrame.getLayoutParams().height = Utils.GetScreenWidthDP(this) / 3;


        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton)findViewById(R.id.select_image_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartDialogSelectImage(R.string.select_cover_dlg_title);

            }
        });

        categorySpinner = (AppCompatSpinner)findViewById(R.id.category_spinner);

        categorySpinnerAdapter = new CategorySpinnerAdapter(this, R.layout.category_spinner_row, new Category[]{});

        categorySpinner.setAdapter(categorySpinnerAdapter);


        if (savedInstanceState != null) {
            if (!Utils.EmptyOrNull(savedInstanceState.getString(Keys.IMG_URI))) {
                coverFileUri = Uri.parse(savedInstanceState.getString(Keys.IMG_URI));
            }
        }

        usernameTIL     = (EditText)findViewById(R.id.activity_new_channel_username);
        descriptionTIL  = (EditText)findViewById(R.id.activity_new_channel_description);
        titleTIL        = (EditText)findViewById(R.id.activity_new_channel_title);
        tagsTIL         = (EditText)findViewById(R.id.activity_new_channel__add_tags_field);
        accessSwitch    = (SwitchCompat)findViewById(R.id.access_switch_view);

        usernameTIL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

//                Toast.makeText(NewChannelActivity.this, charSequence, Toast.LENGTH_LONG).show();

                Log.i(getClass().getName(), "charSequence = " + charSequence);

                if(charSequence.length() > 0) {
                    if(charSequence.toString().endsWith(" ")){
                        usernameTIL.setText(usernameTIL.getText().toString().subSequence(0, usernameTIL.getText().toString().length() - 1));
                        usernameTIL.setSelection(usernameTIL.getText().toString().length());
                    }

                    if(charSequence.length() < 12){
                        usernameTIL.setText("telegram.me/");
                        usernameTIL.setSelection(usernameTIL.getText().toString().length());
                    }
                }

                String url = charSequence.toString();

                int position = url.toLowerCase().indexOf("telegramme/", 12);

                if(position != -1)
                {
                    url = url.replace(url.substring(12, 12 + (position - 12) + "telegramme/".length()), "");
                    usernameTIL.setText(url);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        accessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    compoundButton.setText(R.string.public_access_switch_text);
                }else{
                    compoundButton.setText(R.string.private_access_switch_text);
                }

            }
        });

        submitBtn       = (CircularProgressButton)findViewById(R.id.btnWithText);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        mTransactionId = UniqueIdGenerator.getInstance().getNewId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (coverFileUri != null) {
            outState.putString(Keys.IMG_URI, coverFileUri.toString());
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Intent cropIntent;

            switch (requestCode) {
                case Keys.PICTURE_REQ_CODE:
                    Uri uri = data.getData();


                    cropIntent = new Intent(this, CropActivity.class);
                    cropIntent.setData(uri);

//                    cropIntent.putExtra(Keys.ASPECT_RATIO_X, 3);
//                    cropIntent.putExtra(Keys.ASPECT_RATIO_Y, 1);
                    cropIntent.putExtra(Keys.ASPECT_RATIO, false);
                    startActivityForResult(cropIntent, Keys.CROP_REQ_CODE);

                    break;

                case Keys.CAMERA_REQ_CODE:

                    cropIntent = new Intent(this, CropActivity.class);
                    cropIntent.setData(coverFileUri);
//                    cropIntent.putExtra(Keys.ASPECT_RATIO_X, 3);
//                    cropIntent.putExtra(Keys.ASPECT_RATIO_Y, 1);
                    cropIntent.putExtra(Keys.SOURCE_CAMERA, true);
                    cropIntent.putExtra(Keys.ASPECT_RATIO, false);
                    startActivityForResult(cropIntent, Keys.CROP_REQ_CODE);

                    break;

                case Keys.CROP_REQ_CODE:

                    resImgPath = data.getStringExtra(Keys.IMG_URI);

                    // Show the result
                    if(resImgPath != null){
                        SetImage(resImgPath);
                    }


                    break;

                default:
                    break;
            }

        }
    }

    /**
     * Set the image into image view
     * @param path Image path
     */
    private void SetImage(String path) {

        submitBtn.setProgress(0);

        FutureCallback<File> writeNewFileCallback = new FutureCallback<File>() {
            @Override
            public void onCompleted(Exception e, File file) {
                if (e == null) { // Success


                    Picasso.with(NewChannelActivity.this)
                            .invalidate(file);

                    Transformation transformation = new RoundedTransformationBuilder()
                            .scaleType(ImageView.ScaleType.FIT_XY)
                            .borderColor(Color.parseColor("#77e5e5e5"))
                            .borderWidthDp(2)
                            .cornerRadiusDp(15)
                            .oval(false)
                            .build();

                    Picasso.with(NewChannelActivity.this)
                            .load(file)
                            .fit()
                            .transform(transformation)
                            .into(coverImageView);

                }
            }
        };

        String dir;
        dir = path.replace("tmpimg.jpg", "");

        dir = dir + "cover.jpg";

        Ion.with(this)
                .load(path)
                .noCache()
                .write(new File(dir))
                .setCallback(writeNewFileCallback);
    }



    /**
     * Show picture selection dialog
     */
    private void StartDialogSelectImage(int message) {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setMessage(message);

        // Select picture from gallery
        myAlertDialog.setPositiveButton(R.string.select_avatar_dlg_gallery,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Keys.PICTURE_REQ_CODE);
                    }
                });



        // Select picture from camera
        myAlertDialog.setNegativeButton(R.string.select_avatar_dlg_camera,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        coverFileUri = Uri.fromFile(Utils.GetOutputMediaFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, coverFileUri);


                        startActivityForResult(intent, Keys.CAMERA_REQ_CODE);
                    }
                });

        myAlertDialog.show();
    }

    public void submit(View v){

        submitBtn.setIndeterminateProgressMode(true);
        submitBtn.setProgress(50);

        fields[0] = titleTIL.getText().toString();
        fields[1] = descriptionTIL.getText().toString();

        int position = categorySpinner.getSelectedItemPosition();
        fields[2] = categorySpinnerAdapter.getItem(position).getServerId();

        fields[3] = tagsTIL.getText().toString();
        fields[4] = resImgPath;
        fields[5] = "https://" +usernameTIL.getText().toString();
        fields[6] = accessSwitch.isChecked() ? "1" : "0";

        if(!TextUtils.isEmpty(resImgPath)){
            IonClient.getInstance().AddChannel(this, submitBtn, fields);
        }else{
            submitBtn.setProgress(-1);
            submitBtn.setErrorText(getString(R.string.error_selection_image));

            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    submitBtn.setProgress(0);

                }
            }, 4000);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = GoftagramContract.CategoryEntry.buildCategoryUriWithTransactionId(mTransactionId);
        return new CursorLoader(this, uri , null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() == 0)finish();
        categorySpinnerAdapter.setList(data);
        categorySpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onEventMainThread(AddChannelMessage event){


        for (String message : event.getMessages()){
            if(event.getStatus().equals(Keys.FAIL)){
//                Alerts.showSupportErrorSnackbar(container, message);

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                submitBtn.setProgress(-1);
                submitBtn.setErrorText(getString(R.string.fragment_sign_up_btn_error_text));

                sHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        submitBtn.setProgress(0);
                    }
                }, 4000);
            }else{
//                Alerts.showSupportMessageSnackbar(container, message);

                submitBtn.setProgress(100);
                submitBtn.setCompleteText(getString(R.string.success_add_channel));

                Dialogs.getInstanse().showSuccessAddChannelDialog(this, message, usernameTIL.getText().toString());


            }

        }

    }

    public static class AddChannelMessage {
        private List<String> messages;

        private String status;

        public AddChannelMessage(List<String> messages, String status){
            this.messages = messages;
            this.status = status;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void setMessage(List<String> messages) {
            this.messages = messages;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
