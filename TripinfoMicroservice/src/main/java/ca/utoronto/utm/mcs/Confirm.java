package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * This class implements the Confirm endpoint for the trip microservice
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class Confirm extends Endpoint{

    /**
     * This function handles the POST method
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        try{
            if (body.has("driver") && body.has("passenger") && body.has("startTime")){
                String driverID = body.getString("driver");
                String passengerID = body.getString("passenger");
                int startTime = body.getInt("startTime");

                String id = this.dao.postTripConfirm(driverID, passengerID, startTime);
                this.sendResponse(r, new JSONObject(String.format("{id: %s}", id)), 200);
            }else{
                this.sendStatus(r, 400);
            }
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }

    /**
     * This function handles the PATCH method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");

        if (params.length != 3 || params[2].isEmpty()) {
            System.out.println("HELLO2");
            this.sendStatus(r, 400, true);
            return;
        }

        try{
            String id = params[2];
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

            if (body.has("distance") && body.has("endTime") && body.has("timeElapsed") && body.has("discount")
                    && body.has("totalCost") && body.has("driverPayout")){
                this.dao.updateTrip(id, body);
                this.sendStatus(r, 200);
            }else{
                System.out.println("HELLO2");
                this.sendStatus(r, 400);
            }
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
