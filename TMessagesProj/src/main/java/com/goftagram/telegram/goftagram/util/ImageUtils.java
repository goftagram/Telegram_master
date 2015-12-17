package com.goftagram.telegram.goftagram.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Hesam on 27/07/14
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    public static Bitmap autoCorrectAngleFromGallery(Activity activity, Uri imageUri, Bitmap myImg) {

        // get orientation of image from MediaStore
        Cursor cur;
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        if(Build.VERSION.SDK_INT < 11)
            cur = activity.managedQuery(imageUri, orientationColumn, null, null, null);
        else
            cur = activity.getContentResolver().query(imageUri, orientationColumn, null, null, null);

        int orientation = -1;
        if (cur != null && cur.moveToFirst())
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));

        if(orientation != -1) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(orientation);

            // rotate bitmap
            myImg = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(), matrix, true);
            // delete image if exists
//            if(deleteFile(activity, imageUri))
//                try {
//                    saveImage(activity, imageUri, rotated); // save image
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            else
//                Glogger.OldLog(TAG, "File did not deleted!");
        }

        return myImg;
    }

    public static Bitmap autoCorrectAngleFromCamera(Activity activity, CropImageView cropImageView, Uri imageUri) throws IOException {
        int orientation;

        if (imageUri == null)
            return null;

        // decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        // Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE = 70;
        int width_tmp = o.outWidth;
        int height_tmp = o.outHeight;
        int scale = 0;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale++;
        }
        // decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(FileUtils.getPath(activity, imageUri), o2);
        Bitmap bitmap = bm;

        ExifInterface exif = new ExifInterface(FileUtils.getPath(activity, imageUri));
        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        Matrix matrix = new Matrix();
        matrix.reset();

        if(orientation == 0)
            return bitmap;

        if(Build.VERSION.SDK_INT >= 19){
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                cropImageView.rotateImage(180);
//            matrix.postRotate(180);
//            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                cropImageView.rotateImage(90);
//            matrix.postRotate(90);
//            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                cropImageView.rotateImage(270);
//            matrix.postRotate(270);
//            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else {
                cropImageView.rotateImage(orientation);
//            matrix.postRotate(orientation);
//            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }
        }else{
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
//                cropImageView.rotateImage(180);
            matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//                cropImageView.rotateImage(90);
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//                cropImageView.rotateImage(270);
            matrix.postRotate(270);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else {
//                cropImageView.rotateImage(orientation);
            matrix.postRotate(orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }

        }




//
//        // delete image if exists
//        if(deleteFile(activity, imageUri))
//            saveImage(activity, imageUri, bitmap); // save image
//        else
//            Glogger.OldLog(TAG, "File did not deleted!");

        return bitmap;
    }

    public static BitmapDrawable LoadResourceImage(Context context, String resPath){
        AssetManager assets = context.getResources().getAssets();
        BitmapDrawable bitmapDrawable = new BitmapDrawable();

        try {
            InputStream buffer = new BufferedInputStream((assets.open(resPath)));
            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmapDrawable;
    }

    public static Bitmap decodeSampledBitmapFromPath(String imgPath) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        BitmapFactory.decodeFile(imgPath, options);

        int width, height;

        if(options.outWidth <= options.outHeight){
            width = 1080;
            height = 1920;
        }else{
            width = 1920;
            height = 1080;
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options); // TODO: Gets outOfMemory exception: http://crashes.to/s/dca5d8ac575
    }

    public static Bitmap decodeSampledBitmapFromResource(Context context, int res) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        BitmapFactory.decodeResource(context.getResources(), res, options);

        int width, height;

        if(options.outWidth <= options.outHeight){
            width = 1080;
            height = 1920;
        }else{
            width = 1920;
            height = 1080;
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), res, options); // TODO: Gets outOfMemory exception: http://crashes.to/s/dca5d8ac575
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean deleteFile(Activity activity, Uri uri) {
        File file = new File(getRealPathFromURI(activity, uri));
        return file.delete();
    }

    public static String saveImage(Activity activity, Uri uri, Bitmap bitmap) throws IOException {
//        OldLog.d(TAG, getRealPathFromURI(activity, uri));
        File file = new File(getRealPathFromURI(activity, uri));
        OutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
        fOut.flush();
        fOut.close();
        return MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(),
                file.getName(), file.getName());
    }

    public static String getRealPathFromURI(Activity activity, Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    /**
     * Open a bitmap (without OOM error)
     *
     * @param bmUri Uri of the bitmap
     * @return Bitmap object
     */
    public static Bitmap OpenBitmap(Context context, Uri bmUri) {
        // Options
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false;                     //Disable Dithering mode
        bfOptions.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
//        bfOptions.inTempStorage = new byte[32 * 1024];

        // Open file
        File file = new File(FileUtils.getPath(context, bmUri));
        FileInputStream fs = null;
        Bitmap bm = null;

        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
        }

        try {
            if (fs!=null) bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
        } catch (IOException e) {
        } finally{

            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                }
            }
        }

        return bm;

    }

    /**
     * Get the Options of the image
     * @param imgUri Image URI
     * @return Image options
     */
    public static BitmapFactory.Options GetImageOptions(Context context, Uri imgUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String imgPath = FileUtils.getPath(context, imgUri);
        BitmapFactory.decodeFile(imgPath, options);

        return options;
    }

}
