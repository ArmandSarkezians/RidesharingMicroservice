package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * This class is for testing the usermicroservice endpoints
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class AppTest {
   /* CODE TO CREATE A TABLE
   CREATE TABLE public.users(uid VARCHAR(128)NULL, name VARCHAR(128) NULL, email VARCHAR(128) NULL, password VARCHAR(128)
   NULL, rides int8 NULL, is_driver bool NULL, availableCoupons VARCHAR(128) NULL, redeemedCoupons VARCHAR(128) NULL);

   CODE TO INSERT A USER:
   INSERT INTO users (uid, email, password, name, rides, is_driver, availableCoupons, redeemedCoupons)
   VALUES ('0', 'Jeff@gmail.com', '123', 'Jeff', '0', 'false', 'NULL', 'NULL');

   LOGIN:
   SELECT 1 FROM users WHERE email='%s' AND password='%s'
    */

   @Test
   public void userRegisterPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("name", "Jeff").put("email", "Jeff@gmail.com")
              .put("password", "123");
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/user/register",
              "POST", user.toString());
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void userRegisterFail() throws IOException, InterruptedException, JSONException {
      JSONObject user = new JSONObject().put("name", "Jeff").put("email", "Jeff@gmail.com");
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/user/register/",
              "POST", user.toString());
      assertEquals(400, confirmRes.statusCode());
   }


   @Test
   public void userLoginPass() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("name", "Bob").put("email", "Bob@gmail.com")
              .put("password", "1234");
      JSONObject login = new JSONObject().put("email", "Bob@gmail.com").put("password", "1234");
      Utils.sendRequest("http://localhost:8000/user/register", "POST", user.toString());
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/user/login", "POST", login.toString());
      assertEquals(200, confirmRes.statusCode());
   }

   @Test
   public void userLoginFail() throws JSONException, IOException, InterruptedException {
      JSONObject user = new JSONObject().put("name", "Bob").put("email", "Bob@gmail.com")
              .put("password", "1234");
      JSONObject login = new JSONObject().put("email", "Bob@gmail.com");
      Utils.sendRequest("http://localhost:8000/user/register", "POST", user.toString());
      HttpResponse<String> confirmRes = Utils.sendRequest("http://localhost:8000/user/login", "POST", login.toString());
      assertEquals(400, confirmRes.statusCode());
   }

}
