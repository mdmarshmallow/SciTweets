package filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.SSLHandshakeException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import properties.RetrieveProperties;
//wrapper for the Twitter API
import twitter4j.Status;
import twitter4j.URLEntity;

//this class has all the functions that identify if a Tweet contains a study
public class Filter {

	static RetrieveProperties rp = new RetrieveProperties();

	// checks to see if a tweet has a url using the Twitter4j URLEntity object
	// and the getURLEntities function
	public static boolean hasURL(Status status) {
		URLEntity[] url = status.getURLEntities();
		if (url.length != 0) {
			return true;
		} else {
			return false;
		}
	}

	// function retrieves the article and returns it as an ArrayList for easier
	// analysis
	private static String retrieveArticle(String urlInput) throws IOException {
		try {
			Document webpage = Jsoup.connect(urlInput).timeout(10 * 1000).ignoreContentType(true)
					.validateTLSCertificates(false).get();
			String article = webpage.body().text();
			return article;
			/*
			 * multiple catches for each error, not needed right now but will
			 * make it easier if error handling is implemented in the future for
			 * any of these errors
			 */
			// I found that this error is returned when there is a paywall on
			// the article
		} catch (HttpStatusException e) {
			return "paywall";
			// when Jsoup takes too long to connect to the url
		} catch (SocketTimeoutException e) {
			return "timeout";
			// I'm not really sure what causes a SocketException, hence the
			// generic 'badURL'
		} catch (SocketException e) {
			return "badURL";
			// Again, not really sure what happens with an SSL handshake
		} catch (SSLHandshakeException e) {
			return "SSLHandshakeException";
			// catch for all other exceptions
		} catch (Exception e) {
			return "Other Issue";
		}
	}

	// this function gets information from the article to put into the DNN classifier
	private static Map<String, Float> preProcessing(String article) throws FileNotFoundException {
		// splits the article into an array then gets the word count
		String[] wcArray = article.split("\\s+");
		float articleWC = wcArray.length;
		// loads the FilterWord.txt file to be used
		ClassLoader cs = new Filter().getClass().getClassLoader();
		File filterWordsFile = new File(cs.getResource("FilterWords.txt").getFile());
		Scanner scan = new Scanner(filterWordsFile);
		// checks how many words in the article are on the FilterWords file
		float wordCounter = 0;
		while (scan.hasNextLine()) {
			String word = scan.nextLine();
			if (article.toLowerCase().contains(word.toLowerCase()) && !word.isEmpty()) {
				wordCounter++;
			}
		}
		scan.close();
		// finds the average word length
		String characterCounter = article.replaceAll("\\s+", "");
		float numCharacters = characterCounter.length();
		float meanWordLength = numCharacters / articleWC;
		// finds what percent of the article is scientific
		float sciWordDensity = wordCounter / articleWC;
		// creates a mapping and returns it
		Map<String, Float> data = new HashMap<String, Float>();
		// SWC = scientific word count
		data.put("SWC", wordCounter);
		// WC = the article word count
		data.put("WC", articleWC);
		// AWL = the average word length
		data.put("AWL", meanWordLength);
		// SWD = the scientific word density
		data.put("SWD", sciWordDensity);
		return data;
	}

	// runs the the data from the map through a DNN Classifier (trained in TensorFlow)
	private static boolean checkArticle(Map<String, Float> data) throws IOException {
		try (SavedModelBundle bundle = SavedModelBundle.load(rp.getSciTweetsModelPath(), "serve")) {
			Session session = bundle.session();
			//creates tensors to input into the model
			Tensor<?> SWC = Tensor.create(new long[] { 1, 1 }, FloatBuffer.wrap(new float[] { data.get("SWC") }));
			Tensor<?> WC = Tensor.create(new long[] { 1, 1 }, FloatBuffer.wrap(new float[] { data.get("WC") }));
			Tensor<?> AWL = Tensor.create(new long[] { 1, 1 }, FloatBuffer.wrap(new float[] { data.get("AWL") }));
			Tensor<?> SWD = Tensor.create(new long[] { 1, 1 }, FloatBuffer.wrap(new float[] { data.get("SWD") }));
			//feeds each tensor in and retrieves the output
			List<Tensor<?>> outputs = session.runner().feed("Placeholder_2:0", AWL).feed("Placeholder:0", SWC)
					.feed("Placeholder_3:0", SWD).feed("Placeholder_1:0", WC)
					.fetch("dnn/head/predictions/probabilities:0").run();
			float[][] scores = new float[1][2];
			outputs.get(0).copyTo(scores);
			System.out.println(scores[0][1]);
			//maps the output to true or false
			int finalScore = Math.round(scores[0][1]);
			return (finalScore == 1) ? true : false;
		}
	}

	// checks if the article has returned anything, and if so, runs it through the checkArticle function
	public static boolean checkTweet(String urlInput) throws IOException {
		String article = retrieveArticle(urlInput);
		if (article.length() != 0) {
			if (!article.equals("paywall") && !article.equals("timeout") && !article.equals("badURL")
					&& !article.equals("SSLHandshakeException") && !article.equals("Other Issue")) {
				Map<String, Float> dataMapping = preProcessing(article);
				return checkArticle(dataMapping);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
