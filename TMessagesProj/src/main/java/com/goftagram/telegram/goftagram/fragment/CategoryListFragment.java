package com.goftagram.telegram.goftagram.fragment;

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
import com.goftagram.telegram.goftagram.activity.ChannelListActivity;
import com.goftagram.telegram.goftagram.adapter.CategoryGridAdapter;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryViewModel;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation.GetCategoryPresenterImp;
import com.goftagram.telegram.goftagram.network.api.message.ConnectionNetworkMessage;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.DeviceConfigUtils;
import com.goftagram.telegram.goftagram.util.NetworkUtils;
import com.goftagram.telegram.messenger.R;


public class CategoryListFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        GetCategoryViewModel,
        CategoryGridAdapter.onItemClickListener {

    private final String LOG_TAG = CategoryListFragment.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String EXTRA_FRAGMENT_TITLE = "fragment_title";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";

    public static final int INVALID_TRANSACTION_ID = -1;

    public static final int CATEGORY_LOADER = 100;



    private int mTransactionId;
    private String mFragmentTitle;

    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private CategoryGridAdapter mCategoryGridAdapter;

    private CircularProgressView mCircularProgressView;

    private RelativeLayout mRootViewRelativeLayout;
    private RelativeLayout mEmptyListRootView;
    private RelativeLayout mNoNetworkRootView;

    AbsGetPresenter mPresenter;

    public static CategoryListFragment newInstance(String title) {

        CategoryListFragment fragment = new CategoryListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FRAGMENT_TITLE, title);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFragmentTitle = getArguments().getString(EXTRA_FRAGMENT_TITLE);
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRANSACTION_ID)){
            mTransactionId = savedInstanceState.getInt(EXTRA_TRANSACTION_ID);
        }else{
            mTransactionId = INVALID_TRANSACTION_ID;
        }
        mPresenter = GetCategoryPresenterImp.getInstance(getActivity());

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_grid_list, container, false);
        mRootViewRelativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout_rootView);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.MyRecyclerView);
        mNoNetworkRootView = (RelativeLayout)v.findViewById(R.id.no_network_rootView);
        mCircularProgressView = (CircularProgressView)v.findViewById(R.id.MyProgressBar);
        mEmptyListRootView = (RelativeLayout) v.findViewById(R.id.empty_list_rootView);

        if (DeviceConfigUtils.isTablet(getActivity()) ||
                DeviceConfigUtils.isLandscape(getActivity())) {

            mGridLayoutManager = new GridLayoutManager(getActivity(), 3,
                    GridLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 2,
                    GridLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restartLoader();
    }


    private void restartLoader(){
        getLoaderManager().restartLoader(CATEGORY_LOADER, null, this).forceLoad();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri categoryListUri = GoftagramContract.CategoryEntry.buildCategoryUriWithTransactionId(mTransactionId);
        return new CursorLoader(getActivity(),
                categoryListUri,
                null,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor.getCount() <= 0){
            mRecyclerView.setAdapter(null);
            mCategoryGridAdapter = null;
            if ( !NetworkUtils.isOnline(getActivity())) {
                mNoNetworkRootView.setVisibility(View.VISIBLE);
                mEmptyListRootView.setVisibility(View.GONE);
                mCircularProgressView.setVisibility(View.GONE);

                return;
            }else{
//                if(mTransactionId == INVALID_TRANSACTION_ID){
                    mNoNetworkRootView.setVisibility(View.GONE);
                    mEmptyListRootView.setVisibility(View.GONE);
                    mCircularProgressView.setVisibility(View.VISIBLE);
                    mPresenter.unregister(this, mTransactionId);
                    mTransactionId = mPresenter.getAsync(this,null);
//                }
                return;
            }
        }else{
            mNoNetworkRootView.setVisibility(View.GONE);
            mEmptyListRootView.setVisibility(View.GONE);
            mCircularProgressView.setVisibility(View.GONE);
        }


        if (mCategoryGridAdapter == null) {
            setupAdapter(cursor);
        } else {
            mCategoryGridAdapter.swapCursor(cursor);
        }
        mCategoryGridAdapter.setOnItemClickListener(this);

    }

    private void setupAdapter(Cursor cursor) {

        mCategoryGridAdapter = new CategoryGridAdapter(getActivity(), cursor);
        mRecyclerView.setAdapter(mCategoryGridAdapter);

    }

    @Override
    public void onItemClick(String categoryId,String title) {
        Intent intent = new Intent(getActivity(), ChannelListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ChannelListActivity.EXTRA_TITLE, title);
        intent.putExtra(ChannelListFragment.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(ChannelListFragment.EXTRA_ACTION, ChannelListFragment.ACTION_GET_CHANNELS_BY_CATEGORY);
        getActivity().startActivity(intent);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTransactionId != INVALID_TRANSACTION_ID){
            mPresenter.register(this, mTransactionId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mTransactionId != INVALID_TRANSACTION_ID){
            outState.putInt(EXTRA_TRANSACTION_ID,mTransactionId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mTransactionId != INVALID_TRANSACTION_ID){
            mPresenter.unregister(this, mTransactionId);
        }

    }

    public void onEventMainThread(ConnectionNetworkMessage event) {
        if (event.isConnected()) {
            if(mCategoryGridAdapter == null ){
                mNoNetworkRootView.setVisibility(View.GONE);
                mCircularProgressView.setVisibility(View.VISIBLE);
                mEmptyListRootView.setVisibility(View.GONE);
            }
            restartLoader();

        }
    }


    @Override
    public void showLoading() {

        mNoNetworkRootView.setVisibility(View.GONE);
        mEmptyListRootView.setVisibility(View.GONE);
        mCircularProgressView.setVisibility(View.VISIBLE);


    }

    @Override
    public void onSuccess(String message, int totalItems) {
        mTransactionId = INVALID_TRANSACTION_ID;
        if(totalItems == 0){
            mNoNetworkRootView.setVisibility(View.GONE);
            mEmptyListRootView.setVisibility(View.VISIBLE);
            mCircularProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFail(String message, int totalItems) {
        mTransactionId = INVALID_TRANSACTION_ID;
        if ( NetworkUtils.isOnline(getActivity())) {
                final Snackbar snackbar = Snackbar.make(mRootViewRelativeLayout,
                        message, Snackbar.LENGTH_INDEFINITE
                );
                snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTransactionId = mPresenter.getAsync(CategoryListFragment.this,null);
                        snackbar.dismiss();
                    }
                });
                snackbar.show();

            }
        }
    }

