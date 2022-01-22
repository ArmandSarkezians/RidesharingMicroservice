package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class handles the MongoDB
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class MongoDao {
    private MongoCollection<Document> collection;

    private final String username = "root";
    private final String password = "123456";
    private final String dbName = "trips";

    public MongoDao() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        String uriDb = String.format("mongodb://localhost:27017", username, password);

        MongoClient mongoClient = MongoClients.create(uriDb);
        MongoDatabase database = mongoClient.getDatabase(this.dbName);
        this.collection = database.getCollection(this.dbName);
    }

    /**
     * This method creates a trip
     *
     * @param driverUID the driver on the trip
     * @param passengerUID the passenger on the trip
     * @param startTime the start time of the trip
     * @return the trip ID
     */
    public String postTripConfirm(String driverUID, String passengerUID, int startTime){
        Document trip = new Document()
                .append("distance", "")
                .append("totalCost", 0)
                .append("startTime", startTime)
                .append("endTime", 0)
                .append("timeElapsed", 0)
                .append("driver", driverUID)
                .append("driverPayout", 0)
                .append("passenger", passengerUID)
                .append("discount", 0);
        collection.insertOne(trip);
        ObjectId id = (ObjectId)trip.get("_id");
        return id.toString();
    }

    /**
     * This method updates a trip
     *
     * @param id the trip being updated
     * @param body a JSON object with all the fields needed (I would to them separately, but I got lazy)
     * @return true if ok
     * @throws JSONException
     */
    public boolean updateTrip(String id, JSONObject body) throws JSONException{
        Document trip = new Document()
                .append("distance", body.getInt("distance"))
                .append("endTime", body.getInt("endTime"))
                .append("timElapsed", body.getInt("timeElapsed"))
                .append("discount", body.getDouble("discount"))
                .append("totalCost", body.getDouble("totalCost"))
                .append("driverPayout", body.getDouble("driverPayout"));
        UpdateResult result = collection.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", trip));
        return result.getModifiedCount() == 1;
    }

    /**
     * This function finds all trips of on passenger
     * @param id the passenger for whom all trips are being found
     * @return a list of all trips
     */
    public FindIterable<Document> passengerTrips(String id){
        return collection.find(new Document("passenger", id));
    }

    /**
     * This function finds all trips of on driver
     * @param id the driver for whom all trips are being found
     * @return a list of all trips
     */
    public FindIterable<Document> driverTrips(String id){
        return collection.find(new Document("driver", id));
    }

    /**
     * This method funds the driver given a tripId
     *
     * @param tripId the trip being searched
     * @return the driver id
     */
    public String findDriver(String tripId){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(tripId));
        MongoCursor<Document> cursor = collection.find(query).iterator();

        try{
            String driverId = "";
            while(cursor.hasNext()){
                Document str = cursor.next();
                driverId = (String)str.get("driver");
            }
            return driverId;
        }catch(Exception e){e.printStackTrace();}
        return "";
    }

    /**
     * This method funds the passenger given a tripId
     *
     * @param tripId the trip being searched
     * @return the passenger id
     */
    public String findPassenger(String tripId){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(tripId));
        MongoCursor<Document> cursor = collection.find(query).iterator();

        try{
            String passengerId = "";
            while(cursor.hasNext()){
                Document str = cursor.next();
                passengerId = (String)str.get("passenger");
            }
            return passengerId;
        }catch(Exception e){e.printStackTrace();}
        return "";
    }

}
