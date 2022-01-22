package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class implements the Driver endpoint for the trip microservice
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class Driver extends Endpoint{

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
            ArrayList<Document> drivers = new ArrayList<Document>();
            this.dao.driverTrips(id).into(drivers);
            if (drivers.size() > 0){
                this.sendResponse(r, new JSONObject().put("data", drivers), 200);
            }else{
                this.sendStatus(r, 404);
            }
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }
    }
}
