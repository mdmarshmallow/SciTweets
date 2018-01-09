package tweets;

import java.util.List;

import com.aylien.textapi.TextAPIException;

import dbconnection.DBConnect;
import filter.Filter;
import summarize.SummarizeService;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//class that interfaces with the Twitter API
public class TweetService {
	private static List<STweet> tweets = new ArrayList<STweet>();
	private static TwitterFactory tf = new TwitterFactory();
	private static Twitter twitter = tf.getInstance();
	private String url;

	//retrieves the timeline of a user given the username
	public List<STweet> retrieveTweets(String username) throws IOException, SQLException, TextAPIException,
			TwitterException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (!tweets.isEmpty()) {
			tweets.clear();
		}
		User user = twitter.showUser(username);
		long userID = user.getId();
		Paging p = new Paging();
		//only gets the past 100 tweets in the timeline
		p.setCount(100);
		List<Status> statuses = twitter.getUserTimeline(userID, p);
		List<String> urlsOnPage = new ArrayList<String>();
		List<String> summariesOnPage = new ArrayList<String>();
		//goes through each status
		for (Status status : statuses) {
			//checks for a url
			if (Filter.hasURL(status)) {
				//gets the url of the tweet
				url = status.getURLEntities()[0].getExpandedURL();
				/*checks if the url isn't already 'on the page' (not really on the page but put in the ArrayList that
				gets sent to the page)*/
				if (!urlsOnPage.contains(url)) {
					//deletes the second url of the tweet
					String statusText = ModifyTweet.deleteSecondURL(status.getText());
					//gets the date created and turns it into two ints to put in the STweet object
					Date date = status.getCreatedAt();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					int month = calendar.get(Calendar.MONTH);
					int year = calendar.get(Calendar.YEAR);
					int authorId = DBConnect.selectAuthorId(username);
					//checks if the url is already in the database
					String description = "";
					if (DBConnect.selectFromLinkcache(url) != null) {
						//gets the summary stored in the database and the isValid boolean
						description = DBConnect.selectFromLinkcache(url)[1];
						boolean isValid = DBConnect.checkIsValid(url);
						/*if isValid and the summary isn't already on the page, the tweets is 
						added to the list of tweets to display*/
						if (isValid && !summariesOnPage.contains(description)) {
							tweets.add(new STweet(user.getName(), statusText, url, 
									      description, month, year));
							//these two are arrays that help avoid duplicate tweets/urls/summaries
							urlsOnPage.add(url);
							summariesOnPage.add(description);
						}
					//if it isn't in the database, runs the tweet through the filter
					} else if (Filter.checkTweet(url)) {
						//gets a 4 sentence long summary
						description = SummarizeService.summarize(url, 4);
						/*if the summary isn't empty and the tweets that will be displayed don't already
						have the same summary*/
						if (description != null && description.split(" ").length != 0 && 
						    !summariesOnPage.contains(description)) {
							//adds the information to the ArrayList
							tweets.add(new STweet(user.getName(), statusText, url, description, month, year));
							//stores the information in the database
							DBConnect.insertIntoLinkcache(url, description, authorId, true);
							//these two are arrays that help avoid duplicate tweets/urls/summaries
							urlsOnPage.add(url);
							summariesOnPage.add(description);
						} else {
							//puts puts the url in the database and marks inValid as false
							DBConnect.insertIntoLinkcache(url, null, authorId, false);
						}
					} else {
						//puts puts the url in the database and marks inValid as false
						DBConnect.insertIntoLinkcache(url, null, authorId, false);
					}
				}
			}
		}
		if (tweets.isEmpty()) {
			//if there were no valid tweets associated with the handle
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			tweets.add(new STweet(null, null, null, "There seems to be no valid tweets :(", month, year));
		}
		return tweets;
	}
}
