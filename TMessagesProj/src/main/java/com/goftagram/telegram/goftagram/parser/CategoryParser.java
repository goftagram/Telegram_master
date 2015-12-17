package com.goftagram.telegram.goftagram.parser;

import com.goftagram.telegram.goftagram.application.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CategoryParser{



    public static final String STATUS           = "status";
    public static final String SUCCESS          = "success";
    public static final String FAIL             = "fail";
    public static final String EXPIRE           = "expire";
    public static final String MESSAGE          = "message";
    public static final String RESPONSE_DATA    = "data";
    public static final String CATEGORIES       = "categories";
    public static final String CATEGORIES_DATA  = "data";
    public static final String ID               = "id";
    public static final String TITLE            = "title";
    public static final String IMAGE            = "img";
    public static final String PATH             = "path";

    public static final String ORDER             = "order";
    public static final String COUNT             = "count";

    public static final int STATUS_CODE_SUCCESS     = 1;
    public static final int STATUS_CODE_FAIL        = 0;
    public static final int STATUS_CODE_EXPIRE      = 2;



    public static CategoryParserResponse listParser(String categoryJson) throws JSONException {


        List<Category> categories = null;
        int statusCode = 0;
        String message;

        JSONObject jsonResponse = new JSONObject(categoryJson);

        if(jsonResponse.getString(STATUS).equals(SUCCESS)){
            categories = new ArrayList<>();
            statusCode = 1;
            message = jsonResponse.getString(MESSAGE);
            JSONObject jsonData   = jsonResponse.getJSONObject(RESPONSE_DATA);
            if( jsonData.has(CATEGORIES)){

                JSONObject jsonCategories = jsonData.getJSONObject(CATEGORIES);
                JSONArray data = jsonCategories.getJSONArray(CATEGORIES_DATA);
                for(int i = 0 ; i < data.length();++i){
                    JSONObject jsonCategory = data.getJSONObject(i);
                    Category category = parser(jsonCategory);
                    categories.add(category);
                }

            }else{

                statusCode = STATUS_CODE_FAIL;
                message = jsonResponse.getString(MESSAGE);
            }

        }else if(jsonResponse.getString(STATUS).equals(EXPIRE)){
            statusCode = 2;
            message = jsonResponse.getString(MESSAGE);
        }else {
            statusCode = 0;
            message = jsonResponse.getString(MESSAGE);
        }
        CategoryParserResponse response = new CategoryParserResponse(message,statusCode,categories);
        return response;

    }

    public static Category parser(String jsonCategory) throws JSONException {
        Category category = parser(new JSONObject(jsonCategory));
        return category;
    }

    public static Category parser(JSONObject jsonCategory) throws JSONException {

        Category category = new Category();
        category.setServerId(jsonCategory.getString(ID));
        category.setTitle(jsonCategory.getString(TITLE));
        if(jsonCategory.has(COUNT))
        category.setEstimatedCount(jsonCategory.getInt(COUNT));
        if(jsonCategory.has(ORDER))category.setOrder(jsonCategory.getInt(ORDER));

        if (jsonCategory.has(IMAGE)) {
            JSONObject jsonImage = jsonCategory.getJSONObject(IMAGE);
            category.setThumbnail(jsonImage.getString(PATH));
        }

        return category;
    }

    public static class CategoryParserResponse {
        public String mMessage;
        public int mStatusCode;
        public List<Category> mCategories;

        public CategoryParserResponse(String message, int statusCode, List<Category> categories) {
            mMessage = message;
            mStatusCode = statusCode;
            mCategories = categories;
        }

    }
}
