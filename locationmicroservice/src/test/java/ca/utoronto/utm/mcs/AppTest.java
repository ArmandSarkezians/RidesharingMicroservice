package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * This class is for testing the location microservice endpoints
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class AppTest {

   @Test
   public void getNearbyDriverPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("uid", "123").put("is_driver", "false");
      Utils.sendRequest("http://localhost:8000/location/user", "PUT", user.toString());

      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/location/nearbyDriver/123?radius=0.0", "GET", "");
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void getNearbyDriverFail() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("uid", "123").put("is_driver", "false");
      Utils.sendRequest("http://localhost:8000/location/user", "PUT", user.toString());

      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/location/nearbyDriver/", "GET", "");
      assertEquals(400, confirmRes.statusCode());
   }

   @Test
   public void getNavigationPass() throws JSONException, IOException, InterruptedException {
      JSONObject driverUser = new JSONObject().put("uid", "Jeff").put("is_driver", "true");
      Utils.sendRequest("http://localhost:8000/location/user", "PUT", driverUser.toString());
      JSONObject passengerUser = new JSONObject().put("uid", "Bob").put("is_driver", "false");
      Utils.sendRequest("http://localhost:8000/location/user", "PUT", passengerUser.toString());

      JSONObject roadOne = new JSONObject().put("roadName", "Bayview").put("hasTraffic", "false");
      Utils.sendRequest("http://localhost:8000/location/road", "PUT", roadOne.toString());
      JSONObject roadTwo = new JSONObject().put("roadName", "Yonge").put("hasTraffic", "false");
      Utils.sendRequest("http://localhost:8000/location/road", "PUT", roadTwo.toString());

      JSONObject routeOne = new JSONObject().put("roadName1", "Bayview").put("roadName2", "Yonge")
              .put("hasTraffic", "false").put("time", "2");
      Utils.sendRequest("http://localhost:8000/location/hasRoute", "POST", routeOne.toString());
      JSONObject routeTwo = new JSONObject().put("roadName1", "Yonge").put("roadName2", "Bayview")
              .put("hasTraffic", "false").put("time", "3");
      Utils.sendRequest("http://localhost:8000/location/hasRoute", "POST", routeTwo.toString());

      JSONObject driverUpdate = new JSONObject().put("longitude", "0.0").put("latitude", "0.0").put("street", "Bayview");
      Utils.sendRequest("http://localhost:8000/location/Jeff", "PATCH", driverUpdate.toString());
      JSONObject passengerUpdate = new JSONObject().put("longitude", "0.0").put("latitude", "0.0").put("street", "Yonge");
      Utils.sendRequest("http://localhost:8000/location/Bob", "PATCH", passengerUpdate.toString());


      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/location/navigation/Jeff?passengerUid=Bob", "GET", "");
      System.out.println(confirmRes);
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void getNavigationFail() throws IOException, InterruptedException {
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/location/navigtion/", "GET", "");
      assertEquals(404, confirmRes.statusCode());
   }
}
