package adminControls;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import properties.RetrieveProperties;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/adminlogin")
//admin login serves the adminlogin.jsp
public class AdminLoginServlet extends HttpServlet {
	
	static RetrieveProperties rp = new RetrieveProperties();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/adminlogin.jsp").forward(request, response);
	}

	//a post request is sent to this servlet when the form on the JSP is entered
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		//checks the username and password against the file read by the object 'rp', file is stored in the resources folder
		if (username.equals(rp.getAdminUsername()) && password.equals(rp.getAdminPassword())) {
			//puts the username in the session so that the filter will allow all requests through
			request.getSession().setAttribute("username", username);
			request.setAttribute("errorMessage", "Success");
			request.getRequestDispatcher("/admincontrol").forward(request, response);
		} else {
			request.setAttribute("errorMessage", "Invalid Credentials");
			request.getRequestDispatcher("/WEB-INF/views/adminlogin.jsp").forward(request, response);
		}
	}
}
