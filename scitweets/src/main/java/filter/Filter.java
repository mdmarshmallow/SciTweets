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

import twitter4j.Status;
import twitter4j.URLEntity;

public class Filter {

	public static boolean hasURL(Status status) {
		URLEntity[] url = status.getURLEntities();
		if (url.length != 0) {
			return true;
		} else {
			return false;
		}
	}

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
		} catch (HttpStatusException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("paywall");
			return InvalidArticle;
		} catch (SocketTimeoutException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("timeout");
			return InvalidArticle;
		} catch (SocketException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("badURL");
			return InvalidArticle;
		} catch (SSLHandshakeException e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("SSLHandshakeException");
			return InvalidArticle;
		} catch (Exception e) {
			List<String> InvalidArticle = new ArrayList<String>();
			InvalidArticle.add("Other Issue");
			return InvalidArticle;
		}
	}

	private static boolean checkArticle(List<String> article) throws FileNotFoundException {
		List<String> filterWords = new ArrayList<String>();
		List<String> tempArray = new ArrayList<String>();
		int matchCounter = 0;
		ClassLoader cs = new Filter().getClass().getClassLoader();
		File filterWordsFile = new File(cs.getResource("FilterWords.txt").getFile());
		Scanner scan = new Scanner(filterWordsFile);
		while (scan.hasNextLine()) {
			String word = scan.nextLine();
			if (!word.isEmpty() || !word.contains("<")) {
				filterWords.add(word);
			}
		}
		scan.close();
		for (String wordInArticle : article) {
			wordInArticle = wordInArticle.toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9]", "");
			if (!tempArray.contains(wordInArticle)) {
				tempArray.add(wordInArticle);
				for (String wordInFilter : filterWords) {
					wordInFilter.toLowerCase().replaceAll("\\s+", "");
					if (wordInArticle.equalsIgnoreCase(wordInFilter)) {
						matchCounter++;
						break;
					}
				}
			}
<<<<<<< HEAD
			if (matchCounter > 4) {
=======
			if (matchCounter > 7) {
>>>>>>> ae706e2fec0a13209acf8101b663362c00b06e1d
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
