package adminControls;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbconnection.DBConnect;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/removecategory")
public class RemoveCategory extends HttpServlet {
	
	//doesn't do anything if the request is a GET request except to forward the user to the admin control servlet (filter is still applied)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		request.getRequestDispatcher("/admincontrol").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			//uses the DBConnect class to remove the category from the database, then redirects to the admincontrol JSP
			DBConnect.deleteCategory(request.getParameter("category"));
			request.getRequestDispatcher("/admincontrol").forward(request, response);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
