package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the NearbyDriver endpoint
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class Nearby extends Endpoint{

    /**
     * Get /location/nearbyDriver/:uid?radius=
     *
     * Gets a list of drivers that are in a radius defined by the user
     * @param r is the uid
     * @return 200, 400, 404, 500
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
            String uid = params[3].substring(0, params[3].indexOf("?"));
            Result locationOfUser = this.dao.getUserLocationByUid(uid);
            if (locationOfUser.hasNext()) {
                Record userLocation = locationOfUser.next();
                // FROM getUserLocationByUid
                double longitudeLocation = userLocation.get("n.longitude").asDouble();
                double latitudeLocation = userLocation.get("n.latitude").asDouble();

                //From URI
                double radius = Double.parseDouble(params[3].substring(params[3].indexOf("=") + 1));
                Result result = this.dao.getNearbyDrivers(latitudeLocation, longitudeLocation, radius);

                if(result.hasNext()){
                    JSONObject res = new JSONObject();
                    JSONObject data = new JSONObject();

                    List<Record> columns = result.list();
                    System.out.println(columns);

                    int count = 0;
                    while (count < columns.size()){
                        if (!columns.get(count).toString().contains("ELSE 0")){
                            String array = columns.get(count).toString().substring(columns.get(count).toString().lastIndexOf("["), columns.get(count).toString().lastIndexOf("]") + 1);
                            System.out.println(array.substring(1, array.indexOf(",")));
                            Double longitude = Double.parseDouble(array.substring(1, array.indexOf(",")));
                            Double latitude = Double.parseDouble(array.substring(array.indexOf(",") + 1, array.lastIndexOf(",")));
                            String street = array.substring(array.indexOf("\"") + 1, array.lastIndexOf("\""));

                            data.put(String.valueOf(count), String.format("longitude: %f, latitude: %f, street: %s", longitude, latitude, street));
                        }
                        count++;
                    }

                    res.put("status", "OK");
                    res.put("data", data);

                    this.sendResponse(r, res, 200);
                    return;
                }else {
                    this.sendResponse(r, new JSONObject("{}"), 200);
                    return;
                }
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
