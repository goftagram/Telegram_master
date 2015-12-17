package com.goftagram.telegram.goftagram.parser;



import com.goftagram.telegram.goftagram.application.model.Tag;

import org.json.JSONException;
import org.json.JSONObject;

public class TagParser {


    public static final String ID               = "id";
    public static final String TAG              = "tag";


    public static Tag parser(String jsonTag) throws JSONException {
        Tag tag = parser(new JSONObject(jsonTag));
        return tag;
    }

    public static Tag parser(JSONObject jsonTag) throws JSONException {
        Tag tag = new Tag();
        tag.setServerId(jsonTag.getString(ID));
        tag.setName(jsonTag.getString(TAG));
        return tag;
    }
}
