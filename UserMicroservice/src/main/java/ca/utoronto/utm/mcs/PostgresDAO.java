package ca.utoronto.utm.mcs;

import java.sql.*;
import java.util.Arrays;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * This class handles requests sent to the PostgresSQL database
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class PostgresDAO {
	public Connection conn;
    public Statement st;

	public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://localhost:5432/root";
		try {
            Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * This method handles registering a user into the database
     *
     * @param email the email of the new user
     * @param password the password of the new user
     * @param name the name of the new user
     * @throws SQLException in case of error
     */
    public void registerUser(String email, String password, String name) throws SQLException{
        String query = "INSERT INTO users (uid, email, password, name, rides, is_driver, availableCoupons, redeemedCoupons) " +
                "VALUES ('0', '%s', '%s', '%s', '0', 'false', 'NULL', 'NULL');";
        query = String.format(query, email, password, name);
        this.st.executeUpdate(query);
    }

    /**
     * This method logs users into the database
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return the result of the login
     * @throws SQLException in case of error
     */
    public ResultSet loginUser(String email, String password) throws SQLException{
        String query = "SELECT 1 FROM users WHERE email='%s' AND password='%s'";
        query = String.format(query, email, password);
        return this.st.executeQuery(query);
    }

    /**
     * This method gets users by email
     *
     * @param email the email given
     * @return the users information
     * @throws SQLException in case of error
     */
	public ResultSet getUsersFromEmail(String email) throws SQLException {
		String query = "SELECT * FROM users WHERE email = '%s'";
        query = String.format(query, email);
        return this.st.executeQuery(query);
	}

    /**
     * This method gets users from user id
     *
     * @param uid the user id
     * @return the users information
     * @throws SQLException in case of error
     */
    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    /**
     * This method gets user data from userid
     *
     * @param uid the user id
     * @return the users data
     * @throws SQLException in case of error
     */
    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver,availableCoupons, redeemedCoupons FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    /**
     * This method updates the users information
     *
     * @param uid the user who's information is being updated
     * @param email the new email
     * @param password the new password
     * @param prefer_name the new prefered name
     * @param rides the new rides
     * @param isDriver the new driver field
     * @param availableCoupons the new availableCoupon field
     * @param redeemedCoupons the new redeemedCoupon field
     * @throws SQLException
     */
    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver, Integer[] availableCoupons, Integer[] redeemedCoupons) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
        if (availableCoupons != null) {
            query = ("UPDATE users SET availablecoupons = '%s' WHERE uid = %d");
            query = String.format(query, Arrays.toString(availableCoupons), uid).replace('[', '{').replace(']', '}');
            this.st.execute(query);
        }
        if (redeemedCoupons != null) {
            query = ("UPDATE users SET redeemedcoupons = '%s' WHERE uid = %d");
            query = String.format(query, Arrays.toString(redeemedCoupons), uid).replace('[', '{').replace(']', '}');
            this.st.execute(query);
        }
    }
}
