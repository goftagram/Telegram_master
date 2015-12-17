package com.goftagram.telegram.goftagram.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.goftagram.telegram.goftagram.provider.GoftagramContract.*;

public class GoftagramProvider extends ContentProvider {

    private final String LOG_TAG = GoftagramProvider.class.getSimpleName();


    private static final UriMatcher sUriMatcher = buildUriMatcher();


    private static final int ALL_ROWS = 101;

    private static final int TELEGRAM_CHANNEL_ROWS = 201;
    private static final int TELEGRAM_CHANNEL_ROW = 202;
    private static final int PROMOTED_TELEGRAM_CHANNEL_ROWS = 203;
    private static final int TOP_RATED_TELEGRAM_CHANNEL_ROWS = 204;
    private static final int NEW_TELEGRAM_CHANNEL_ROWS = 205;

    private static final int CATEGORY_ROWS = 300;
    private static final int CATEGORY_ROW = 301;

    private static final int TELEGRAM_CHANNEL_OF_TAG_ROWS = 302;

    private static final int TELEGRAM_CHANNEL_OF_QUERY_ROWS = 303;

    private static final int TELEGRAM_CHANNEL_BY_CATEGORY_ROWS = 900;

    private static final int COMMENT_ROWS = 1000;

    private static final int TAG_ROWS = 1100;
    private static final int TAG_OF_TELEGRAM_CHANNEL_ROWS = 1200;


    private static final int TELEGRAM_CHANNEL_TELEGRAM_CHANNEL_ROWS = 1300;

    private static final int SEARCHED_QUERY_ROWS = 1400;


    private static final SQLiteQueryBuilder sTopRatedChannelQueryBuilder;
    private static final SQLiteQueryBuilder sPromotedChannelQueryBuilder;
    private static final SQLiteQueryBuilder sNewChannelQueryBuilder;
    private static final SQLiteQueryBuilder sTagTelegramChannelQueryBuilder;
    private static final SQLiteQueryBuilder sQueryTelegramChannelQueryBuilder;
    private static final SQLiteQueryBuilder sTelegramChannelByCategoryQueryBuilder;
    private static final SQLiteQueryBuilder sRelatedTelegramChannelQueryBuilder;


    static {

        sTopRatedChannelQueryBuilder            = new SQLiteQueryBuilder();
        sPromotedChannelQueryBuilder            = new SQLiteQueryBuilder();
        sNewChannelQueryBuilder                 = new SQLiteQueryBuilder();
        sTagTelegramChannelQueryBuilder         = new SQLiteQueryBuilder();
        sTelegramChannelByCategoryQueryBuilder  = new SQLiteQueryBuilder();
        sRelatedTelegramChannelQueryBuilder     = new SQLiteQueryBuilder();
        sQueryTelegramChannelQueryBuilder       = new SQLiteQueryBuilder();

        sTopRatedChannelQueryBuilder.setTables(
                TopRatedTelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + TelegramChannelEntry.TABLE_NAME +
                        " ON " + TopRatedTelegramChannelEntry.TABLE_NAME +
                        "." + TopRatedTelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID);

        sPromotedChannelQueryBuilder.setTables(
                PromotedTelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + TelegramChannelEntry.TABLE_NAME +
                        " ON " + PromotedTelegramChannelEntry.TABLE_NAME +
                        "." + PromotedTelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID);

        sNewChannelQueryBuilder.setTables(
                NewTelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + TelegramChannelEntry.TABLE_NAME +
                        " ON " + NewTelegramChannelEntry.TABLE_NAME +
                        "." + NewTelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID);



        sTelegramChannelByCategoryQueryBuilder.setTables(
                TelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + CategoryEntry.TABLE_NAME +
                        " ON " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + CategoryEntry.TABLE_NAME +
                        "." + CategoryEntry.COLUMN_SERVER_ID
        );


        sTagTelegramChannelQueryBuilder.setTables(
                TelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + TelegramChannelTagEntry.TABLE_NAME +
                        " ON " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelTagEntry.TABLE_NAME +
                        "." + TelegramChannelTagEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID +
                        " INNER JOIN " + TagEntry.TABLE_NAME +
                        " ON " + TagEntry.TABLE_NAME +
                        "." + TagEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelTagEntry.TABLE_NAME +
                        "." + TelegramChannelTagEntry.COLUMN_TAG_SERVER_ID
        );

        sRelatedTelegramChannelQueryBuilder.setTables(
                TelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + TelegramChannelTelegramChannelEntry.TABLE_NAME +
                        " ON " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelTelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_2
        );

        sQueryTelegramChannelQueryBuilder.setTables(
                TelegramChannelEntry.TABLE_NAME +
                        " INNER JOIN " + TelegramChannelSearchedQueryEntry.TABLE_NAME +
                        " ON " + TelegramChannelEntry.TABLE_NAME +
                        "." + TelegramChannelEntry.COLUMN_SERVER_ID +
                        " = " + TelegramChannelSearchedQueryEntry.TABLE_NAME +
                        "." + TelegramChannelSearchedQueryEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID +
                        " INNER JOIN " + SearchedQueryEntry.TABLE_NAME +
                        " ON " + SearchedQueryEntry.TABLE_NAME +
                        "." + SearchedQueryEntry.COLUMN_QUERY +
                        " = " + TelegramChannelSearchedQueryEntry.TABLE_NAME +
                        "." + TelegramChannelSearchedQueryEntry.COLUMN_SEARCHED_QUERY
        );

    }


    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GoftagramContract.CONTENT_AUTHORITY;

        // content://com.goftagram.telegram.goftagram/all/
        matcher.addURI(authority,  GoftagramContract.PATH_ALL, ALL_ROWS);

        // content://com.goftagram.telegram.goftagram/telegramChannel/
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL, TELEGRAM_CHANNEL_ROWS);

        //content://com.goftagram.telegram.goftagram/telegramChannel/detail
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_DETAIL, TELEGRAM_CHANNEL_ROW);

        //content://com.goftagram.telegram.goftagram/telegramChannel/promoted
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_PROMOTED, PROMOTED_TELEGRAM_CHANNEL_ROWS);

        //content://com.goftagram.telegram.goftagram/telegramChannel/topRated
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_TOP_RATED, TOP_RATED_TELEGRAM_CHANNEL_ROWS);

        //content://com.goftagram.telegram.goftagram/telegramChannel/topRated
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_NEW, NEW_TELEGRAM_CHANNEL_ROWS);

        //content://com.goftagram.telegram.goftagram/category
        matcher.addURI(authority, GoftagramContract.PATH_CATEGORY, CATEGORY_ROWS);

        //content://com.goftagram.telegram.goftagram/category/id
        matcher.addURI(authority, GoftagramContract.PATH_CATEGORY + "/" +GoftagramContract.PATH_ID
                ,CATEGORY_ROW);


        //content://com.goftagram.telegram.goftagram/telegramChannel/category
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_CATEGORY, TELEGRAM_CHANNEL_BY_CATEGORY_ROWS);

        //content://com.goftagram.telegram.goftagram/tag
        matcher.addURI(authority, GoftagramContract.PATH_TAG, TAG_ROWS);

        //content://com.goftagram.telegram.goftagram/tag/telegramChannel
        matcher.addURI(authority, GoftagramContract.PATH_TAG  + "/" +
                GoftagramContract.PATH_TELEGRAM_CHANNEL
                ,TELEGRAM_CHANNEL_OF_TAG_ROWS );

        //content://com.goftagram.telegram.goftagram/telegramChannel/comment
        matcher.addURI(authority, GoftagramContract.PATH_COMMENT, COMMENT_ROWS);

        //content://com.goftagram.telegram.goftagram/telegramChannel/tag
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_DETAIL + "/" + GoftagramContract.PATH_TAG
                , TAG_OF_TELEGRAM_CHANNEL_ROWS);

        //content://com.goftagram.telegram.goftagram/telegramChannel/telegramChannel
        matcher.addURI(authority, GoftagramContract.PATH_TELEGRAM_CHANNEL + "/" +
                GoftagramContract.PATH_TELEGRAM_CHANNEL, TELEGRAM_CHANNEL_TELEGRAM_CHANNEL_ROWS);


        //content://com.goftagram.telegram.goftagram/searchedQuery
        matcher.addURI(authority, GoftagramContract.PATH_SEARCHED_QUERY, SEARCHED_QUERY_ROWS);

        //content://com.goftagram.telegram.goftagram/searchedQuery/telegramChannel
        matcher.addURI(authority, GoftagramContract.PATH_SEARCHED_QUERY + "/"
                + GoftagramContract.PATH_TELEGRAM_CHANNEL,TELEGRAM_CHANNEL_OF_QUERY_ROWS );

        return matcher;
    }


    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = OpenDatabaseHelper.getInstance(getContext()).getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public synchronized Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs,
                                     String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {

            case TELEGRAM_CHANNEL_ROW: {

                retCursor = mDatabase.query(TelegramChannelEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null,
                        null);
            }
            retCursor.setNotificationUri(getContext().getContentResolver(), TelegramChannelEntry
                    .buildTelegramChannel());
            break;


            case TELEGRAM_CHANNEL_BY_CATEGORY_ROWS: {

                retCursor = mDatabase.query(TelegramChannelEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        TelegramChannelEntry._ID +" ASC",
                        null);

            }
            retCursor.setNotificationUri(
                    getContext().getContentResolver(), TelegramChannelEntry.buildTelegramChannel());
            break;

            case TOP_RATED_TELEGRAM_CHANNEL_ROWS: {


                retCursor = sTopRatedChannelQueryBuilder.query(mDatabase,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        TopRatedTelegramChannelEntry._ID +" ASC ",
                        null);


            }
            retCursor.setNotificationUri(getContext().getContentResolver(), TelegramChannelEntry.buildTelegramChannel());
            break;


            case PROMOTED_TELEGRAM_CHANNEL_ROWS: {

              retCursor = sPromotedChannelQueryBuilder.query(mDatabase,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        PromotedTelegramChannelEntry._ID +" ASC",
                        null);
            }
            retCursor.setNotificationUri(
                    getContext().getContentResolver(), TelegramChannelEntry.buildTelegramChannel());
            break;


            case NEW_TELEGRAM_CHANNEL_ROWS: {

                retCursor = sNewChannelQueryBuilder.query(mDatabase,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        NewTelegramChannelEntry._ID +" ASC ",
                        null);
            }
            retCursor.setNotificationUri(
                    getContext().getContentResolver(), TelegramChannelEntry.buildTelegramChannel());
            break;



            case CATEGORY_ROWS: {

                retCursor = mDatabase.query(CategoryEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        CategoryEntry.COLUMN_ORDER +" DESC",
                        null);

            }
            retCursor.setNotificationUri(getContext().getContentResolver(), CategoryEntry.buildCategoryList());
            break;


            case COMMENT_ROWS: {


                retCursor = mDatabase.query(CommentEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        CommentEntry._ID +" ASC",
                        null);

            }
            retCursor.setNotificationUri(getContext().getContentResolver(),
                    CommentEntry.buildCommentUri()
            );
            break;

            case TAG_ROWS: {

                retCursor = mDatabase.query(TagEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null,
                        null);

                retCursor.setNotificationUri(getContext().getContentResolver(),
                        TagEntry.buildTagUri()
                );
            }
            break;

            case SEARCHED_QUERY_ROWS: {

                retCursor = mDatabase.query(SearchedQueryEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        SearchedQueryEntry._ID +" ASC",
                        null);

                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            }
            break;


            case TAG_OF_TELEGRAM_CHANNEL_ROWS: {

                retCursor =   sTagTelegramChannelQueryBuilder.query(mDatabase,
                        new String[]{
                                TagEntry.TABLE_NAME +"."+TagEntry._ID,
                                TagEntry.TABLE_NAME +"."+TagEntry.COLUMN_STATE,
                                TagEntry.TABLE_NAME +"."+TagEntry.COLUMN_UPDATED,
                                TagEntry.TABLE_NAME +"."+TagEntry.COLUMN_SERVER_ID,
                                TagEntry.TABLE_NAME +"."+TagEntry.COLUMN_NAME,
                                TagEntry.TABLE_NAME +"."+TagEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL
                        },
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null,
                        null
                );

                retCursor.setNotificationUri(getContext().getContentResolver(),
                        TelegramChannelTagEntry.CONTENT_URI
                );
            }
            break;

            case TELEGRAM_CHANNEL_TELEGRAM_CHANNEL_ROWS: {

                retCursor =   sRelatedTelegramChannelQueryBuilder.query(mDatabase,
                        new String[]{
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry._ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STATE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_UPDATED,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_CATEGORY_ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_SERVER_ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_DESCRIPTION,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_IMAGE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_LINK,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_RATE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_5,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_4,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_3,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_2,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_1,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_THUMBNAIL,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_TITLE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_HAS_READ_ALL_COMMENTS

                        },
                        selection,
                        selectionArgs,
                        null,
                        null,
                        TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry._ID +" ASC",
                        null
                );

                retCursor.setNotificationUri(getContext().getContentResolver(),
                        TelegramChannelEntry.buildTelegramChannel()
                );
            }
            break;

            case TELEGRAM_CHANNEL_OF_TAG_ROWS: {

                String tagId = uri.getQueryParameter(TagEntry.QUERY_PARAMETER_TAG_ID);

                retCursor =   sTagTelegramChannelQueryBuilder.query(mDatabase,
                        new String[]{
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry._ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STATE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_UPDATED,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_CATEGORY_ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_SERVER_ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_DESCRIPTION,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_IMAGE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_LINK,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_RATE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_5,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_4,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_3,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_2,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_1,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_THUMBNAIL,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_TITLE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_HAS_READ_ALL_COMMENTS

                        },
                        TagEntry.TABLE_NAME+"."+TagEntry.COLUMN_SERVER_ID  + " = ? ",
                        new String[]{tagId},
                        null,
                        null,
                        TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry._ID +" ASC",
                        null
                );

                retCursor.setNotificationUri(getContext().getContentResolver(),
                        TelegramChannelEntry.buildTelegramChannel());
            }
            break;


            case TELEGRAM_CHANNEL_OF_QUERY_ROWS: {

                String query = uri.getQueryParameter(SearchedQueryEntry.QUERY_PARAMETER_QUERY);

                retCursor =   sQueryTelegramChannelQueryBuilder.query(mDatabase,
                        new String[]{
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry._ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STATE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_UPDATED,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_CATEGORY_ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_SERVER_ID,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_DESCRIPTION,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_IMAGE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_LINK,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_RATE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_5,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_4,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_3,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_2,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_STAR_1,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_THUMBNAIL,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_TITLE,
                                TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry.COLUMN_HAS_READ_ALL_COMMENTS

                        },
                        SearchedQueryEntry.TABLE_NAME+"."+SearchedQueryEntry.COLUMN_QUERY  + " = ? ",
                        new String[]{query},
                        null,
                        null,
                        TelegramChannelEntry.TABLE_NAME+"."+TelegramChannelEntry._ID +" ASC",
                        null
                );

                retCursor.setNotificationUri(getContext().getContentResolver(),
                        TelegramChannelEntry.buildTelegramChannel());
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        return retCursor;
    }

    @Nullable
    @Override
    public synchronized String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public synchronized Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case CATEGORY_ROW: {
                long _id = -1;
                Integer conflictAlgorithm = null;
                if (uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_CONFLICT_FLAG) != null) {
                    conflictAlgorithm = Integer.valueOf(
                            uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_CONFLICT_FLAG));
                }
                String id = null;
                if (uri.getQueryParameter(CategoryEntry.QUERY_PARAMETER_ID) != null) {
                    id = uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_ID);
                }
                if(conflictAlgorithm != null){

                    _id = mDatabase.insertWithOnConflict(
                            CategoryEntry.TABLE_NAME,
                            null,
                            contentValues,
                            conflictAlgorithm
                    );

                }else{

                    _id = mDatabase.insert(
                            CategoryEntry.TABLE_NAME,
                            null,
                            contentValues
                    );
                }

                if (_id > 0)
                    returnUri = CategoryEntry.buildCategoryByIdUri(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            case SEARCHED_QUERY_ROWS: {
                long _id = -1;
                Integer conflictAlgorithm = null;
                if (uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_CONFLICT_FLAG) != null) {
                    conflictAlgorithm = Integer.valueOf(
                            uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_CONFLICT_FLAG));
                }
                String id = null;
                if (uri.getQueryParameter(CategoryEntry.QUERY_PARAMETER_ID) != null) {
                    id = uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_ID);
                }
                if(conflictAlgorithm != null){
                    _id = mDatabase.insertWithOnConflict(
                            SearchedQueryEntry.TABLE_NAME,
                            null,
                            contentValues,
                            conflictAlgorithm
                    );
                }else{
                    _id = mDatabase.insert(
                            SearchedQueryEntry.TABLE_NAME,
                            null,
                            contentValues
                    );
                }
                if (_id > 0)
                    returnUri = SearchedQueryEntry.buildSearchedQueryUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case TELEGRAM_CHANNEL_ROW: {


                long _id = mDatabase.insert(
                        TelegramChannelEntry.TABLE_NAME, null, contentValues
                );
                if (_id > 0)
                    returnUri = TelegramChannelEntry.buildTelegramChannel();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case ALL_ROWS:

                rowsDeleted = mDatabase.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(CommentEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(TagEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(TelegramChannelEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(TopRatedTelegramChannelEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(PromotedTelegramChannelEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(NewTelegramChannelEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(TelegramChannelTagEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(TelegramChannelTelegramChannelEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(SearchedQueryEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += mDatabase.delete(TelegramChannelSearchedQueryEntry.TABLE_NAME, selection, selectionArgs);


                getContext().getContentResolver().notifyChange(GoftagramContract.BASE_CONTENT_URI, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return rowsDeleted;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {


        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TELEGRAM_CHANNEL_ROW: {
                rowsUpdated = mDatabase.update(
                        TelegramChannelEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);

                break;

            }

            case CATEGORY_ROWS: {
                rowsUpdated = mDatabase.update(
                        CategoryEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);

                break;

            }

            case TAG_ROWS: {
                rowsUpdated = mDatabase.update(
                        TagEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);

                break;

            }

            case SEARCHED_QUERY_ROWS: {
                rowsUpdated = mDatabase.update(
                        SearchedQueryEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);

                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

    @Override
    public synchronized int bulkInsert(Uri uri, ContentValues[] values) {

        final int match = sUriMatcher.match(uri);
        Integer conflictAlgorithm = null;
        if (uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_CONFLICT_FLAG) != null) {
            conflictAlgorithm = Integer.valueOf(
                    uri.getQueryParameter(GoftagramQueryParameter.QUERY_PARAMETER_CONFLICT_FLAG));
        }
        long _id = -1;
        switch (match) {
            case TELEGRAM_CHANNEL_ROWS: {

                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TelegramChannelEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TelegramChannelEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case TOP_RATED_TELEGRAM_CHANNEL_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TopRatedTelegramChannelEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TopRatedTelegramChannelEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case PROMOTED_TELEGRAM_CHANNEL_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    PromotedTelegramChannelEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    PromotedTelegramChannelEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }


            case NEW_TELEGRAM_CHANNEL_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    NewTelegramChannelEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    NewTelegramChannelEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }


            case CATEGORY_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    CategoryEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    CategoryEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case TELEGRAM_CHANNEL_TELEGRAM_CHANNEL_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TelegramChannelTelegramChannelEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TelegramChannelTelegramChannelEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }


                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }


            case TAG_OF_TELEGRAM_CHANNEL_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TelegramChannelTagEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TelegramChannelTagEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(
                        TelegramChannelTagEntry.CONTENT_URI, null
                );
                return returnCount;
            }

            case TELEGRAM_CHANNEL_OF_QUERY_ROWS: {

                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TelegramChannelSearchedQueryEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TelegramChannelSearchedQueryEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(
                        TelegramChannelSearchedQueryEntry.CONTENT_URI, null
                );
                return returnCount;
            }

            case TAG_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TagEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TagEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case COMMENT_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    CommentEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    CommentEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }


                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TELEGRAM_CHANNEL_OF_TAG_ROWS: {
                mDatabase.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        if (conflictAlgorithm == null) {
                            _id = mDatabase.insert(
                                    TelegramChannelTagEntry.TABLE_NAME, null, value
                            );
                        } else {
                            _id = mDatabase.insertWithOnConflict(
                                    TelegramChannelTagEntry.TABLE_NAME, null, value, conflictAlgorithm
                            );
                        }


                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }


            default:
                return super.bulkInsert(uri, values);
        }

    }
}
