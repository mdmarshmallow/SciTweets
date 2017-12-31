package trainingsetConstructor;

import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVWriter;

public class TrainingSetConstructor {
	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/scitweetsdb?useSSL=false",
					"scitweets", "scitweets");
			Statement statement = conn.createStatement();
			ResultSet rset = statement.executeQuery("SELECT url, isValid FROM linkcache ORDER BY RAND()");
			int i = 0;
			int positiveURLCounter = 0;
			boolean valid = false;
			try (Writer writer = Files.newBufferedWriter(Paths.get("scitweets_test.csv"), StandardCharsets.UTF_8);
					Writer urlWriter = Files.newBufferedWriter(Paths.get("urltestlist.txt"), StandardCharsets.UTF_8);
					CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR,
							CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
							CSVWriter.DEFAULT_LINE_END)) {
				csvWriter.writeNext(new String[] { "200", "4", "SWC", "WC", "AWL", "SWD", "isValid" });
				while (i < 20) {
					if (rset.next()) {
						valid = rset.getBoolean("isValid");
						if (i > 148 && positiveURLCounter < 50) {
							if (!valid) {
								continue;
							}
						}
						try {
							String url = rset.getString("url");
							Document webpage = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true)
									.validateTLSCertificates(false).get();
							String article = webpage.body().text();
							String[] wcArray = article.split("\\s+");
							float articleWC = wcArray.length;
							ClassLoader cs = new TrainingSetConstructor().getClass().getClassLoader();
							File filterWordsFile = new File(cs.getResource("FilterWords.txt").getFile());
							Scanner scan = new Scanner(filterWordsFile);
							float wordCounter = 0;
							while (scan.hasNextLine()) {
								String word = scan.nextLine();
								if (article.toLowerCase().contains(word.toLowerCase())) {
									wordCounter++;
								}
							}
							wordCounter = wordCounter - 6;
							scan.close();
							String characterCounter = article.replaceAll("\\s+", "");
							float numCharacters = characterCounter.length();
							float meanWordLength = numCharacters / articleWC;
							float sciWordDensity = wordCounter / articleWC;
							System.out.println("\n");
							System.out.println(url);
							System.out.println("Number of scientific words: " + wordCounter);
							System.out.println("Word Count: " + articleWC);
							System.out.println("Average word length: " + meanWordLength);
							System.out.println("Density of scientific words: " + sciWordDensity);
							System.out.println("Valid: " + valid);
							System.out.println(article);
							int validInt = valid ? 1 : 0;
							csvWriter.writeNext(new String[] { Float.toString(wordCounter), Float.toString(articleWC),
									Float.toString(meanWordLength), Float.toString(sciWordDensity),
									Integer.toString(validInt) });
							urlWriter.write("\n" + url);
							i++;
							if (valid) {
								positiveURLCounter++;
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
