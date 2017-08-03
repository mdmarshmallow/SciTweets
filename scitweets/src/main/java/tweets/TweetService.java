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

public class TweetService {
	private static List<STweet> tweets = new ArrayList<STweet>();
	private static TwitterFactory tf = new TwitterFactory();
	private static Twitter twitter = tf.getInstance();
	private String url;

	public List<STweet> retrieveTweets(String username) throws IOException, SQLException, TextAPIException,
			TwitterException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (!tweets.isEmpty()) {
			tweets.clear();
		}
<<<<<<< HEAD
		try {
			User user = twitter.showUser(username);
			long userID = user.getId();
			List<Status> statuses = twitter.getUserTimeline(userID);
			for (Status status : statuses) {
				if (Filter.hasURL(status.getText())) {
					for (int i = 0; i < status.getURLEntities().length; i++) {
						url = status.getURLEntities()[i].getExpandedURL();
			
					}
					if(Filter.checkTweet(url)){
						tweets.add(new STweet(user.getName(), status.getText(), url, "Description to be added later"));
=======
		User user = twitter.showUser(username);
		long userID = user.getId();
		Paging p = new Paging();
		p.setCount(100);
		List<Status> statuses = twitter.getUserTimeline(userID, p);
		List<String> urlsOnPage = new ArrayList<String>();
		List<String> summariesOnPage = new ArrayList<String>();
		for (Status status : statuses) {
			if (Filter.hasURL(status)) {
				url = status.getURLEntities()[0].getExpandedURL();
				if (!urlsOnPage.contains(url)) {
					String statusText = ModifyTweet.deleteSecondURL(status.getText());
					if (DBConnect.selectFromLinkcache(url) != null) {
						String description = DBConnect.selectFromLinkcache(url)[1];
						if (!description.equals("Invalid") && !summariesOnPage.contains(description)) {
							tweets.add(new STweet(user.getName(), statusText, url, description));
							urlsOnPage.add(url);
							summariesOnPage.add(description);
						}
					} else if (Filter.checkTweet(url)) {
						String description = SummarizeService.summarize(url, 4);
						if (description != null && !description.isEmpty() && !summariesOnPage.contains(description)) {
							tweets.add(new STweet(user.getName(), statusText, url, description));
							DBConnect.insertIntoLinkcache(url, description);
							urlsOnPage.add(url);
							summariesOnPage.add(description);
						} else {
							DBConnect.insertIntoLinkcache(url, "Invalid");
						}
					} else if (!Filter.checkTweet(url)) {
						DBConnect.insertIntoLinkcache(url, "Invalid");
>>>>>>> ae706e2fec0a13209acf8101b663362c00b06e1d
					}
				}
			}
		}
		if (tweets.isEmpty()) {
			tweets.add(new STweet("", "", "", "There seems to be no valid tweets :("));
		}
		return tweets;
	}
}