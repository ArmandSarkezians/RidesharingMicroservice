package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * This class handles the Request endpoint
 */
public class Request extends Endpoint {

    /**
     * This function handes the POST method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        try{
            if(body.has("uid") && body.has("radius")){
                HttpResponse<String> response = Utils.sendRequest(String.format("/location/nearbyDriver/%s?radius=%s",
                        body.get("uid"), body.get("radius")), "GET", "{}");
                JSONObject drivers = new JSONObject(response.body()).getJSONObject("data");
                this.sendResponse(r, drivers, 200);
            }else{
                this.sendStatus(r, 400);
            }
        }catch(Exception e){
            this.sendStatus(r, 500);
        }
    }
}
