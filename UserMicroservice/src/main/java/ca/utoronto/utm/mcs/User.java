package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.*;

/**
 * This class handed the User endpoint for the user microservice
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class User extends Endpoint {

	/**
	 * This function handles the GET method
	 *
	 * @param r the HTTPExchange being handled
	 * @throws IOException in case of error
	 * @throws JSONException in case of error
	 */
	@Override
	public void handleGet(HttpExchange r) throws IOException, JSONException {
		
		// check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400, true);
			return;
		}

		// check if uid given is integer, return 400 if not
		String uidString = splitUrl[2];
        int uid;
		try {
			uid = Integer.parseInt(uidString);
		} catch (Exception e) {
			this.sendStatus(r, 400, true);
			return;
		}

		// make query and get required data, return 500 if error
		ResultSet rs;
		boolean resultHasNext;
		try {
			rs = this.dao.getUserData(uid);
			resultHasNext = rs.next();
		} 
		catch (SQLException e) {
			this.sendStatus(r, 500, true);
			return;
		}

		// check if user was found, return 404 if not found
		if (!resultHasNext) {
			this.sendStatus(r, 404, true);
			return;
		}

		// get data
		String name;
		String email;
		int rides;
		Boolean isDriver;
		JSONArray availableCoupons;
		JSONArray redeemedCoupons;
		try {
			name = rs.getString("name");
			email = rs.getString("email");
			rides = rs.getInt("rides");
			isDriver = rs.getBoolean("isdriver");
			availableCoupons = new JSONArray(rs.getString("availableCoupons").replace("{", "[").replace("}", "]"));
			redeemedCoupons = new JSONArray(rs.getString("redeemedCoupons").replace("{", "[").replace("}", "]"));
		}
		catch (SQLException e) {
			this.sendStatus(r, 500, true);
			return;
		}

		// making the response
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("name", name);
		data.put("email", email);
		data.put("rides", rides);
		data.put("isDriver", isDriver);
		data.put("availableCoupons", availableCoupons);
		data.put("redeemedCoupons", redeemedCoupons);
		resp.put("data", data);

		this.sendResponse(r, resp, 200);
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
		
		// check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}

		// check if uid given is integer, return 400 if not
		String uidString = splitUrl[2];
        int uid;
		try {
			uid = Integer.parseInt(uidString);
		} catch (Exception e) {
			this.sendStatus(r, 400);
			return;
		}

		// make query to check if user with given uid exists, return 500 if error
		ResultSet rs1;
		boolean resultHasNext;
		try {
			rs1 = this.dao.getUsersFromUid(uid);
			resultHasNext = rs1.next();
		} 
		catch (SQLException e) {
			this.sendStatus(r, 500);
			return;
		}

		// check if user with given uid exists, return 404 if not
		if (!resultHasNext) {
			this.sendStatus(r, 404);
			return;
		}

		String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);
		
		String email = null;
		String name = null;
		String password = null;
		Boolean isDriver = null;
		Integer rides = null;
		Integer[] availableCoupons = null;
		Integer[] redeemedCoupons = null;

		// check what values are present
		if (deserialized.has("email")) { 
			if (deserialized.get("email").getClass() != String.class) {
				this.sendStatus(r, 400);
				return;
			}
			email = deserialized.getString("email");
		}
		if (deserialized.has("name")) {
			if (deserialized.get("name").getClass() != String.class) {
				this.sendStatus(r, 400);
				return;
			}
			name = deserialized.getString("name");
		}
		if (deserialized.has("password")) {
			if (deserialized.get("password").getClass() != String.class) {
				this.sendStatus(r, 400);
				return;
			}
			password = deserialized.getString("password");
		}
		if (deserialized.has("isDriver")) { 
			if (deserialized.get("isDriver").getClass() != Boolean.class) {
				this.sendStatus(r, 400);
				return;
			}
			isDriver = deserialized.getBoolean("isDriver");
		}
		if (deserialized.has("rides")) {
			if (deserialized.get("rides").getClass() != Integer.class) {
				this.sendStatus(r, 400);
				return;
			}
			rides = deserialized.getInt("rides");
		}
		if (deserialized.has("redeemedCoupons") && deserialized.get("redeemedCoupons").getClass() == JSONArray.class) {
			JSONArray tempjson = deserialized.getJSONArray("redeemedCoupons");
			Integer[] tempint = new Integer[tempjson.length()];
			for (int i = 0; i < tempjson.length(); i++) {
				if (tempjson.get(i).getClass() != Integer.class) {
					this.sendStatus(r, 400);
					return;
				}
				else {
					tempint[i] = (int)tempjson.get(i);
				}
			}
			redeemedCoupons = tempint;
		}
		if (deserialized.has("availableCoupons") && deserialized.get("availableCoupons").getClass() == JSONArray.class) {
			JSONArray tempjson = deserialized.getJSONArray("availableCoupons");
			Integer[] tempint = new Integer[tempjson.length()];
			for (int i = 0; i < tempjson.length(); i++) {
				if (tempjson.get(i).getClass() != Integer.class) {
					this.sendStatus(r, 400);
					return;
				} 
				else {
					tempint[i] = (int)tempjson.get(i);
				}
			}
			availableCoupons = tempint;
		}

		// if all the variables are still null then there's no variables in request so retrun 400
		if (email == null && name == null && password == null && isDriver == null && rides == null && availableCoupons == null && redeemedCoupons == null) {
			this.sendStatus(r, 400);
			return;
		}

		// update db, return 500 if error
		try {
			this.dao.updateUserAttributes(uid, email, password, name, rides, isDriver, availableCoupons, redeemedCoupons);
		}
		catch (SQLException e) {
			this.sendStatus(r, 500);
			return;
		}

		// return 200 if everything is updated without error
		this.sendStatus(r, 200);
	}

	/**
	 * This function handles the POST method
	 *
	 * @param r the HTTPExchange being handled
	 * @throws IOException in case of error
	 * @throws JSONException in case of error
	 */
	@Override
	public void handlePost(HttpExchange r) throws IOException, JSONException {
		JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
		String url = r.getRequestURI().getPath();

		try{
			//Register case
			if (url.contains("register") && body.has("email") && body.has("password") && body.has("name")){
				this.dao.registerUser(body.getString("email"), body.getString("password"), body.getString("name"));
				this.sendStatus(r, 200, true);
			//Login case
			}else if (url.contains("login") && body.has("email") && body.has("password")){
				ResultSet result = this.dao.loginUser(body.getString("email"), body.getString("password"));
				boolean resultHasNext  = result.next();
				if (resultHasNext){
					this.sendStatus(r, 200, true);
				}else{
					this.sendStatus(r, 500, true);
				}
			//Bad input
			}else{
				this.sendStatus(r, 400, true);
			}
		}catch(Exception e){
			System.out.println(e);
			this.sendStatus(r, 500, true);
		}
	}
}