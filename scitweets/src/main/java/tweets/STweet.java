package tweets;

//creates an object to hold all the the tweet information
public class STweet {
	private String username;
	private String tweet;
	private String url;
	private String description;
	private int month;
	private int year;

	public STweet(String username, String tweet, String url, String description, int month, int year) {
		super();
		this.username = username;
		this.tweet = tweet;
		this.url = url;
		this.description = description;
		this.month = month;
		this.year = year;
	}

	@Override
	public String toString() {
		return "STweet [username=" + username + ", tweet=" + tweet + ", url=" + url + ", description=" + description
				+ ", month=" + month + ", year=" + year + "]";
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
