package filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//wrapper for the Twitter API
import twitter4j.Status;
import twitter4j.URLEntity;

//this class has all the functions that identify if a Tweet contains a study
public class Filter {

	//checks to see if a tweet has a url using the Twitter4j URLEntity object and the getURLEntities function
	public static boolean hasURL(Status status) {
		URLEntity[] url = status.getURLEntities();
		if (url.length != 0) {
			return true;
		} else {
			return false;
		}
	}

	//function retrieves the article and returns it as an ArrayList for easier analysis
	private static List<String> retrieveArticle(String urlInput) throws IOException {
		final String SPECIALCHAR_REGEX = "[^a-z0-9 ]";
		try {
			Document webpage = Jsoup.connect(urlInput).timeout(10 * 1000).ignoreContentType(true)
					.validateTLSCertificates(false).get();
			String article = webpage.body().toString();
			String[] parsedArticle = Jsoup.parse(article).toString().split("\\s+");
			Pattern p = Pattern.compile(SPECIALCHAR_REGEX, Pattern.CASE_INSENSITIVE);
			List<String> cleanedArticle = new ArrayList<String>();
			for (String word : parsedArticle) {
				Matcher m = p.matcher(word);
				if (!m.find()) {
					cleanedArticle.add(word);
				}
			}
			return cleanedArticle;
		/*multiple catches for each error, not needed right now but will make it easier if error handling is implemented
			in the future for any of these errors*/
		//I found that this error is returned when there is a paywall on the article
		} catch (HttpStatusException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("paywall");
			return InvalidArticle;
		//when Jsoup takes too long to connect to the url
		} catch (SocketTimeoutException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("timeout");
			return InvalidArticle;
		//I'm not really sure what causes a SocketException, hence the generic 'badURL'
		} catch (SocketException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("badURL");
			return InvalidArticle;
		//Again, not really sure what happens with an SSL handshake
		} catch (SSLHandshakeException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("SSLHandshakeException");
			return InvalidArticle;
		//catch for all other exceptions
		} catch (Exception e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("Other Issue");
			return InvalidArticle;
		}
	}

	//this is where the filtering actually happens, it takes in the article in the form of an ArrayList
	private static boolean checkArticle(List<String> article) throws FileNotFoundException {
		List<String> filterWords = new ArrayList<String>();
		List<String> tempArray = new ArrayList<String>();
		int matchCounter = 0;
		//gets the FilterWords.txt file which contains words that would occur in a study
		ClassLoader cs = new Filter().getClass().getClassLoader();
		File filterWordsFile = new File(cs.getResource("FilterWords.txt").getFile());
		Scanner scan = new Scanner(filterWordsFile);
		//loads the FilterWords.txt file into an ArrayList we can use
		while (scan.hasNextLine()) {
			String word = scan.nextLine();
			if (!word.isEmpty() || !word.contains("<")) {
				filterWords.add(word);
			}
		}
		scan.close();
		//cross checks each word in the article to the words in the FilterWords
		for (String wordInArticle : article) {
			/*takes out all the spaces in and converts all the letters to lowercase, this will make sure that
			false negatives won't occur*/
			wordInArticle = wordInArticle.toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9]", "");
			//the if loop stops repeated words from being checked each time the word is repeated
			if (!tempArray.contains(wordInArticle)) {
				tempArray.add(wordInArticle);
				//goes through all the words in the filter and cross checks it
				for (String wordInFilter : filterWords) {
					wordInFilter.toLowerCase().replaceAll("\\s+", "");
					if (wordInArticle.equalsIgnoreCase(wordInFilter)) {
						matchCounter++;
						break;
					}
				}
			}
			if (matchCounter > 7) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkTweet(String urlInput) throws IOException {
		List<String> article = retrieveArticle(urlInput);
		if (article.size() != 0) {
			if (!article.get(0).equals("paywall") && !article.get(0).equals("timeout")
					&& !article.get(0).equals("badURL") && !article.get(0).equals("SSLHandshakeException")
					&& !article.get(0).equals("Other Issue")) {
				return checkArticle(article);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
