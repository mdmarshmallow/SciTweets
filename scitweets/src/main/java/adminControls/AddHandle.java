package adminControls;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbconnection.DBConnect;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/addhandle")
//Adds a handle via the admin control page
public class AddHandle extends HttpServlet {

	private static TwitterFactory tf = new TwitterFactory();
	private static Twitter twitter = tf.getInstance();

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String usernameToAdd = request.getParameter("username");
		String[] checkFormat = usernameToAdd.split("");
		//Checks if the handle starts with an '@'
		if (checkFormat[0].equals("@")) {
			try {
				//The .showUser function is used here to activate the catch loop if a 404 is returned
				twitter.showUser(usernameToAdd);
				//Gets the category field from the add handle form
				String category = request.getParameter("category");
				String name = request.getParameter("name");
				//runs a check against the database to see if the category exists
				if (DBConnect.categoryExists(category)) {
					DBConnect.insertIntoHandles(name, usernameToAdd, category);
				} else {
					//if category doesn't exist, create the category in the database and insert the handle under the new category
					DBConnect.insertIntoCategory(category);
					DBConnect.insertIntoHandles(name, usernameToAdd, category);
				}
				request.getRequestDispatcher("/admincontrol").forward(request, response);
			} catch (TwitterException | InstantiationException | IllegalAccessException | ClassNotFoundException
					| SQLException e) {
				//runs if handle does not exist
				if (((TwitterException) e).getStatusCode() == 404) {
					request.setAttribute("errorAddingHandle", "Handle does not exist");
					request.getRequestDispatcher("/admincontrol").forward(request, response);
				}
			}
		} else {
			request.setAttribute("errorAddingHandle", "Please add an @ sign to the beginning of the username");
			request.getRequestDispatcher("/admincontrol").forward(request, response);
		}
	}
}
