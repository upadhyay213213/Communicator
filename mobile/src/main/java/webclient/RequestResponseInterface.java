package webclient;

import com.android.volley.VolleyError;

public interface RequestResponseInterface {

    public void responseListener(Object o,String callType);

    public void errorListener(VolleyError error);
}