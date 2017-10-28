package newsengine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aylien.textapi.TextAPIException;

import handles.CategoryService;
import tweets.STweet;
import tweets.TweetService;
import twitter4j.TwitterException;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/newsengine")
//serves the newsengine.jsp
public class NewsEngineServlet extends HttpServlet {

	private TweetService tweetService = new TweetService();
	private CategoryService categoryService = new CategoryService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//sets sends the categories list and the method to the jsp
		request.setAttribute("categories", categoryService.retrieveCategories());
		request.setAttribute("method", "get");
		request.getRequestDispatcher("/WEB-INF/views/newsengine.jsp").forward(request, response);
	}

	//doPost is called when a handle is clicked on the page
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			//gets a list of the tweets associated with that user (they are filtered in the retrieveTweets function)
			List<STweet> tweetList = tweetService.retrieveTweets(request.getParameter("user"));
			//adds the tweets and the number of tweets 
			request.setAttribute("tweets", tweetList);
			request.setAttribute("numOfArticles", (tweetList).size());
			//gets the month and year of the oldest tweet on the list
			int month = tweetList.get(tweetList.size() - 1).getMonth();
			int year = tweetList.get(tweetList.size() - 1).getYear();
			//calculate months calculates the number of months from now and the first tweet
			int difference = calculateMonths(month, year);
			if (difference != 0){
				request.setAttribute("months", difference);
			} else {
				request.setAttribute("months", "");
			}
		} catch (SQLException | TextAPIException | TwitterException | InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			//no error handling again :(
			e.printStackTrace();
		}
		//resends the categories and the sends the name of the tweet's author back
		request.setAttribute("name", request.getParameter("name"));
		request.setAttribute("categories", categoryService.retrieveCategories());
		request.setAttribute("method", "post");
		request.getRequestDispatcher("/WEB-INF/views/newsengine.jsp").forward(request, response);
	}

	//function calculates the difference between the current month and the inputted month given the year
	private int calculateMonths(int month, int year) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentYear = calendar.get(Calendar.YEAR);
		//this is where the calculation happens
		int difference = (((currentYear - year) * 12) + currentMonth) - month;
		return difference;
	}
}
