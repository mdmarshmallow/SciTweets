package adminControls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import handles.Account;
import handles.Category;
import handles.CategoryService;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/admincontrol")
//serves the admincontrol.jsp
public class AdminControlServlet extends HttpServlet {

	private CategoryService categoryService = new CategoryService();

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Category> categories = categoryService.retrieveCategories();
		List<Account> accountList = new ArrayList<Account>();
		//retrieves all the accounts from each category
		for(Category category : categories) {
			Account[] tempAccountArray = category.getAccounts();
			//adds the account array into an aggregate list of accounts to be forwarded to the jsp
			for (Account account : tempAccountArray) {
				accountList.add(account);
			}
		}
		//accounts sent to jsp, they are used in a list for the fields to make it easier for the administrator to find accounts 
		int numOfAccounts = accountList.size();
		request.setAttribute("accounts", accountList.toArray(new Account[numOfAccounts]));
		int listSize = categories.size();
		request.setAttribute("categories", categories.toArray(new Category[listSize]));
		request.getRequestDispatcher("/WEB-INF/views/admincontrol.jsp").forward(request, response);
	}
}
