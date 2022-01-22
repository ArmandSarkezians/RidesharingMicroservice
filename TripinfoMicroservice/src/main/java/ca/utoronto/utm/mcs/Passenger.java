package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * This class implements the passenger endpoint for the trip microservice
 */
public class Passenger extends Endpoint{

    /**
     * This function handles the GET method
     *
     * @param r the HTTPExchange being handled
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
            String id = params[3];
            ArrayList<Document> passengers = new ArrayList<Document>();
            this.dao.passengerTrips(id).into(passengers);
            if (passengers.size() > 0){
                this.sendResponse(r, new JSONObject().put("data", passengers), 200);
            }else{
                this.sendStatus(r, 404);
            }
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }
    }
}
