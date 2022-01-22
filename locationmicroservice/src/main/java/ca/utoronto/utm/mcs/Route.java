package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;

/**
 * This function handles the Route endpoint
 *
 * @version 1.0
 * @author Armand Sarkezians (not really)
 */
public class Route extends Endpoint {

    /**
     * POST /location/hasRoute/
     * 
     * @body roadName1, roadName2, hasTraffic, time
     * @return 200, 400, 404, 500
     *         Create a connection from a road to another; making
     *         a relationship in Neo4j.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("roadName1") && body.has("roadName2") && body.has("hasTraffic") && body.has("time")) {
            String road1 = body.getString("roadName1");
            String road2 = body.getString("roadName2");
            Boolean is_traffic = body.getBoolean("hasTraffic");
            int time = body.getInt("time");

            Result result = this.dao.createRoute(road1, road2, time, is_traffic);
            if (!result.hasNext()) {
                this.sendStatus(r, 404);
                return;
            }
            this.sendResponse(r, new JSONObject(), 200);
            return;

        } else {
            this.sendStatus(r, 400);
            return;
        }
    }

    /**
     * DELETE /location/route/
     * 
     * @body roadName1, roadName2
     * @return 200, 400, 404, 500
     *         Disconnect a road with another; remove the
     *         relationship in Neo4j.
     */

    @Override
    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("roadName1") && body.has("roadName2")) {
            String road1 = body.getString("roadName1");
            String road2 = body.getString("roadName2");

            Result result = this.dao.deleteRoute(road1, road2);
            if (!result.hasNext()) {
                this.sendStatus(r, 404, true);
                this.sendStatus(r, 500);
                return;
            }
            this.sendResponse(r, new JSONObject(), 200);
            int numDeletedRoutes = result.next().get("numDeletedRoutes").asInt();
            if (numDeletedRoutes == 0) {
                this.sendStatus(r, 404);
                return;
            }

            this.sendStatus(r, 200);
            return;
        } else {
            this.sendStatus(r, 400, true);
            this.sendStatus(r, 400);
            return;
        }
    }
}
