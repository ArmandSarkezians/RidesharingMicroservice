package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the navigation endpoint
 *
 * @version 1.0
 * @authoer Armand Sarkezians
 */
public class Navigation extends Endpoint{
    /**
     * This function handles the GET method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of errors
     * @throws JSONException in case of errors
     */
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        //Check for invalid input, return code 400 if input is invalid
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }


        try{
            String driverUid = params[3].substring(0, params[3].indexOf("?"));
            String passengerUid = params[3].substring(params[3].indexOf("=") + 1);


            Result result = this.dao.getQuickestPath(driverUid, passengerUid);

            if (result.hasNext()) {
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                Record user = result.next();

                data.put("", user.toString());
                res.put("status", "OK");
                res.put("data", data);

                this.sendResponse(r, res, 200);
            } else {
                this.sendStatus(r, 404, true);
            }
            // 500 error code check, catches exception if GET fails
        }catch(Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
        this.sendStatus(r, 500, true);
        return;
    }
}
