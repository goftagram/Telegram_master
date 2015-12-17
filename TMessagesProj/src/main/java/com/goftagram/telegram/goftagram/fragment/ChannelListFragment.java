package com.goftagram.telegram.goftagram.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.goftagram.telegram.goftagram.activity.ChannelDetailActivity;
import com.goftagram.telegram.goftagram.adapter.TelegramChannelGridAdapter;
import com.goftagram.telegram.goftagram.adapter.listener.OnLoadMoreListener;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.implementation.GetCategoryChannelPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.implementation.GetChannelByQueryPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.implementation.GetChannelByTagPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.implementation.GetNewChannelPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.implementation.GetPromotedChannelPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.implementation.GetTopRatedChannelPresenterImp;
import com.goftagram.telegram.goftagram.network.api.message.ConnectionNetworkMessage;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.DeviceConfigUtils;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.goftagram.util.NetworkUtils;
import com.goftagram.telegram.messenger.R;


public class ChannelListFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AbsListViewModel,
        TelegramChannelGridAdapter.onItemClickListener {


    private final String LOG_TAG = LogUtils.makeLogTag(ChannelListFragment.class.getSimpleName());


    public static final String EXTRA_FRAGMENT_TITLE = "fragment_title";
    public static final String EXTRA_ACTION = "Action";


    public static final String ACTION_SEARCH_BY_TAG = "Action_Search_By_Tag";
    public static final String EXTRA_TAG      = "Tag";
    public static final String EXTRA_TAG_NAME = "Tag_Name";
    public static final String EXTRA_TAG_ID = "Tag_Id";


    public static final String ACTION_ADVANCED_SEARCH = "Action_Advanced_Search";
    public static final String EXTRA_SEARCH_INSIDE_TITLE = "Inside_Title";
    public static final String EXTRA_SEARCH_INSIDE_DESCRIPTION = "Inside_Description";
    public static final String EXTRA_ADVANCED_SEARCHED_QUERY = "Advanced_Searched_Query";


    public static final String ACTION_SEARCHED_VIEW_QUERY = "Action_SearchedView_Query";
    public static final String EXTRA_SEARCHED_VIEW_QUERY = "SearchView_Query";

    public static final String ACTION_GET_TOP_RATED_CHANNEL = "Action_Get_Top_Rated_Channel";

    public static final String ACTION_GET_PROMOTED_CHANNEL = "Action_Get_Promoted_Channel";

    public static final String ACTION_GET_NEW_CHANNEL = "Action_Get_New_Channel";

    public static final String ACTION_GET_CHANNELS_BY_CATEGORY = "Action_Get_Channel_By_Category";

    public static final String EXTRA_CATEGORY_ID = "Category_Id";

    private String mAction;


    private String mTagName;
    private String mTagId;


    private String mCategoryId;
    private boolean mHasSearchedInsideTitle;
    private boolean mHasSearchedInsideDescription;


    private String mQuery;




    public static final String EXTRA_TRANSACTION_ID = "transaction_id";

    public static final int INVALID_TRANSACTION_ID = -1;
    public static final int INVALID_SERVER_TOTAL_CHANNELS = -1;


    public static final String EXTRA_NEXT_PAGE      = "Next_Page";
    public static final String EXTRA_CURRENT_PAGE   = "Current_Page";
    public static final String EXTRA_HAS_MORE_ITEM  = "Has_More_Items";
    public static final String EXTRA_TOTAL_SERVER_CHANNELS  = "Total_Server_Channels";
    public static final String EXTRA_TOTAL_CURSOR_CHANNELS  = "Total_Cursor_Channels";



    public static final int LOADER_ID_GET_TOP_RATED_CHANNEL = 1;
    public static final int LOADER_ID_GET_PROMOTED_CHANNEL = 2;
    public static final int LOADER_ID_GET_NEW_CHANNEL = 3;
    public static final int LOADER_ID_SEARCH_BY_TAG = 4;
    public static final int LOADER_ID_ADVANCED_SEARCH = 5;
    public static final int LOADER_ID_GET_CHANNEL_BY_CATEGORY = 6;
    public static final int LOADER_ID_SEARCH_VIEW_QUERY = 7;


    private String mFragmentTitle;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private TelegramChannelGridAdapter mTelegramChannelGridAdapter;

    private int mCurrentPage;
    private int mNextPage;
    private int mTransactionId;
    private int mCurrentLoaderId;
    private CircularProgressView mCircularProgressView;



    private String mSelection;
    private String []mSelectionArgs;
    private String []mProjection;

    private boolean mHasMoreItem;
    private int mTotalServerChannels;
    private int mTotalCursorChannels;

    private int spanCount = 2;

    private RelativeLayout mRootViewRelativeLayout;
    private RelativeLayout mNoNetworkRootView;
    private RelativeLayout mEmptyListRootView;

    AbsGetPresenter mPresenter;


    Uri mTelegramChannelListUri = null;

    public static ChannelListFragment topRatedInstance(Context context) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_ACTION, ACTION_GET_TOP_RATED_CHANNEL);
        argBundle.putString(EXTRA_FRAGMENT_TITLE, context.getString(R.string.top_rated_channel));
        fragment.setArguments(argBundle);

        return fragment;
    }

    public static ChannelListFragment promotedInstance(Context context) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_ACTION, ACTION_GET_PROMOTED_CHANNEL);
        argBundle.putString(EXTRA_FRAGMENT_TITLE, context.getString(R.string.promoted_channel));
        fragment.setArguments(argBundle);

        return fragment;
    }

    public static ChannelListFragment newChannelInstance(Context context) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_ACTION, ACTION_GET_NEW_CHANNEL);
        argBundle.putString(EXTRA_FRAGMENT_TITLE, context.getString(R.string.new_channel));
        fragment.setArguments(argBundle);

        return fragment;
    }

    public static ChannelListFragment searchedTagInstance(Tag tag) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_ACTION, ACTION_SEARCH_BY_TAG);
        argBundle.putString(EXTRA_FRAGMENT_TITLE, tag.getName());
        argBundle.putString(EXTRA_TAG_NAME, tag.getName());
        argBundle.putString(EXTRA_TAG_ID, tag.getServerId());
        fragment.setArguments(argBundle);

        return fragment;
    }

    public static ChannelListFragment advancedSearchedInstance(
            String query, String categoryId, boolean insideTitle, boolean insideDescription
    ) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_ACTION, ACTION_ADVANCED_SEARCH);
        argBundle.putString(EXTRA_FRAGMENT_TITLE, query);
        argBundle.putBoolean(EXTRA_SEARCH_INSIDE_TITLE, insideTitle);
        argBundle.putBoolean(EXTRA_SEARCH_INSIDE_DESCRIPTION, insideDescription);
        argBundle.putString(EXTRA_CATEGORY_ID, categoryId);
        argBundle.putString(EXTRA_ADVANCED_SEARCHED_QUERY, query);
        fragment.setArguments(argBundle);

        return fragment;
    }


    public static ChannelListFragment searchedViewQueryInstance(String query) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_ACTION, ACTION_SEARCHED_VIEW_QUERY);
        argBundle.putString(EXTRA_FRAGMENT_TITLE, query);
        argBundle.putString(EXTRA_SEARCHED_VIEW_QUERY, query);
        fragment.setArguments(argBundle);

        return fragment;
    }


    public static ChannelListFragment categoryChannelInstance(String categoryId, String categoryName) {

        ChannelListFragment fragment = new ChannelListFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(EXTRA_FRAGMENT_TITLE, categoryName);
        argBundle.putString(EXTRA_ACTION, ACTION_GET_CHANNELS_BY_CATEGORY);
        argBundle.putString(EXTRA_CATEGORY_ID, categoryId);
        fragment.setArguments(argBundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initChannelListArguments(getArguments());
        readSavedInstanceState(savedInstanceState);
        setHasOptionsMenu(true);
}

    public void initChannelListArguments(Bundle bundle) {

        mFragmentTitle = bundle.getString(EXTRA_FRAGMENT_TITLE);
        mTransactionId = INVALID_TRANSACTION_ID;
        mCurrentPage = 0;
        mNextPage = 1;
        mAction = bundle.getString(EXTRA_ACTION);
        mHasMoreItem = true;
        mTotalServerChannels = INVALID_SERVER_TOTAL_CHANNELS;
        mTotalCursorChannels = 0;

        switch (mAction) {

            case ACTION_SEARCH_BY_TAG:

                mTagId      = bundle.getString(EXTRA_TAG_ID);
                mTagName    = bundle.getString(EXTRA_TAG_NAME);
                mCurrentLoaderId = LOADER_ID_SEARCH_BY_TAG;

                mPresenter = GetChannelByTagPresenterImp.getInstance(getActivity());
                mTelegramChannelListUri = GoftagramContract
                        .TagEntry.buildTelegramChannelOfTagUri(mTagId);
                mSelection = null;
                mSelectionArgs = null;
                mProjection =null;
                break;

//            case ACTION_ADVANCED_SEARCH:
//
//                mQuery = bundle.getString(EXTRA_ADVANCED_SEARCHED_QUERY);
//                mHasSearchedInsideDescription = bundle.getBoolean(EXTRA_SEARCH_INSIDE_DESCRIPTION);
//                mHasSearchedInsideTitle = bundle.getBoolean(EXTRA_SEARCH_INSIDE_TITLE);
//                mCategoryId = bundle.getString(EXTRA_CATEGORY_ID);
//                mCurrentLoaderId = LOADER_ID_ADVANCED_SEARCH;
//                break;

            case ACTION_SEARCHED_VIEW_QUERY:


                mQuery      = bundle.getString(EXTRA_SEARCHED_VIEW_QUERY);
                mCurrentLoaderId = LOADER_ID_SEARCH_VIEW_QUERY;
                mPresenter = GetChannelByQueryPresenterImp.getInstance(getActivity());
                mTelegramChannelListUri = GoftagramContract
                        .SearchedQueryEntry.buildTelegramChannelOfSearchedQueryUri(mQuery);
                mSelection = null;
                mSelectionArgs = null;
                mProjection =null;

                break;


            case ACTION_GET_TOP_RATED_CHANNEL:

                mCurrentLoaderId = LOADER_ID_GET_TOP_RATED_CHANNEL;
                mHasMoreItem = false;
                mPresenter = GetTopRatedChannelPresenterImp.getInstance(getActivity());
                mTelegramChannelListUri = GoftagramContract
                        .TopRatedTelegramChannelEntry
                        .buildTelegramChannelList();
                mSelection = null;
                mSelectionArgs = null;
                mProjection =null;
                break;

            case ACTION_GET_PROMOTED_CHANNEL:

                mCurrentLoaderId = LOADER_ID_GET_PROMOTED_CHANNEL;
                mPresenter = GetPromotedChannelPresenterImp.getInstance(getActivity());
                mHasMoreItem = false;
                mTelegramChannelListUri = GoftagramContract
                        .PromotedTelegramChannelEntry
                        .buildTelegramChannelList();
                mSelection = null;
                mSelectionArgs = null;
                mProjection =null;
                break;

            case ACTION_GET_NEW_CHANNEL:

                mCurrentLoaderId = LOADER_ID_GET_NEW_CHANNEL;
                mPresenter = GetNewChannelPresenterImp.getInstance(getActivity());
                mHasMoreItem = false;
                mTelegramChannelListUri = GoftagramContract
                        .NewTelegramChannelEntry
                        .buildTelegramChannelList();
                mSelection = null;
                mSelectionArgs = null;
                mProjection =null;
                break;

            case ACTION_GET_CHANNELS_BY_CATEGORY:

                mCurrentLoaderId = LOADER_ID_GET_CHANNEL_BY_CATEGORY;
                mPresenter = GetCategoryChannelPresenterImp.getInstance(getActivity());
                mCategoryId = bundle.getString(EXTRA_CATEGORY_ID);
                mTelegramChannelListUri = GoftagramContract
                        .TelegramChannelEntry
                        .buildTelegramChannelCategoryUri();
                mSelection = GoftagramContract.TelegramChannelEntry.COLUMN_CATEGORY_ID + " = ? AND " +
                        GoftagramContract.TelegramChannelEntry.COLUMN_RANK_IN_CATEGORY + " >= 0 ";
                mSelectionArgs = new String[]{mCategoryId};
                mProjection =null;
                break;
        }

    }


    public void readSavedInstanceState(Bundle savedInstanceState){
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRANSACTION_ID)){
            mTransactionId = savedInstanceState.getInt(EXTRA_TRANSACTION_ID);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_NEXT_PAGE)){
            mNextPage = savedInstanceState.getInt(EXTRA_NEXT_PAGE);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_CURRENT_PAGE)){
            mCurrentPage = savedInstanceState.getInt(EXTRA_CURRENT_PAGE);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_HAS_MORE_ITEM)){
            mHasMoreItem = savedInstanceState.getBoolean(EXTRA_HAS_MORE_ITEM);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TOTAL_SERVER_CHANNELS)){
            mTotalServerChannels = savedInstanceState.getInt(EXTRA_TOTAL_SERVER_CHANNELS);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TOTAL_CURSOR_CHANNELS)){
            mTotalCursorChannels = savedInstanceState.getInt(EXTRA_TOTAL_CURSOR_CHANNELS);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_CATEGORY_ID)){
            mCategoryId = savedInstanceState.getString(EXTRA_CATEGORY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_grid_list, container, false);
        mRootViewRelativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout_rootView);
        mCircularProgressView = (CircularProgressView) v.findViewById(R.id.MyProgressBar);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.MyRecyclerView);
        mNoNetworkRootView = (RelativeLayout) v.findViewById(R.id.no_network_rootView);
        mEmptyListRootView = (RelativeLayout) v.findViewById(R.id.empty_list_rootView);

        spanCount = getSpanCount();
        mGridLayoutManager = new GridLayoutManager(getActivity(), spanCount,
                GridLayoutManager.VERTICAL, false);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mTelegramChannelGridAdapter.getItemViewType(position) ==
                        mTelegramChannelGridAdapter.VIEW_ITEM) {
                    return 1;
                } else {
                    return mGridLayoutManager.getSpanCount();
                }
            }
        });

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mHasMoreItem = true;

        return v;
    }



    private int getSpanCount() {
        int retValue = 2;
        if (DeviceConfigUtils.isTablet(getActivity()) &&
                DeviceConfigUtils.isLandscape(getActivity())) {
            retValue = 4;
        } else if (DeviceConfigUtils.isTablet(getActivity()) ||
                DeviceConfigUtils.isLandscape(getActivity())) {
            retValue = 3;
        } else {
            retValue = 2;
        }
        return retValue;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restartLoader();
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(mCurrentLoaderId, null, this).forceLoad();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                mTelegramChannelListUri,
                mProjection,
                mSelection,
                mSelectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {



        mTotalCursorChannels = cursor.getCount();
        LogUtils.LOGI(LOG_TAG, "onLoadFinished mTotalCursorChannels mFragmentTitle: " +
                mTotalCursorChannels + " " + mFragmentTitle);
        if(mTotalCursorChannels <= 0){
            mCurrentPage = 0;
            mNextPage = 1;
            mRecyclerView.setAdapter(null);
            mTelegramChannelGridAdapter = null;
            if ( !NetworkUtils.isOnline(getActivity())) {
                mNoNetworkRootView.setVisibility(View.VISIBLE);
                mEmptyListRootView.setVisibility(View.GONE);
                mCircularProgressView.setVisibility(View.GONE);
                return;
            }else{
//                LogUtils.LOGI(LOG_TAG, "mTransactionId  mFragmentTitle: " + mTransactionId + " " + mFragmentTitle);
//                if(mTransactionId == INVALID_TRANSACTION_ID){

                    mNoNetworkRootView.setVisibility(View.GONE);
                    mEmptyListRootView.setVisibility(View.GONE);
                    mCircularProgressView.setVisibility(View.VISIBLE);
                    makeNetworkRequest(mNextPage);

//                }
                return;
            }
        }else{
            mNoNetworkRootView.setVisibility(View.GONE);
            mEmptyListRootView.setVisibility(View.GONE);
            mCircularProgressView.setVisibility(View.GONE);
        }
        if (mTelegramChannelGridAdapter == null) {
            setupAdapter(cursor);
        } else {
            mTelegramChannelGridAdapter.swapCursor(cursor);
        }
        updateMoreItemBoolean();
//        LogUtils.LOGI(LOG_TAG, "onLoadFinished mTotalServerChannels: mFragmentTitle" +
//                mTotalServerChannels + " " + mFragmentTitle);
        if (mTotalCursorChannels <= mTotalServerChannels ) {
            if (mTelegramChannelGridAdapter.isLoading()) {
                mTelegramChannelGridAdapter.setIsLoading(false);
            }
//            LogUtils.LOGI(LOG_TAG, "onLoadFinished mCurrentPage: mFragmentTitle " + mCurrentPage +
//                            " "+ mFragmentTitle);

            mCurrentPage = mNextPage;
        }

    }

    private void makeNetworkRequest(int pageNumber) {

//        LogUtils.LOGI(LOG_TAG, "makeNetworkRequest: " + mFragmentTitle + " pageNumber " + pageNumber);
        NameValueDataHolder dataHolder = new NameValueDataHolder();
        mPresenter.unregister(this, mTransactionId);
        switch (mAction) {

            case ACTION_SEARCH_BY_TAG:

                dataHolder.put(GetChannelByTagPresenter.KEY_PAGE,pageNumber);
                dataHolder.put(GetChannelByTagPresenter.KEY_TAG_ID,mTagId);
                mTransactionId = mPresenter.getAsync(this, dataHolder);
                break;

            case ACTION_ADVANCED_SEARCH:
                break;

            case ACTION_SEARCHED_VIEW_QUERY:

                dataHolder.put(GetChannelByQueryPresenter.KEY_PAGE,pageNumber);
                dataHolder.put(GetChannelByQueryPresenter.KEY_QUERY,mQuery);
                mTransactionId = mPresenter.getAsync(this, dataHolder);

                break;


            case ACTION_GET_TOP_RATED_CHANNEL:
            case ACTION_GET_PROMOTED_CHANNEL:
            case ACTION_GET_NEW_CHANNEL:

                mTransactionId = mPresenter.getAsync(this,null);
                break;

            case ACTION_GET_CHANNELS_BY_CATEGORY:

                dataHolder.put(GetCategoryChannelPresenter.KEY_PAGE,pageNumber);
                dataHolder.put(GetCategoryChannelPresenter.KEY_CATEGORY_ID,mCategoryId);
                mTransactionId = mPresenter.getAsync(this, dataHolder);
                break;
        }

    }

    private void setupAdapter(final Cursor cursor) {

        mTelegramChannelGridAdapter = new TelegramChannelGridAdapter(
                getActivity(), cursor, mRecyclerView
        );

        mTelegramChannelGridAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mTelegramChannelGridAdapter);

        mTelegramChannelGridAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mHasMoreItem) {
                    mNextPage = mCurrentPage + 1;
//                    LogUtils.LOGI(LOG_TAG, "setOnLoadMoreListener mCurrentPage: " + mCurrentPage);
//                    LogUtils.LOGI(LOG_TAG, "setOnLoadMoreListener mCurrentPage: " + mNextPage);
                    makeNetworkRequest(mNextPage);

                }

            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateMoreItemBoolean() {
        if(mTotalServerChannels == INVALID_SERVER_TOTAL_CHANNELS ){
            mHasMoreItem = true;
        }else{
            mHasMoreItem = (mTotalServerChannels > mTotalCursorChannels);
        }

        if(mTelegramChannelGridAdapter != null){
            mTelegramChannelGridAdapter.setHasMoreItem(mHasMoreItem);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mTransactionId != INVALID_TRANSACTION_ID) {
            mPresenter.register(this, mTransactionId);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(EXTRA_TRANSACTION_ID, mTransactionId);
        outState.putInt(EXTRA_NEXT_PAGE, mNextPage);
        outState.putInt(EXTRA_CURRENT_PAGE, mCurrentPage);
        outState.putBoolean(EXTRA_HAS_MORE_ITEM, mHasMoreItem);
        outState.putInt(EXTRA_TOTAL_SERVER_CHANNELS, mTotalServerChannels);
        outState.putInt(EXTRA_TOTAL_CURSOR_CHANNELS, mTotalCursorChannels);
        outState.putString(EXTRA_CATEGORY_ID, mCategoryId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mTransactionId != INVALID_TRANSACTION_ID) {
            mPresenter.unregister(this, mTransactionId);
        }

    }


    @Override
    public void onItemClick(String telegramChannelId, String title) {
        Intent intent = new Intent(getActivity(), ChannelDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ChannelDetailActivity.EXTRA_TELEGRAM_CHANNEL_ID, telegramChannelId);
        intent.putExtra(ChannelDetailActivity.EXTRA_TELEGRAM_CHANNEL_TITLE, title);
        getActivity().startActivity(intent);
    }


    public void onEventMainThread(ConnectionNetworkMessage event) {
        if (event.isConnected()) {
            if (mTelegramChannelGridAdapter == null) {
                mNoNetworkRootView.setVisibility(View.GONE);
                mCircularProgressView.setVisibility(View.VISIBLE);
                mEmptyListRootView.setVisibility(View.GONE);
            }
            restartLoader();
        }
    }



    @Override
    public void onSuccess(String message, int totalItems) {
        LogUtils.LOGI(LOG_TAG, "onSuccess: " + mFragmentTitle + " " + totalItems );

        mTransactionId = INVALID_TRANSACTION_ID;
        mCircularProgressView.setVisibility(View.GONE);
        mTotalServerChannels = totalItems;
        updateMoreItemBoolean();
        if(mTotalServerChannels == 0){
            mEmptyListRootView.setVisibility(View.VISIBLE);
        }else{
            mEmptyListRootView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onFail(String message, int totalItems) {
        LogUtils.LOGI(LOG_TAG, "onFail: " + mFragmentTitle + " " + totalItems);
        mTransactionId = INVALID_TRANSACTION_ID;
        mCircularProgressView.setVisibility(View.GONE);
        final Snackbar snackbar = Snackbar.make(
                mRootViewRelativeLayout,
                message,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNetworkRequest(mNextPage);
                if (mNextPage == 1) mCircularProgressView.setVisibility(View.VISIBLE);
                snackbar.dismiss();
            }
        });
        if(NetworkUtils.isOnline(getActivity()))snackbar.show();
    }
}
