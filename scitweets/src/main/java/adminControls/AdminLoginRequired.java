package adminControls;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = { "/admincontrol", "/adminlogout", "/addhandle", "/removehandle" })
//filter is applied to all the servlets that have to do with admin control
public class AdminLoginRequired implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		/*if the username is in the session, it will allow the request to go through, if not, the user will be 
		forwarded to the admin login page*/
		if (request.getSession().getAttribute("username") != null) {
			chain.doFilter(servletRequest, servletResponse);
		} else {
			request.getRequestDispatcher("/adminlogin").forward(servletRequest, servletResponse);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
