package dbconnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import properties.RetrieveProperties;

//this class contains all the functions that interact with the database
public class DBConnect {

	static RetrieveProperties rp = new RetrieveProperties();
	
	//connects to the database, used in all of the following functions
	private static Connection dbconnect()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = DriverManager.getConnection(rp.getDBURL(), rp.getDBUsername(), rp.getDBPass());
		return conn;
	}
	
	//inserts the url, summary, tweet author, and an isValid bool which says if the link leads to a valid study
	public static void insertIntoLinkcache(String url, String summary, int authorID, boolean isValid)
			throws SQLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = dbconnect();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO linkcache (url, summary, authorId, isValid) VALUES (?,?,?,?)");
		ps.setString(1, url);
		ps.setString(2, summary);
		ps.setInt(3, authorID);
		ps.setBoolean(4, isValid);
		ps.execute();
		conn.close();
	}
	
	//gets the summary of a study from the database given the url of that study
	public static String[] selectFromLinkcache(String url)
			throws SQLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = dbconnect();
		PreparedStatement ps = conn.prepareStatement("SELECT url, summary FROM linkcache WHERE url=?");
		ps.setString(1, url);
		ResultSet resultset = ps.executeQuery();
		String[] results = new String[2];
		//fills the results array with the results and returns it to the calling function
		if (!resultset.next()) {
			conn.close();
			return null;
		} else {
			results[0] = resultset.getString("url");
			results[1] = resultset.getString("summary");
			conn.close();
			return results;
		}
	}

	//checks the isValid column based on the url and returns the true or false accordingly
	public static boolean checkIsValid(String url)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Connection conn = dbconnect();
		PreparedStatement ps = conn.prepareStatement("SELECT isValid FROM linkcache WHERE url=?");
		ps.setString(1, url);
		ResultSet resultset = ps.executeQuery();
		while (resultset.next()) {
			boolean isValid = resultset.getBoolean("isValid");
			if (isValid) {
				conn.close();
				return true;
			}
		}
		conn.close();
		return false;
	}

	//inserts a new Twitter user into a table of users that appear on the website
	public static void insertIntoHandles(String name, String username, String category)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Connection conn = dbconnect();
		//gets the ID associated with the category from the 'category' table, which is used to link the handles with the categories
		PreparedStatement getCategoryID = conn.prepareStatement("SELECT CategoryID FROM category WHERE CategoryName=?");
		getCategoryID.setString(1, category);
		ResultSet resultset = getCategoryID.executeQuery();
		resultset.next();
		int CategoryID = resultset.getInt("CategoryID");
		PreparedStatement ps = conn.prepareStatement("INSERT INTO handles (name, username, CategoryID) VALUES (?,?,?)");
		ps.setString(1, name);
		ps.setString(2, username);
		ps.setInt(3, CategoryID);
		ps.execute();
		conn.close();
	}

	//removes the handles from the 'handles' table
	public static void deleteHandle(String username)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Connection conn = dbconnect();
		PreparedStatement deleteFromHandle = conn.prepareStatement("DELETE FROM handles WHERE username=?");
		deleteFromHandle.setString(1, username);
		deleteFromHandle.execute();
		conn.close();
	}
	
	//gets all the handles and returns it in an ArrayList of String arrays
	public static List<String[]> selectAllFromHandles()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Connection conn = dbconnect();
		Statement statement = conn.createStatement();
		ResultSet resultset = statement.executeQuery("SELECT name, username, CategoryID FROM handles");
		ArrayList<String[]> results = new ArrayList<String[]>();
		//creates and fills the row array and adds it to the results ArrayList
		while (resultset.next()) {
			String[] row = new String[3];
			row[0] = resultset.getString("name");
			row[1] = resultset.getString("username");
			row[2] = Integer.toString(resultset.getInt("CategoryID"));
			results.add(row);
		}
		conn.close();
		return results;
	}
	
	//gets the authorId associated with a username
	public static int selectAuthorId(String username)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Connection conn = dbconnect();
		PreparedStatement ps = conn.prepareStatement("SELECT Id FROM handles WHERE username=?");
		ps.setString(1, username);
		ResultSet resultset = ps.executeQuery();
		resultset.next();
		int Id = resultset.getInt("Id");
		conn.close();
		return Id;
	}

	//selects all the categories from the 'category' table
	public static List<String[]> selectFromCategory()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Connection conn = dbconnect();
		Statement statement = conn.createStatement();
		ResultSet resultset = statement.executeQuery("SELECT CategoryID, CategoryName FROM category");
		ArrayList<String[]> results = new ArrayList<String[]>();
		//creates and fills row array containing each row of the table, than adds the array to the results ArrayList
		while (resultset.next()) {
			String[] row = new String[2];
			row[0] = Integer.toString(resultset.getInt("CategoryID"));
			row[1] = resultset.getString("CategoryName");
			results.add(row);
		}
		conn.close();
		return results;
	}
	
	//deletes a category from the 'category' table
	public static void deleteCategory(String category)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Connection conn = dbconnect();
		PreparedStatement deleteCategory = conn.prepareStatement("DELETE FROM category WHERE CategoryName=?");
		deleteCategory.setString(1, category);
		deleteCategory.execute();
		conn.close();
	}

	//checks to make sure a category exists, returns true if it does, false if it doesn't
	public static boolean categoryExists(String category)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Connection conn = dbconnect();
		String query = "SELECT 1 FROM category WHERE CategoryName=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, category);
		ResultSet resultset = ps.executeQuery();
		//if result.next() returns true, that meanst the results aren't empty and the category exists
		if (resultset.next()) {
			conn.close();
			return true;
		} else {
			conn.close();
			return false;
		}
	}
	
	//inserts a new category and generates a category ID associated with it
	public static void insertIntoCategory(String category)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		Connection conn = dbconnect();
		Statement statement = conn.createStatement();
		//gets a list of all the category ID's
		ResultSet resultset = statement.executeQuery("SELECT CategoryID FROM category");
		int lastInt = 0;
		//finds the last categoryID (which will also be the highest)
		while (resultset.next()) {
			lastInt = resultset.getInt("CategoryID");
		}
		//creates new categoryID by adding one to the current highest ID number
		int newInt = lastInt + 1;
		//inserts the name inputted into the function along with the associated ID
		PreparedStatement ps = conn.prepareStatement("INSERT INTO category (CategoryID, CategoryName) VALUES (?,?)");
		ps.setInt(1, newInt);
		ps.setString(2, category);
		ps.execute();
		conn.close();
	}
}
