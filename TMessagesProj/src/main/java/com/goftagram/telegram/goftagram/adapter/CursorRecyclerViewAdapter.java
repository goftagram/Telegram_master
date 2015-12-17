package com.goftagram.telegram.goftagram.adapter;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;


public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<VH> {

    public static final String LOG_TAG = CursorRecyclerViewAdapter.class.getSimpleName();



    private Context mContext;

    protected Cursor mCursor;

    protected boolean mDataValid;

    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;
    protected int mCurrentPosition = -1;
    public CursorRecyclerViewAdapter(Context context, Cursor cursor) {

        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
//        Log.i(LOG_TAG,"getItemCount: mDataValid = " + mDataValid);
//        Log.i(LOG_TAG, "getItemCount: mCursor != null " + ( mCursor != null));
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }



    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        mCurrentPosition = position;
        onBindViewHolder(viewHolder, mCursor);

    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }

    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
//        Log.i(LOG_TAG, "swapCursor: newCursor == null " + (mCursor == null));
        if (newCursor == mCursor) {
//            Log.i(LOG_TAG, "swapCursor: newCursor == mCursor");
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }



    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
//            Log.i(LOG_TAG, "onChanged ");
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
//            Log.i(LOG_TAG, "onInvalidated ");
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}