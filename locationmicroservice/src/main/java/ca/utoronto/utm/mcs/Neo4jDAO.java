package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * This class handles interactions with the database
 *
 * @version 1.0
 * @author Armand Sarkezians (kinda but not really)
 */
public class Neo4jDAO {

    private final Session session;
    private final Driver driver;

    private final String username = "neo4j";
    private final String password = "123456";

    public Neo4jDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        String uriDb = "bolt://localhost:7687";

        this.driver = GraphDatabase.driver(uriDb, AuthTokens.basic(this.username, this.password));
        this.session = this.driver.session();
    }

    /**
     * This function is for testing, it cleans the database after every test
     *
     */
    public void cleanDatabase(){
        this.session.run("MATCH (n) DETACH DELETE n");
    }

    /**
     * This function adds a user to the database
     *
     * @param uid the uid of the user being added
     * @param is_driver true if user is a driver, false if not
     * @return result of the addition
     */
    public Result addUser(String uid, boolean is_driver) {
        String query = "CREATE (n: user {uid: '%s', is_driver: %b, longitude: 0, latitude: 0, street: ''}) RETURN n";
        query = String.format(query, uid, is_driver);
        return this.session.run(query);
    }

    /**
     * This function deletes a user from the database
     *
     * @param uid the uid of the user being deleted
     * @return result of the deletion
     */
    public Result deleteUser(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) DETACH DELETE n RETURN n";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    /**
     * This function gets the users location by the uid
     * @param uid the uid of the user
     * @return the location of the user
     */
    public Result getUserLocationByUid(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) RETURN n.longitude, n.latitude, n.street";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    /**
     * This function gets the user by their uid
     * @param uid the uid of the user
     * @return the user
     */
    public Result getUserByUid(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) RETURN n";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    /**
     * This function sets a user to be a driver
     * @param uid the user
     * @param isDriver the updated driver variable
     * @return the result of the update
     */
    public Result updateUserIsDriver(String uid, boolean isDriver) {
        String query = "MATCH (n:user {uid: '%s'}) SET n.is_driver = %b RETURN n";
        query = String.format(query, uid, isDriver);
        return this.session.run(query);
    }

    /**
     * This function updates a users location
     *
     * @param uid the user being updated
     * @param longitude the new longitude
     * @param latitude the new latitude
     * @param street the new street
     * @return the result of the update
     */
    public Result updateUserLocation(String uid, double longitude, double latitude, String street) {
        String query = "MATCH(n: user {uid: '%s'}) SET n.longitude = %f, n.latitude = %f, n.street = \"%s\" RETURN n";
        query = String.format(query, uid, longitude, latitude, street);
        return this.session.run(query);
    }

    /**
     * This function gets the road
     *
     * @param roadName the road being gotten
     * @return the road
     */
    public Result getRoad(String roadName) {
        String query = "MATCH (n :road) where n.name='%s' RETURN n";
        query = String.format(query, roadName);
        return this.session.run(query);
    }

    /**
     * This function creates a road
     *
     * @param roadName the new road name
     * @param has_traffic if the road has traffic or not
     * @return the result of the creation
     */
    public Result createRoad(String roadName, boolean has_traffic) {
        String query = "CREATE (n: road {name: '%s', is_traffic: %b}) RETURN n";
        query = String.format(query, roadName, has_traffic);
        return this.session.run(query);
    }

    /**
     * This function updates a road
     *
     * @param roadName the road name
     * @param has_traffic the new traffic
     * @return the result of the update
     */
    public Result updateRoad(String roadName, boolean has_traffic) {
        String query = "MATCH (n:road {name: '%s'}) SET n.is_traffic = %b RETURN n";
        query = String.format(query, roadName, has_traffic);
        return this.session.run(query);
    }

    /**
     * This function creates a new route
     *
     * @param roadname1 the start road
     * @param roadname2 the end road
     * @param travel_time the travel time between the roads
     * @param has_traffic if the road has traffic or not
     * @return the result of the creation
     */
    public Result createRoute(String roadname1, String roadname2, int travel_time, boolean has_traffic) {
        String query = "MATCH (r1:road {name: '%s'}), (r2:road {name: '%s'}) CREATE (r1) -[r:ROUTE_TO {travel_time: %d, is_traffic: %b}]->(r2) RETURN type(r)";
        query = String.format(query, roadname1, roadname2, travel_time, has_traffic);
        return this.session.run(query);
    }

    /**
     * This function deletes a route
     *
     * @param roadname1 The starting road
     * @param roadname2 The ending road
     * @return the result of the deletion
     */
    public Result deleteRoute(String roadname1, String roadname2) {
        String query = "MATCH (r1:road {name: '%s'})-[r:ROUTE_TO]->(r2:road {name: '%s'}) DELETE r RETURN COUNT(r) AS numDeletedRoutes";
        query = String.format(query, roadname1, roadname2);
        return this.session.run(query);
    }

    /**
     * This function gets all nearby drivers to a user
     *
     * @param latitude the latitude of the user
     * @param longitude the longitude of the user
     * @param radius the radius to look for drivers
     * @return a list of all nearby drivers
     */
    public Result getNearbyDrivers(double latitude, double longitude, double radius){
        String query = "MATCH (n:user) \n" +
                "WHERE n.is_driver = true\n" +
                "WITH \n" +
                "    point({latitude: n.latitude, longitude: n.longitude}) AS p1,\n" +
                "    point({latitude: %f, longitude: %f}) AS p2,\n" +
                "    n AS n\n" +
                "RETURN \n" +
                "CASE\n" +
                "    WHEN distance(p1, p2) < %f THEN [n.latitude, n.longitude, n.street]\n" +
                "    ELSE 0\n" +
                "END";
        query = String.format(query, latitude, longitude, radius);
        return this.session.run(query);
    }

    /**
     * This function gets the quickest path from a driver to a user
     *
     * @param driverID the drivers id
     * @param passengerID the passengers id
     * @return the quickest route from the driver to the passenger
     */
    public Result getQuickestPath(String driverID, String passengerID){
        String query = "MATCH(driver:user {uid:\"%s\"}), (passenger:user {uid:\"%s\"})\n" +
                "MATCH(driverRoad:road{name: driver.street}), (passengerRoad:road{name:passenger.street})\n" +
                "MATCH p = (driverRoad)-[:ROUTE_TO*]->(passengerRoad)\n" +
                "WITH p, reduce (time = 0, r IN relationships(p) | time+r.travel_time) AS totalTime\n" +
                "RETURN p, totalTime ORDER BY totalTime ASC LIMIT 1";
        query = String.format(query, driverID, passengerID);
        return this.session.run(query);
    }

} 