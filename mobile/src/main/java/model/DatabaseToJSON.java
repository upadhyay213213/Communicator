package model;

import android.content.ClipData;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;

/**
 * Created by nupadhay on 9/20/2016.
 */
public class DatabaseToJSON {


    public DatabaseToJSON(Context context) {
          DatabaseHelper.init(context);
    }

    public JSONObject getJSON() throws JSONException {
        ArrayList<MessageResposneDatabase> item = null;
        JSONObject pl = new JSONObject();
        item = DataBaseQuery.getMessageResponse();
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<item.size();i++){
            JSONObject val = new JSONObject();
            try {
                val.put("id", item.get(i).getmID());
                val.put("name", item.get(i).getmSenderID());
                jsonArray.put(val);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        pl.put("data",jsonArray);

    return pl;
} }