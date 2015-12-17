package com.goftagram.telegram.goftagram.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goftagram.telegram.goftagram.adapter.listener.OnCommentReportButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnCommentSendButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnLinkClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnLoadMoreListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnRatingSendButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnRelatedChannelClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnTagClickListener;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailCommentViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailDescriptionViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailLinkViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailRateViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailRelatedChannelViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailTagViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailUserCommentViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.ChannelDetailUserStarViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.LoadingViewHolder;
import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORK on 11/6/2015.
 */
public class TelegramChannelDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{


    private final String LOG_TAG = LogUtils.makeLogTag(TelegramChannelDetailAdapter.class.getSimpleName());



    public static final int CHANNEL_RATE            = 0;
    public static final int CHANNEL_LINK            = 1;
    public static final int CHANNEL_DESCRIPTION     = 2;
    public static final int CHANNEL_USER_STAR       = 3;
    public static final int CHANNEL_USER_COMMENT    = 4;
    public static final int CHANNEL_TAG             = 5;
    public static final int CHANNEL_RELATED_CHANNEL = 6;
    public static final int CHANNEL_COMMENT         = 7;
    public static final int VIEW_PROGRESSBAR        = 8;


    public static final String CHANNEL_RATE_STR             = "CHANNEL_RATE";
    public static final String CHANNEL_LINK_STR             = "CHANNEL_LINK";
    public static final String CHANNEL_DESCRIPTION_STR      = "CHANNEL_DESCRIPTION";
    public static final String CHANNEL_USER_STAR_STR        = "CHANNEL_USER_STAR";
    public static final String CHANNEL_USER_COMMENT_STR     = "CHANNEL_USER_COMMENT";
    public static final String CHANNEL_TAG_STR              = "CHANNEL_TAG";
    public static final String CHANNEL_RELATED_CHANNEL_STR  = "CHANNEL_RELATED_CHANNEL";
    public static final String CHANNEL_COMMENT_STR          = "CHANNEL_COMMENT";
    public static final String VIEW_PROGRESSBAR_STR         = "VIEW_PROGRESSBAR";



    private int mVisibleThreshold = 5;
    private int mLastVisibleItem, mTotalItemCount;
    private boolean mIsLoading;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mHasMoreItem;

    private OnTagClickListener                  mOnTagClickListenerCallback;
    private OnRelatedChannelClickListener       mOnRelatedChannelClickListenerCallback;
    private OnRatingSendButtonClickListener     mOnRatingSendButtonClickCallback;
    private OnCommentSendButtonClickListener    mOnCommentSendButtonClickCallback;
    private OnCommentReportButtonClickListener  mOnCommentReportButtonClickCallback;
    private OnLinkClickListener mOnLinkClickListener;


    private List<Tag> mTagList;
    private List<Comment> mCommentList;
    private List<TelegramChannel> mRelatedTelegramChannelList;

    private List<String> mViewType;

    private TelegramChannel mTelegramChannel;
    private ViewTypeBuilder mViewTypeBuilder;
    private Context mContext;


    public TelegramChannelDetailAdapter(
            Context context,
            TelegramChannel telegramChannel,
            RecyclerView recyclerView,
            OnTagClickListener onTagClickListener,
            OnRelatedChannelClickListener onRelatedChannelClickListener,
            OnRatingSendButtonClickListener onRatingSendButtonClickListener,
            OnCommentSendButtonClickListener onCommentSendButtonClickListener,
            OnCommentReportButtonClickListener onCommentReportButtonClickListener,
            OnLinkClickListener onLinkClickListener
    ) {

        mOnTagClickListenerCallback = onTagClickListener;
        mOnRelatedChannelClickListenerCallback = onRelatedChannelClickListener;
        mOnRatingSendButtonClickCallback = onRatingSendButtonClickListener;
        mOnCommentSendButtonClickCallback = onCommentSendButtonClickListener;
        mOnCommentReportButtonClickCallback = onCommentReportButtonClickListener;
        mOnLinkClickListener = onLinkClickListener;
        mTagList = new ArrayList<>();
        mCommentList = new ArrayList<>();
        mRelatedTelegramChannelList = new ArrayList<>();
        mViewType = new ArrayList<>();
        mViewTypeBuilder = new ViewTypeBuilder();
        mContext = context;
        mViewType = mViewTypeBuilder
                        .addRateView()
                        .addLinkView()
                        .addDescriptionView()
                        .addUserCommentView()
                        .addUserStarView()
                        .addProgressbarView()
                        .build();

        mHasMoreItem = true;

        if (telegramChannel == null) {
            throw new IllegalArgumentException("Invalid input argument");
        }
        mTelegramChannel = telegramChannel;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHasMoreItem) {
                    mTotalItemCount = linearLayoutManager.getItemCount();
                    mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mIsLoading = true;
                    }
                }

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        View view;
        switch (viewType) {
            case CHANNEL_RATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_rate, parent, false);
                viewHolder = new ChannelDetailRateViewHolder(view,mContext);
                break;
            case CHANNEL_LINK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_link, parent, false);
                viewHolder = new ChannelDetailLinkViewHolder(view);
                break;
            case CHANNEL_DESCRIPTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_description, parent, false);
                viewHolder = new ChannelDetailDescriptionViewHolder(view);
                break;
            case CHANNEL_TAG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_tag, parent, false);
                viewHolder = new ChannelDetailTagViewHolder(view,mContext);
                break;
            case CHANNEL_RELATED_CHANNEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_related_channels, parent, false);
                viewHolder = new ChannelDetailRelatedChannelViewHolder(view);
                break;
            case CHANNEL_USER_STAR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_user_star, parent, false);
                viewHolder = new ChannelDetailUserStarViewHolder(view,mContext);
                break;
            case CHANNEL_USER_COMMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_user_comment, parent, false);
                viewHolder = new ChannelDetailUserCommentViewHolder(view,mContext);
                break;
            case CHANNEL_COMMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_channel_detail_comment, parent, false);
                viewHolder = new ChannelDetailCommentViewHolder(view);
                break;
            case VIEW_PROGRESSBAR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_gridlist_loading, parent, false);
                viewHolder = new LoadingViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case CHANNEL_RATE:
                ((ChannelDetailRateViewHolder) holder).fill(mTelegramChannel);
                break;
            case CHANNEL_LINK:
                ((ChannelDetailLinkViewHolder) holder).fill(mTelegramChannel,mOnLinkClickListener);
                break;
            case CHANNEL_DESCRIPTION:
                ((ChannelDetailDescriptionViewHolder) holder).fill(mTelegramChannel);
                break;
            case CHANNEL_TAG:
                ((ChannelDetailTagViewHolder) holder).fill(mTagList,mOnTagClickListenerCallback);
                break;
            case CHANNEL_RELATED_CHANNEL:
                ((ChannelDetailRelatedChannelViewHolder) holder).fill(mRelatedTelegramChannelList,mOnRelatedChannelClickListenerCallback);
                break;
            case CHANNEL_USER_STAR:
                ((ChannelDetailUserStarViewHolder) holder).fill(mOnRatingSendButtonClickCallback);
                break;
            case CHANNEL_USER_COMMENT:
                ((ChannelDetailUserCommentViewHolder) holder).fill(mOnCommentSendButtonClickCallback);

                break;
            case CHANNEL_COMMENT:
                ((ChannelDetailCommentViewHolder) holder).fill(
                        mCommentList.get(position - mViewTypeBuilder.getFirstCommentIndex()),
                        mOnCommentReportButtonClickCallback
                );
                break;
        }



    }



    @Override
    public int getItemCount() {
        return mViewType.size();
    }


    @Override
    public int getItemViewType(int position) {
//        LogUtils.LOGI(LOG_TAG, " mViewType.get(position) => position " + position +
//                " ViewType " + mViewType.get(position));
        return convertViewTypeStrToInt(mViewType.get(position));
    }

    private int convertViewTypeStrToInt(String s) {

        int retValue = VIEW_PROGRESSBAR;
        switch (s) {

            case CHANNEL_RATE_STR:
                retValue = CHANNEL_RATE;
                break;
            case CHANNEL_LINK_STR:
                retValue = CHANNEL_LINK;
                break;
            case CHANNEL_DESCRIPTION_STR:
                retValue = CHANNEL_DESCRIPTION;
                break;
            case CHANNEL_USER_COMMENT_STR:
                retValue = CHANNEL_USER_COMMENT;
                break;
            case CHANNEL_USER_STAR_STR:
                retValue = CHANNEL_USER_STAR;
                break;
            case CHANNEL_TAG_STR:
                retValue = CHANNEL_TAG;
                break;
            case CHANNEL_RELATED_CHANNEL_STR:
                retValue = CHANNEL_RELATED_CHANNEL;
                break;
            case CHANNEL_COMMENT_STR:
                retValue = CHANNEL_COMMENT;
                break;
            case VIEW_PROGRESSBAR_STR:
                retValue = VIEW_PROGRESSBAR;
                break;
        }
        return retValue;
    }


    public boolean isLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public boolean hasMoreItem() {
        return mHasMoreItem;
    }

    public void setHasMoreItem(boolean hasMoreItem) {
        if (mHasMoreItem == true && mHasMoreItem != hasMoreItem) {
            mViewType = mViewTypeBuilder.removeProgressbarView().build();
            notifyItemRemoved(mViewType.size() - 1);
        }
        this.mHasMoreItem = hasMoreItem;

    }


    public void setMetaData(List<Comment> localCommentList, List<Comment> downloadedCommentList, List<TelegramChannel> relatedTelegramChannelList,
                            List<Tag> tagList) {


        boolean hasInsertedNewItems = false;
        boolean addMethodRetValue = false;

//        dumpViewType("BEFORE setMetaData");

//        dumpViewType("BEFORE TAG ");

        addMethodRetValue = addTagList(tagList);
        hasInsertedNewItems = hasInsertedNewItems || addMethodRetValue;

//        dumpViewType("AFTER TAG ");

        addMethodRetValue = addRelatedTelegramChannelList(relatedTelegramChannelList);
        hasInsertedNewItems = hasInsertedNewItems || addMethodRetValue;

//        dumpViewType("AFTER Related TelegramChannel ");

        addMethodRetValue = addLocalCommentList(localCommentList);
        hasInsertedNewItems = hasInsertedNewItems || addMethodRetValue;

//        dumpViewType("AFTER Local Comment");

        addMethodRetValue = addDownloadedCommentList(downloadedCommentList);
        hasInsertedNewItems = hasInsertedNewItems || addMethodRetValue;



        mViewType = mViewTypeBuilder.build();



        if (hasInsertedNewItems) {
            notifyDataSetChanged();
        }

//        dumpViewType("BEFORE setMetaData is finishing");

    }


    private boolean addTagList(List<Tag> tagList) {
        boolean hasInsertedNewItems = false;
        if ( tagList != null && !tagList.isEmpty()) {
            mTagList.clear();
            mTagList.addAll(tagList);
            mViewTypeBuilder.addTagView();
            hasInsertedNewItems = true;
        }
        return hasInsertedNewItems;
    }


    private boolean addRelatedTelegramChannelList(List<TelegramChannel> relatedTelegramChannelList){
        boolean hasInsertedNewItems = false;
        if (relatedTelegramChannelList != null && !relatedTelegramChannelList.isEmpty()) {
            mRelatedTelegramChannelList = relatedTelegramChannelList;
            mViewTypeBuilder.addRelatedChannelView();
            hasInsertedNewItems = true;
        }
        return hasInsertedNewItems;
    }

    private boolean addLocalCommentList(List<Comment> localCommentList) {
        boolean hasInsertedNewItems = false;
        if (localCommentList != null && !localCommentList.isEmpty()) {
            mCommentList.clear();
            mCommentList.addAll(localCommentList);
            mViewTypeBuilder.addCommentView(mCommentList.size());
            hasInsertedNewItems = true;
        }

        return hasInsertedNewItems;
    }

    private boolean addDownloadedCommentList(List<Comment> downloadedCommentList) {
        boolean hasInsertedNewItems = false;
        if (downloadedCommentList != null && !downloadedCommentList.isEmpty()) {
            mCommentList.addAll(downloadedCommentList);
            mViewTypeBuilder.addCommentView(mCommentList.size());
            hasInsertedNewItems = true;
        }
        return hasInsertedNewItems;
    }




    private class ViewTypeBuilder {

         int mNumOfComment              = 0;
         boolean hasRateView            = false;
         boolean hasLinkView            = false;
         boolean hasDescriptionView     = false;
         boolean hasUserStarView        = false;
         boolean hasUserCommentView     = false;
         boolean hasRelatedChannelView  = false;
         boolean hasTagView             = false;
         boolean hasCommentView         = false;
         boolean hasProgressbarView     = false;

        ViewTypeBuilder addRateView() {
            hasRateView = true;
            return this;
        }


        ViewTypeBuilder addLinkView() {
            hasLinkView = true;
            return this;
        }

        ViewTypeBuilder addDescriptionView() {
            hasDescriptionView = true;
            return this;
        }

        ViewTypeBuilder addUserCommentView() {
            hasUserCommentView = true;
            return this;
        }

        ViewTypeBuilder addUserStarView() {
            hasUserStarView = true;
            return this;
        }

        ViewTypeBuilder addRelatedChannelView() {
            hasRelatedChannelView = true;
            return this;
        }

        ViewTypeBuilder addTagView() {
            hasTagView = true;
            return this;
        }

        ViewTypeBuilder addCommentView(int numOfComment) {
            hasCommentView = true;
            mNumOfComment = numOfComment;
            return this;
        }

        ViewTypeBuilder addProgressbarView() {
            hasProgressbarView = true;
            return this;
        }

        ViewTypeBuilder removeProgressbarView() {
            hasProgressbarView = false;
            return this;
        }

        List<String> build() {

            List<String> retValue = new ArrayList<>();

            if (hasRateView) retValue.add(CHANNEL_RATE_STR);
            if (hasLinkView) retValue.add(CHANNEL_LINK_STR);
            if (hasDescriptionView) retValue.add(CHANNEL_DESCRIPTION_STR);
            if (hasUserStarView) retValue.add(CHANNEL_USER_STAR_STR);
            if (hasUserCommentView) retValue.add(CHANNEL_USER_COMMENT_STR);
            if (hasTagView) retValue.add(CHANNEL_TAG_STR);
            if (hasRelatedChannelView) retValue.add(CHANNEL_RELATED_CHANNEL_STR);
            if (hasCommentView) {
                for (int i = 0; i < mNumOfComment; ++i) retValue.add(CHANNEL_COMMENT_STR);
            }
            if (hasProgressbarView) retValue.add(VIEW_PROGRESSBAR_STR);

            return retValue;
        }

        int getFirstCommentIndex(){
            int index = 0;

            if (hasRateView) ++index;
            if (hasLinkView)  ++index;
            if (hasDescriptionView)  ++index;
            if (hasUserStarView)  ++index;
            if (hasUserCommentView)  ++index;
            if (hasTagView)  ++index;
            if (hasRelatedChannelView)  ++index;

            return  index;

        }
    }

    public void dumpViewType(String message) {
        for (int i = 0; i < mViewType.size(); ++i) {
            LogUtils.LOGI(LOG_TAG, message + " Position = " + i + "mViewType = " + mViewType.get(i));
        }
    }
}



