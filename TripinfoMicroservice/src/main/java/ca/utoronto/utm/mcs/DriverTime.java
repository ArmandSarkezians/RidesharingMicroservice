package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * This class handles the DriverTime endpoint for the trip microservice
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class DriverTime extends Endpoint{

    /**
     * This function handles the GET method
     *
     * @param r The HTTPExchange
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        try{
            String tripId = params[3];
            String driverId = this.dao.findDriver(tripId);
            String passengerId = this.dao.findPassenger(tripId);

            HttpResponse<String> response = Utils.sendRequest(String.format("/location/navigation/%s?passengerUid=%s",
                    driverId, passengerId), "GET", "{}");
            JSONObject time = new JSONObject(response.body()).getJSONObject("data");
            this.sendResponse(r, time, 200);
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}