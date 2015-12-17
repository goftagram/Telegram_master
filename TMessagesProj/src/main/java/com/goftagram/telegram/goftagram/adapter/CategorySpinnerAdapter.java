package com.goftagram.telegram.goftagram.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.application.model.Category;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.CursorUtil;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhossein on 10/11/15.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private final String LOG_TAG = LogUtils.makeLogTag(CategorySpinnerAdapter.class.getSimpleName());

    List<Category> list = new ArrayList<>();
     
    private Context context;

    public CategorySpinnerAdapter(Context context, int txtViewResourceId, Category[] categories) {
        super(context, txtViewResourceId, categories);

        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getMyView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return getMyView(position, convertView, parent);
    }


    private View getMyView(int position, View convertView, ViewGroup parent){
        CSViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_spinner_row, parent, false);
            viewHolder = new CSViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (CSViewHolder)convertView.getTag();
        }



        viewHolder.fill(list.get(position));

        return convertView;
    }

    /**
     * Load channel list
     * @param cursor
     */
    public void setList(Cursor cursor){
        list.clear();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            // The Cursor is now set to the right position
            Category category = new Category();

            category.setTitle(CursorUtil.getStringColumnFromCursor(cursor, GoftagramContract.CategoryEntry.COLUMN_TITLE));
            category.setServerId(CursorUtil.getStringColumnFromCursor(cursor, GoftagramContract.CategoryEntry.COLUMN_SERVER_ID));
            category.setThumbnail(CursorUtil.getStringColumnFromCursor(cursor, GoftagramContract.CategoryEntry.COLUMN_THUMBNAIL));
            list.add(category);
        }
    }




    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Category getItem(int position) {
        return list.get(position);
    }

    class CSViewHolder{
        private TextView titleView;
        private ImageView thumbnailView;
        private View container;

        public CSViewHolder(View itemView){

            titleView = (TextView)itemView.findViewById(R.id.category_spinner_row_title);
            thumbnailView = (ImageView)itemView.findViewById(R.id.category_spinner_row_image);
            container = itemView.findViewById(R.id.category_spinner_row_container);

        }

        public void fill(Category category){
            titleView.setText(category.getTitle());

            if(!TextUtils.isEmpty(category.getThumbnail())){
                Glide.with(context)
                        .load(category.getThumbnail())
                        .placeholder(R.drawable.placeholder)
                        .into(thumbnailView);
            }

        }
    }
}
