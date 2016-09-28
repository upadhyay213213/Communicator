package webclient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import appconstant.AppConstatnts;
import appmanager.AppController;

/**
 * Created by nupadhay on 10/15/2015.
 */
public class RequestManager {

    public static RequestManager mRequestManager = new RequestManager();
    public RequestResponseInterface responseInterface;
    private String TAG = "requesttag";

    private RequestManager() {
    }

    public static RequestManager getInstance() {
        return mRequestManager;
    }


    public void LoginRequest(String userid, String password) {
        try {
            /**URL */
            String url = AppConstatnts.LOGIN_URL;
            JSONObject jsonObject = new JSONObject();
            if (userid.contains("@")) {
                jsonObject.put("email", userid);
                jsonObject.put("password", password);
            } else {
                jsonObject.put("username", userid);
                jsonObject.put("password", password);
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            responseInterface.responseListener(response,"Login");
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    responseInterface.errorListener(error);
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
            System.out.println("requestexecption" + e.getMessage());
        }
    }

    public void UpdateLocationRequest(String id, String token, String latitude, String longitude, final String calltype) {
        try {
            /**URL */
            String url = AppConstatnts.LOCATION_UPDATE + id + "?" + "token=" + token;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            responseInterface.responseListener(response,calltype);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    responseInterface.errorListener(error);
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
            System.out.println("requestexecption" + e.getMessage());
        }
    }

    public void requestAssistance(String token, String userid, String latitude, String longitude, final String callType) {
        try {
            /**URL */
            String url = AppConstatnts.LOCATION_ASSISTANCE + token;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("user_id", userid);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            responseInterface.responseListener(response,callType);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    responseInterface.errorListener(error);
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
            System.out.println("requestexecption" + e.getMessage());
        }
    }


    public void pushNotificationRequest(String token, String deviceToken, String deviceType, String username, String userid, final String callType) {
        try {
            /**URL */
            String url = AppConstatnts.UPDATE_PUSH_TOKEN + token;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_token", deviceToken);
            jsonObject.put("device_type", "android");
            jsonObject.put("information", username);
            jsonObject.put("user_id", userid);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            responseInterface.responseListener(response,callType);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    responseInterface.errorListener(error);
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
            System.out.println("requestexecption" + e.getMessage());
        }


    }


    public void RequestMessage(String id, String token, final String callType) {
        try {
            /**URL */
            String url = AppConstatnts.GET_MESSAGE + id +"/messages"+ "?" + "token=" + token;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            responseInterface.responseListener(response,callType);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    responseInterface.errorListener(error);
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
            System.out.println("requestexecption" + e.getMessage());
        }
    }


    public void RequestMessageDetail(String id, String token,String messageID, final String callType) {
        try {
            /**URL */
            String url = AppConstatnts.GET_MESSAGE_DETAIL + id +"/message/" +messageID+ "?" + "token=" + token;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            responseInterface.responseListener(response,callType);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    responseInterface.errorListener(error);
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
            System.out.println("requestexecption" + e.getMessage());
        }
    }




}
