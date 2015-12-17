package com.goftagram.telegram.goftagram.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.activity.ChannelDetailActivity;
import com.goftagram.telegram.goftagram.activity.ChannelListActivity;
import com.goftagram.telegram.goftagram.adapter.TelegramChannelDetailAdapter;
import com.goftagram.telegram.goftagram.adapter.listener.OnChannelReportButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnCommentReportButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnCommentSendButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnLinkClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnLoadMoreListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnRatingSendButtonClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnRelatedChannelClickListener;
import com.goftagram.telegram.goftagram.adapter.listener.OnTagClickListener;
import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostPresenter;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentPresenter;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.implementation.AddCommentPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.implementation.GetChannelMetaDataPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.implementation.PostUserInterestPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.implementation.RateChannelPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.implementation.ReportChannelPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentPresenter;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.implementation.ReportCommentPresenterImp;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.Dialogs;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.goftagram.util.NetworkUtils;
import com.goftagram.telegram.messenger.R;
import com.goftagram.telegram.ui.LaunchActivity;

import java.util.List;

public class ChannelDetailFragment extends BaseFragment implements
        GetChannelMetaDataViewModel,
        OnCommentReportButtonClickListener, OnChannelReportButtonClickListener,
        OnLinkClickListener,
        OnTagClickListener, OnRelatedChannelClickListener,
        OnCommentSendButtonClickListener, OnRatingSendButtonClickListener {

    private final String LOG_TAG = LogUtils.makeLogTag(ChannelListFragment.class.getSimpleName());


    public static final String EXTRA_TELEGRAM_CHANNEL_TITLE = "Extra_Title";
    public static final String EXTRA_TELEGRAM_CHANNEL_ID = "Extra_Channel_Id";

    public static final String EXTRA_TRANSACTION_ID = "transaction_id";

    public static final int INVALID_REQUEST_ID = -1;
    public static final int PENDING_REQUEST_ID = 0;


    public static final int LOADER_ID_CHANNEL = 1;

    public static final String EXTRA_NEXT_PAGE = "Next_Page";
    public static final String EXTRA_CURRENT_PAGE = "Current_Page";
    public static final String EXTRA_HAS_MORE_ITEM = "Has_More_Items";
    public static final String EXTRA_TOTAL_SERVER_CHANNELS = "Total_Server_Channels";
    public static final String EXTRA_TOTAL_CURSOR_CHANNELS = "Total_Cursor_Channels";


    private String mFragmentTitle;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private TelegramChannelDetailAdapter mAdapter;


    private int mCurrentPage;
    private int mNextPage;
    private int mGetMetaDataRequestId;
    private int mPostRatingRequestId;
    private int mPostCommentRequestId;
    private int mPostReportChannelRequestId;
    private int mPostReportCommentRequestId;

    private String mTelegramChannelId;

    private boolean mHasMoreItem;

    private AbsGetPresenter mGetChannelMetaDataPresenter;
    private AbsPostPresenter mPostCommentPresenter;
    private AbsPostPresenter mPostRatingPresenter;
    private AbsPostPresenter mPostUserInterestPresenter;
    private AbsPostPresenter mPostReportChannelPresenter;
    private AbsPostPresenter mPostReportCommnetPresenter;


    private ImageView mTelegramChannelCoverImageView;
    private CollapsingToolbarLayout collapsingToolbar;

    private FloatingActionButton mFloatingActionButton;
    private String mAddedComment;
    private int mRate;
    private Comment mReportedComment;
    private boolean mIsChannelReported;
    private Snackbar mSnackbar;


    public static ChannelDetailFragment newInstance(
            String telegramChannelId, String telegramChannelTitle) {

        ChannelDetailFragment fragment = new ChannelDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TELEGRAM_CHANNEL_TITLE, telegramChannelTitle);
        bundle.putString(EXTRA_TELEGRAM_CHANNEL_ID, telegramChannelId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFragmentTitle = getArguments().getString(EXTRA_TELEGRAM_CHANNEL_TITLE);
        mTelegramChannelId = getArguments().getString(EXTRA_TELEGRAM_CHANNEL_ID);
        mGetMetaDataRequestId = INVALID_REQUEST_ID;
        mPostRatingRequestId = INVALID_REQUEST_ID;
        mPostCommentRequestId = INVALID_REQUEST_ID;
        mCurrentPage = 0;
        mNextPage = 0;
        mHasMoreItem = true;

        mGetChannelMetaDataPresenter = GetChannelMetaDataPresenterImp.getInstance(getActivity());
        mPostCommentPresenter = AddCommentPresenterImp.getInstance(getActivity());
        mPostRatingPresenter = RateChannelPresenterImp.getInstance(getActivity());
        mPostReportChannelPresenter = ReportChannelPresenterImp.getInstance(getActivity());
        mPostReportCommnetPresenter = ReportCommentPresenterImp.getInstance(getActivity());


        mPostUserInterestPresenter = PostUserInterestPresenterImp.getInstance(getActivity());

        NameValueDataHolder nvdh = new NameValueDataHolder();
        nvdh.put(PostUserInterestPresenterImp.KEY_TYPE, GoftagramContract.SEEN_TYPE_IN_APP);
        nvdh.put(PostUserInterestPresenterImp.KEY_TELEGRAM_CHANNEL_ID, mTelegramChannelId);
        mPostUserInterestPresenter.postAsync(this, nvdh);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_channel_detail, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setupToolbar(toolbar, mFragmentTitle);

        mTelegramChannelCoverImageView = (ImageView) v.findViewById(R.id.backdrop);

        collapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mFragmentTitle);

        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.fab);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.MyRecyclerView);

        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mHasMoreItem = true;

        return v;
    }


    private void setupToolbar(Toolbar toolbar, String title) {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_channel_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.report_channel:
                OnChannelReportButtonClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        super.onResume();
        loadChannelMetaData(mNextPage);
        mGetChannelMetaDataPresenter.register(this, mGetMetaDataRequestId);
        mPostCommentPresenter.register(this, mPostCommentRequestId);
        mPostRatingPresenter.register(this, mPostRatingRequestId);
        mPostReportChannelPresenter.register(this, mPostReportChannelRequestId);
        mPostReportCommnetPresenter.register(this, mPostReportCommentRequestId);

    }

    @Override
    public void onPause() {
        super.onPause();
        mGetChannelMetaDataPresenter.unregister(this, mGetMetaDataRequestId);
        mPostCommentPresenter.unregister(this, mPostCommentRequestId);
        mPostRatingPresenter.unregister(this, mPostRatingRequestId);
        mPostReportChannelPresenter.unregister(this, mPostReportChannelRequestId);
        mPostReportCommnetPresenter.unregister(this, mPostReportCommentRequestId);
        if(mSnackbar != null && mSnackbar.isShown())mSnackbar.dismiss();
    }

    private void loadChannelMetaData(int pageNumber) {


        if (NetworkUtils.isOnline(getActivity()) || pageNumber == 0) {

            NameValueDataHolder dataHolder = new NameValueDataHolder();
            dataHolder.put(GetChannelMetaDataPresenter.KEY_PAGE, pageNumber);
            dataHolder.put(GetChannelMetaDataPresenter.KEY_TELEGRAM_CHANNEL_ID, mTelegramChannelId);
            mGetMetaDataRequestId = mGetChannelMetaDataPresenter.getAsync(this, dataHolder);

        } else {
            mGetMetaDataRequestId = PENDING_REQUEST_ID;
            showShortToastMessage(R.string.no_network_connection);
        }

    }




    @Override
    public void onSuccess(int requestId, ChannelMetaDataHolder dataHolder) {


        String message = dataHolder.getString(GetChannelMetaDataPresenter.KEY_MESSAGE);
        if (mGetMetaDataRequestId == requestId) {

            mGetMetaDataRequestId = INVALID_REQUEST_ID;

            TelegramChannel telegramChannel = dataHolder.getTelegramChannel(
                    GetChannelMetaDataPresenter.KEY_TELEGRAM_CHANNEL
            );
            List<TelegramChannel> relatedTelegramChannelList =
                    dataHolder.getList(GetChannelMetaDataPresenter.KEY_RELATED_TELEGRAM_CHANNEL_LIST);
            List<Tag> tagList =
                    dataHolder.getList(GetChannelMetaDataPresenter.KEY_TAG_LIST);
            List<Comment> localCommentList =
                    dataHolder.getList(GetChannelMetaDataPresenter.KEY_LOCAL_COMMENT_LIST);
            List<Comment> downloadedCommentList =
                    dataHolder.getList(GetChannelMetaDataPresenter.KEY_DOWNLOADED_COMMENT_LIST);
            boolean hasMoreItem =
                    dataHolder.getBoolean(GetChannelMetaDataPresenter.KEY_HAS_MORE_ITEMS);

            if (mAdapter == null ) {

                if(telegramChannel == null)return;
                setupAdapter(telegramChannel);
                initWidget(telegramChannel.getLink(), telegramChannel.getImage());
            }
            mHasMoreItem = hasMoreItem;
            mAdapter.setIsLoading(false);
            mAdapter.setHasMoreItem(hasMoreItem);
            mAdapter.setMetaData(localCommentList, downloadedCommentList, relatedTelegramChannelList, tagList);
            mCurrentPage = mNextPage;
            mNextPage++;

        } else if (requestId == mPostCommentRequestId) {

            mPostCommentRequestId = INVALID_REQUEST_ID;
            showShortToastMessage(message);

        } else if (requestId == mPostRatingRequestId) {

            mPostRatingRequestId = INVALID_REQUEST_ID;
            showShortToastMessage(message);

        } else if (requestId == mPostReportCommentRequestId) {

            mPostReportCommentRequestId = INVALID_REQUEST_ID;
            showShortToastMessage(message);


        } else if (requestId == mPostReportChannelRequestId) {

            mPostReportChannelRequestId = INVALID_REQUEST_ID;
            showShortToastMessage(message);

        }

    }


    @Override
    public void onFail(final int requestId, final ChannelMetaDataHolder dataHolder) {

        String message = dataHolder.getString(GetChannelMetaDataPresenter.KEY_MESSAGE);
        mSnackbar = Snackbar.make(
                mRecyclerView,
                message,
                Snackbar.LENGTH_INDEFINITE
        );
        mSnackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (requestId == mGetMetaDataRequestId) {
                    loadChannelMetaData(mNextPage);
                } else if (requestId == mPostCommentRequestId) {
                    OnCommentSendButtonClick(mAddedComment);
                } else if (requestId == mPostRatingRequestId) {
                    OnRatingSendButtonClick(mRate);
                } else if (requestId == mPostReportChannelRequestId) {
                    OnChannelReportButtonClick();
                } else if (requestId == mPostReportCommentRequestId) {
                    OnCommentReportButtonClick(mReportedComment);
                }
                mSnackbar.dismiss();
            }
        });
        if (NetworkUtils.isOnline(getActivity())) mSnackbar.show();


    }


    private void setupAdapter(TelegramChannel telegramChannel) {


        mAdapter = new TelegramChannelDetailAdapter(
                getActivity(),
                telegramChannel,
                mRecyclerView,
                this,
                this,
                this,
                this,
                this,
                this

        );
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mHasMoreItem) {
                    loadChannelMetaData(mNextPage);
                }

            }
        });

    }

    @Override
    public void OnTagChannelClick(Tag tag) {

        Intent intent = new Intent(getActivity(), ChannelListActivity.class);
        intent.putExtra(ChannelListActivity.EXTRA_TITLE, tag.getName());
        intent.putExtra(ChannelListFragment.EXTRA_ACTION, ChannelListFragment.ACTION_SEARCH_BY_TAG);
        intent.putExtra(ChannelListFragment.EXTRA_TAG, tag);
        getActivity().startActivity(intent);


    }

    @Override
    public void OnRelatedChannelClick(TelegramChannel telegramChannel) {

        Intent intent = new Intent(getActivity(), ChannelDetailActivity.class);
        intent.putExtra(ChannelDetailActivity.EXTRA_TELEGRAM_CHANNEL_ID, telegramChannel.getServerId());
        intent.putExtra(ChannelDetailActivity.EXTRA_TELEGRAM_CHANNEL_TITLE, telegramChannel.getTitle());
        getActivity().startActivity(intent);
    }


    private void initWidget(final String link, final String imageUrl) {

        Glide.with(getActivity())
                .load(imageUrl)
                .into(mTelegramChannelCoverImageView);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OnLinkClick(link);

            }
        });
    }

    @Override
    public void OnCommentSendButtonClick(String text) {

        mAddedComment = text;

        if (NetworkUtils.isOnline(getActivity())) {

            showLongToastMessage(R.string.comment_has_sent);

            NameValueDataHolder dataHolder = new NameValueDataHolder();
            dataHolder.put(AddCommentPresenter.KEY_COMMENT, text);
            dataHolder.put(AddCommentPresenter.KEY_TELEGRAM_CHANNEL_ID, mTelegramChannelId);
            mPostCommentPresenter.unregister(this, mPostCommentRequestId);
            mPostCommentRequestId = mPostCommentPresenter.postAsync(this, dataHolder);

        } else {
            showLongToastMessage(R.string.no_network_connection);
        }
    }

    @Override
    public void OnRatingSendButtonClick(int rate) {

        mRate = rate;
        if (NetworkUtils.isOnline(getActivity())) {

            showLongToastMessage(R.string.rate_has_sent);


            NameValueDataHolder dataHolder = new NameValueDataHolder();
            dataHolder.put(RateChannelPresenter.KEY_RATE, rate);
            dataHolder.put(RateChannelPresenter.KEY_TELEGRAM_CHANNEL_ID, mTelegramChannelId);
            mPostRatingPresenter.unregister(this, mPostRatingRequestId);
            mPostRatingRequestId = mPostRatingPresenter.postAsync(this, dataHolder);

        } else {

            showLongToastMessage(R.string.no_network_connection);

        }
    }


    @Override
    public void OnChannelReportButtonClick() {
        mIsChannelReported = true;

        if (NetworkUtils.isOnline(getActivity())) {

            Dialogs.getInstanse().showReportChannelDialog(
                    getActivity(),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (NetworkUtils.isOnline(getActivity())) {

                                showLongToastMessage(R.string.report_text_has_sent);
                                NameValueDataHolder dataHolder = new NameValueDataHolder();
                                dataHolder.put(ReportChannelPresenter.KEY_TELEGRAM_CHANNEL_ID, mTelegramChannelId);
                                mPostReportChannelPresenter.unregister(ChannelDetailFragment.this, mPostReportChannelRequestId);
                                mPostReportChannelRequestId = mPostReportChannelPresenter.postAsync(ChannelDetailFragment.this, dataHolder);
                            }
                        }
                    },
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }
            );






        } else {
            showLongToastMessage(R.string.no_network_connection);
        }

    }

    @Override
    public void OnCommentReportButtonClick(Comment comment) {

        mReportedComment = comment;


        Dialogs.getInstanse().showReportCommentDialog(
                getActivity(),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (NetworkUtils.isOnline(getActivity())) {

                            showLongToastMessage(R.string.report_text_has_sent);
                            NameValueDataHolder dataHolder = new NameValueDataHolder();
                            dataHolder.put(ReportCommentPresenter.KEY_COMMENT_ID, mReportedComment.getServerId());
                            mPostReportCommnetPresenter.unregister(ChannelDetailFragment.this, mPostReportCommentRequestId);
                            mPostReportCommentRequestId = mPostReportCommnetPresenter.postAsync(ChannelDetailFragment.this, dataHolder);


                        }
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }
        );

    }




    @Override
    public void OnLinkClick(String link) {

        if (NetworkUtils.isOnline(getActivity())) {

            NameValueDataHolder nvdh = new NameValueDataHolder();
            nvdh.put(PostUserInterestPresenterImp.KEY_TYPE, GoftagramContract.SEEN_TYPE_IN_TELEGRAM);
            nvdh.put(PostUserInterestPresenterImp.KEY_TELEGRAM_CHANNEL_ID, mTelegramChannelId);
            mPostUserInterestPresenter.postAsync(ChannelDetailFragment.this, nvdh);

        }

        if (!TextUtils.isEmpty(link)) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            Intent intent = new Intent(getActivity(), LaunchActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData( Uri.parse(link));
            startActivity(intent);
        }

    }

    void showLongToastMessage(int stringResourceId){
        showToastMessage(stringResourceId, Toast.LENGTH_LONG);
    }

    void showLongToastMessage(String message){
        showToastMessage(message, Toast.LENGTH_LONG);
    }


    void showShortToastMessage(int stringResourceId){
        showToastMessage(stringResourceId, Toast.LENGTH_SHORT);
    }

    void showShortToastMessage(String message){
        showToastMessage(message, Toast.LENGTH_SHORT);
    }

    void showToastMessage(int stringResourceId,int length){
        Toast.makeText(
                getActivity(),
                getResources().getString(stringResourceId),
                length
        ).show();
    }

    void showToastMessage(String message,int length){
        Toast.makeText(
                getActivity(),
                message,
                length
        ).show();
    }
}
