package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataTaskCallback;
import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class GetChannelMetaDataTask extends AbsGetTask {

    private final String LOG_TAG = LogUtils.makeLogTag(GetChannelMetaDataTask.class.getSimpleName());

    private Context mContext;
    private GetChannelMetaDataRequest mRequestModel;
    private GetChannelMetaDataTaskCallback mGetChannelMetaDataTaskCallback;
    boolean mIsCategoryTableEmpty = false;
    protected ChannelMetaDataHolder mDataHolder;
    public GetChannelMetaDataTask(
            Context context,
            GetChannelMetaDataRequest requestModel,
            GetChannelMetaDataTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetChannelMetaDataTaskCallback = callback;
        mDataHolder = new ChannelMetaDataHolder();
    }

    @Override
    public void run() {


        Cursor allCategoryCursor = null;
        Cursor channelCursor = null;
        Cursor commentCursor = null;
        Cursor telegramChannelTagCursor = null;
        Cursor relatedTelegramChannelCursor = null;


        allCategoryCursor = mContext.getContentResolver().query(
                GoftagramContract.CategoryEntry.buildCategoryList(),
                null,
                null,
                null,
                null
        );

        boolean hasReadAllComments = false;

        if (!allCategoryCursor.moveToFirst()) {
            mIsCategoryTableEmpty = true;
            mRequestModel.mServerPageRequest = 1;
            mDataHolder.put(GetChannelMetaDataInteractor.KEY_IS_CATEGORY_EMPTY, mIsCategoryTableEmpty);
            mGetChannelMetaDataTaskCallback.onMiss(mRequestModel, mDataHolder);
        } else {

            mIsCategoryTableEmpty = false;
            Uri channelUri = GoftagramContract.TelegramChannelEntry.buildTelegramChannelDetail();

            channelCursor = mContext.getContentResolver().query(
                    channelUri,
                    null,
                    GoftagramContract.TelegramChannelEntry.COLUMN_SERVER_ID + " = ? ",
                    new String[]{mRequestModel.mTelegramChannelId},
                    null
            );

            if (channelCursor.moveToFirst()) {

                commentCursor = mContext.getContentResolver().query(
                        GoftagramContract.CommentEntry.buildCommentUri(),
                        null,
                        GoftagramContract.CommentEntry.COLUMN_TELEGRAM_CHANNEL_ID + " = ? ",
                        new String[]{mRequestModel.mTelegramChannelId},
                        null
                );

                relatedTelegramChannelCursor = mContext.getContentResolver().query(
                        GoftagramContract
                                .TelegramChannelTelegramChannelEntry
                                .buildTelegramChannelTelegramChannelUri(),
                        null,
                        GoftagramContract
                                .TelegramChannelTelegramChannelEntry
                                .COLUMN_TELEGRAM_CHANNEL_ID_1 + " = ? AND " +
                        GoftagramContract.TelegramChannelTelegramChannelEntry
                                .COLUMN_TELEGRAM_CHANNEL_ID_1 + " != " +
                                GoftagramContract.TelegramChannelTelegramChannelEntry
                                .COLUMN_TELEGRAM_CHANNEL_ID_2,
                        new String[]{mRequestModel.mTelegramChannelId},
                        null
                );

                telegramChannelTagCursor = mContext.getContentResolver().query(
                        GoftagramContract.TelegramChannelEntry.buildTelegramChannelTagUri(),
                        null,
                        GoftagramContract
                                .TelegramChannelTagEntry
                                .COLUMN_TELEGRAM_CHANNEL_SERVER_ID + " = ? ",
                        new String[]{mRequestModel.mTelegramChannelId},
                        null
                );

                TelegramChannel telegramChannel = TelegramChannel.cursorToTelegramChannel(channelCursor);

                List<TelegramChannel> relatedTelegramChannelList
                        = TelegramChannel.cursorToTelegramChannelList(relatedTelegramChannelCursor);
                List<Tag> tagList
                        = Tag.cursorToList(telegramChannelTagCursor);

                List<Comment> downloadedCommentList = new ArrayList<>();

                List<Comment> commentList
                        = Comment.cursorToList(commentCursor);

                int storedHasReadAllComment = channelCursor.getInt(
                        channelCursor.getColumnIndex(
                                GoftagramContract.TelegramChannelEntry.COLUMN_HAS_READ_ALL_COMMENTS
                        )
                );
                hasReadAllComments = (storedHasReadAllComment == 1);

                LogUtils.LOGI(LOG_TAG, "mRequestModel.mClientPageRequest: " + mRequestModel.mClientPageRequest);
                LogUtils.LOGI(LOG_TAG, "hasReadAllComments: " + hasReadAllComments);

                mDataHolder.put(GetChannelMetaDataInteractor.KEY_IS_CATEGORY_EMPTY, mIsCategoryTableEmpty);
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_RELATED_TELEGRAM_CHANNEL_LIST, relatedTelegramChannelList);
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_TAG_LIST, tagList);
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_LOCAL_COMMENT_LIST, commentList);
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_HAS_MORE_COMMENTS, !hasReadAllComments);
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_TELEGRAM_CHANNEL, telegramChannel);
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_DOWNLOADED_COMMENT_LIST, downloadedCommentList);

                if(mRequestModel.mClientPageRequest == 0){
                    mGetChannelMetaDataTaskCallback.onHit(mRequestModel, mDataHolder);
                }else{
                    if (!hasReadAllComments) {
                        mRequestModel.mServerPageRequest = mRequestModel.mClientPageRequest;
                        mDataHolder.put(GetChannelMetaDataInteractor.KEY_IS_CATEGORY_EMPTY, mIsCategoryTableEmpty);
                        mGetChannelMetaDataTaskCallback.onMiss(mRequestModel, mDataHolder);
                    } else {
                        mGetChannelMetaDataTaskCallback.onHit(mRequestModel, mDataHolder);
                    }
                }
            } else {
                mRequestModel.mServerPageRequest = 1;
                mDataHolder.put(GetChannelMetaDataInteractor.KEY_IS_CATEGORY_EMPTY, mIsCategoryTableEmpty);
                mGetChannelMetaDataTaskCallback.onMiss(mRequestModel, mDataHolder);
            }


        }

        if (allCategoryCursor != null) allCategoryCursor.close();
        if (channelCursor != null) channelCursor.close();
        if (commentCursor != null) commentCursor.close();
        if (telegramChannelTagCursor != null) telegramChannelTagCursor.close();
        if (relatedTelegramChannelCursor != null) relatedTelegramChannelCursor.close();

    }


}
