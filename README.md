# SciTweets
------------
Find the site on <a href="http://www.scitweets.com">scitweets.com</a>

## Twitter4j.properties
A twitter4j.properties file is required for this project. The reason it's not included is because it contains sensitive information such as the accesstoken and other things that the app requires to access the twitter APIs. The file is set up as follows:
```
debug=false
oauth.consumerKey=***************************
oauth.consumerSecret=***************************
oauth.accessToken=***************************
oauth.accessTokenSecret=***************************
tweetModeExtended=true
```
It is placed in the resources folder.

## Aylien.properties
This app uses Aylien for analysing link tweets. In order to use it, create an Aylien.properties file and fill in your information. This is placed in the resources folder.
```
APP_ID=***********
KEY=**************************
```

## AdminCredentials.properties
In order to use the admin controls for this website, you need to create an AdminCredentials.properties file and put it in the resources folder. The file should look like this:
```
Username=[username]
Password=[password]
```
This file can be placed next to the Aylien.properties file in the resources folder with the Filter.txt

## DBCredentials.properties
This file is included in github. However, you will need to change your username and password as well as the db url to match whatever you have setup.

## scitweets_model.properties
This file stores the disk location of the saved DNNClassifier model. You will need to edit it based on where this project is downloaded.

## Setting up the database
A sql file that will create an empty database is uploaded. 

## scitweets_info
This folder contains files that aren't used in the website itself, but were used to create SciTweets. The following folders and files within scitweets_info are listed:
  * `SciTweetsClassifier`:
    * `scitweets_classifier.py`: This program contains Python code for training, evaluating, and finally saving the DNN classfier so it can be used by Java in the main website. I used the TensorFlow estimator API to write this code.
    * `scitweets_train.csv`: This is a list of data from 200 URLs. It includes word count, average word length, the number of words in the article that correspond with the words in the FilterWords.txt file, and the percentage of those same words. It also contains a results column to tell the Neural Net 
    * `urllist.txt`: Contains a list of URLs that correspond with the data in scitweets_train.csv
    * `scitweets_test.csv`: A similar dataset to scitweets_train.csv, except it contains data for different URLs and is much smaller. It was used for evaluating the DNN classifier.
    * `urltestlist.txt`: Contains a list of URLs that corresponds with the data in scitweets_test.csv
    * `FeatureColumns.txt`: This file just has the definitions for the abbreviations used in the CSV files.
  * `scitweetsdb.sql`: The sql file mentioned in the previous section.

## Modifying the filter
An easy way to change the bahaviour of this webapp is to modify the wordlist contained in Filter.txt. By adding words to it, the filter will be more sensitive to scientific articles. However, if the changes are significant enough, it is also possible that you might need to retrain the the neural net so that it accounts for these changes.

However, another way to change the filter would be to modify the neural net itself.  There are countless ways to modify the neural net, from taking in new or different features, to changing the number of hidden layers and neurons in those hidden layers. However, if you do change the neural net, there will be several changes that you will need to make SciTweets itself for the changes to take effect:
 1. Change the `saved_model` folder located in `src/main/resources` with the new one created by your modifed model. You will be able to find the new one in the path defined in the `classifier.export_savedmodel()` function.
 2. Change the input and output tensor names in `.fetch()` and `.feed()` functions in the Filter class. You can find the new input and output names by inspecting them using `saved_model_cli` in the command line. For more information on this, check [this link](https://www.tensorflow.org/programmers_guide/saved_model#cli_to_inspect_and_execute_savedmodel) out.
 3. Change the `preProcessing()` and `checkArticle()` functions in the Filter class appropriately to fit whatever features you may have changed.

## Credits
Marc D'Mello - Wrote the backend (Java), set up the database, some HTML and javascript <br/>
Adith Arun - Wrote the CSS, some HTML
