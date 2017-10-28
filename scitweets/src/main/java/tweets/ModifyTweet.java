package tweets;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyTweet {
	//the Twitter API returns the urls in the tweets again at the end of the tweet, this removes that second url
	public static String deleteSecondURL(String text){
		//the regex used by the pattermatcher to identify a url *thanks StackOverflow
		final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
		int URLCounter = 0;
		String finalText = null;
		Pattern p = Pattern.compile(URL_REGEX);
		List<String> textWord = new ArrayList<String>();
		//turns the tweet String int a String array
		String[] words = text.split("\\s+");
		//adds all the elements in the words array into the textWord ArrayList
		for(int i = 0; i < words.length; i++){
			textWord.add(words[i]);
		}
		//goes through the entire textWord ArrayList
		for(String word : textWord){
			Matcher m = p.matcher(word);
			//if the word is identified as a url
			if(m.find()){
				URLCounter++;
				//it removes the second url found (there are never three urls in the tweets)
				if(URLCounter == 2){
					//removess the url and rejoins everything
					textWord.remove(word);
					finalText = String.join(",", textWord).replaceAll(",", " ");
					return finalText;
				}
			}
		}
		return text;
	}
}
