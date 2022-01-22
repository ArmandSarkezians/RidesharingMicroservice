package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * This class creates the endpoints for each context
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public abstract class Endpoint implements HttpHandler {
    public Neo4jDAO dao;
    public HashMap<Integer, String> errorMap;

    public Endpoint() {
        this.dao = new Neo4jDAO();
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
	    errorMap.put(403, "FORBIDDEN");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    /**
     * This function handles the user requests
     * @param r the HTTPExchange request
     */
    public void handle(HttpExchange r) {
        try {
            switch (r.getRequestMethod()) {
            case "GET":
                this.handleGet(r);
                break;
            case "PATCH":
                this.handlePatch(r);
                break;
            case "POST":
                this.handlePost(r);
                break;
            case "PUT":
                this.handlePut(r);
                break;
            case "DELETE":
                this.handleDelete(r);
                break;
            default:
                this.sendStatus(r, 405);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function writes data to the output stream
     *
     * @param r the HTTPExchange send by the user
     * @param response the response given by the microservice
     * @throws IOException in case of error
     */
    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * This function sends a response to the user
     *
     * @param r the HTTPExchange send by the user
     * @param obj the object to be sent to the user
     * @param statusCode the status code of the microservice
     * @throws JSONException in case of error
     * @throws IOException in case of error
     */
    public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
        obj.put("status", errorMap.get(statusCode));
        String response = obj.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    /**
     * This function sends a status to the user
     *
     * @param r the HTTPExchange send by the user
     * @param statusCode the status code of the microservice
     * @throws JSONException in case of error
     * @throws IOException in case of error
     */
    public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    /**
     * This function sends a status to the user, with an EmptyData condition
     *
     * @param r the HTTPExchange send by the user
     * @param statusCode the status code of the microservice
     * @param hasEmptyData in case the status has empty data
     * @throws JSONException in case of error
     * @throws IOException in case of error
     */
    public void sendStatus(HttpExchange r, int statusCode, boolean hasEmptyData) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        res.put("data", new JSONObject());
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    /**
     * This function validates the fields given
     *
     * @param JSONRequest The request being checked
     * @param stringFields The string field being checked
     * @param integerFields The integer field being checked
     * @return false if error, true if no error
     */
    public boolean validateFields(JSONObject JSONRequest, ArrayList<String> stringFields, ArrayList<String> integerFields) {
		try {
			return validateFieldsHelper(JSONRequest,stringFields, String.class) && validateFieldsHelper(JSONRequest, integerFields, Integer.class);
		} catch (JSONException e) {
			System.err.println("Caught Exception: " + e.getMessage());
			return false;
		}
	}

    /**
     * This function check if fields are in JSONRequest and make sure they're of type classOfFields
     *
     * @param JSONRequest the request being checked
     * @param fields the field being checked
     * @param classOfFields the class of fields being checked
     * @return false if error, true if no error
     * @throws JSONException in case of error
     */
	private boolean validateFieldsHelper(JSONObject JSONRequest, ArrayList<String> fields, Class<?> classOfFields) throws JSONException {
		for (String field : fields) {
			if (!(JSONRequest.has(field) && JSONRequest.get(field).getClass().equals(classOfFields))) {
				return false;
			}
		}
		return true;
	}

    /**
     * This function handles the GET method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    public void handleGet(HttpExchange r) throws IOException, JSONException {};

    /**
     * This function handles the PATCH method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    public void handlePatch(HttpExchange r) throws IOException, JSONException {};

    /**
     * This function handles the POST method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    public void handlePost(HttpExchange r) throws IOException, JSONException {};

    /**
     * This function handles the PUT method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    public void handlePut(HttpExchange r) throws IOException, JSONException {};

    /**
     * This function handles the DELETE method
     *
     * @param r the HTTPExchange being handled
     * @throws IOException in case of error
     * @throws JSONException in case of error
     */
    public void handleDelete(HttpExchange r) throws IOException, JSONException {};


} 
