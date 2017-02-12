package edu.wgu.hreid6.wgugo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.wgu.hreid6.wgugo.FormHelper;
import edu.wgu.hreid6.wgugo.R;
import edu.wgu.hreid6.wgugo.data.model.Assessment;

/**
 * Created by hreid on 2/6/17.
 */

public class AssessmentPhotoListAdapter extends ArrayAdapter<String> {

    private List<String> photoPaths;
    private int layout;

    public AssessmentPhotoListAdapter(Context context, int resource, List<String> photoPaths, int layout) {
        super(context, resource, photoPaths);
        this.photoPaths = photoPaths;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
        }

        String  photoPath = photoPaths.get(position);
        ImageView photo = (ImageView) convertView.findViewById(R.id.img_assessment_photo);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, null);
        photo.setImageBitmap(bitmap);

        Button button = (Button) convertView.findViewById(R.id.btn_assessment_photo_delete);
        if (button != null) {
            button.setTag(photoPath);
        }
        return convertView;
    }

//    private void setPic(ImageView v, String path) {
//        // Get the dimensions of the View
//        int targetW = v.getWidth();
//        int targetH = v.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
//        v.setImageBitmap(bitmap);
//    }

}
