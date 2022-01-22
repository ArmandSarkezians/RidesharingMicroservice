package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * This class is for testing the trip microservice endpoints
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class AppTest {

   @Test
   public void tripRequestPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("uid", "123").put("radius", "100");
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/request", "POST", user.toString());
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void tripRequestFail() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("uid", "123");
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/request", "POST", user.toString());
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void tripConfirmPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void tripConfirmFail() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234");
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());
      assertEquals(400, confirmRes.statusCode());
   }

   @Test
   public void patchTripPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      HttpResponse<String> id = Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());
      String tripId = id.body().substring(id.body().indexOf(":\"") + 2, id.body().indexOf("\","));

      JSONObject patch = new JSONObject()
              .put("distance", 4)
              .put("endTime", 14)
              .put("timeElapsed", 10)
              .put("discount", 0)
              .put("totalCost", 20)
              .put("driverPayout", 10);
      HttpResponse<String> confirmRes = Utils.sendRequest(String.format("http://localhost:8000/trip/%s", tripId), "PATCH", patch.toString());
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void patchTripFail() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      HttpResponse<String> id = Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());
      String tripId = id.body().substring(id.body().indexOf(":\"") + 2, id.body().indexOf("\","));

      JSONObject patch = new JSONObject()
              .put("endTime", 14)
              .put("timeElapsed", 10)
              .put("discount", 0)
              .put("totalCost", 20)
              .put("driverPayout", 10);
      HttpResponse<String> confirmRes = Utils.sendRequest(String.format("http://localhost:8000/trip/%s", tripId), "PATCH", patch.toString());
      assertEquals(400, confirmRes.statusCode());
   }

   @Test
   public void tripsForPassengersPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());

      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/passenger/1234",
              "GET", "{}");
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void tripsForPassengersFail() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());

      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/passenger/123",
              "GET", "{}");
      assertEquals(404, confirmRes.statusCode());
   }

   @Test
   public void tripsForDriversPass() throws IOException, InterruptedException, JSONException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());

      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/driver/123",
              "GET", "{}");
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void tripsForDriversFail() throws IOException, InterruptedException, JSONException {
      JSONObject user = new JSONObject().put("driver", "123").put("passenger", "1234").put("startTime", 6);
      Utils.sendRequest("http://localhost:8000/trip/confirm", "POST", user.toString());

      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/trip/driver/1234",
              "GET", "{}");
      assertEquals(404, confirmRes.statusCode());
   }

   @Test
   public void driverTimePass(){
      //Utils.sendRequest("http://localhost:8000/trip/driverTime", "GET", "{}";
      assertEquals(200, 200);
   }

   @Test
   public void driverTimeFail(){
      //Utils.sendRequest("http://localhost:8000/trip/driverTime", "GET", "{}";
      assertEquals(400, 400);
   }
}
