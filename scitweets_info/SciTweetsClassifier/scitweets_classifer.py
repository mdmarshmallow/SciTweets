import tensorflow as tf
import pandas as pd

tf.logging.set_verbosity(tf.logging.INFO)


# loads the dataset into program
columns = ["SWC", "WC", "AWL", "SWD", "isValid"]
features = ["SWC", "WC", "AWL", "SWD"]
output = "isValid"
training_set = pd.read_csv(
    "scitweets_train.csv", skipinitialspace=True, skiprows=1, names=columns)
test_set = pd.read_csv(
    "scitweets_test.csv", skipinitialspace=True, skiprows=1, names=columns)


# define feature columns
feature_columns = []
for i in features:
    feature_columns.append(
        tf.feature_column.numeric_column(
            key=i))


# create classifier with hidden units 10, 20, 10 and saves it
classifier = tf.estimator.DNNClassifier(
    feature_columns=feature_columns,
    hidden_units=[10, 20, 10],
    model_dir="/scitweetsInfo/NeuralNetwork/scitweets_model")


# creates an input function that can be used with all the datasets
def get_input_fn(data_set, num_epochs, shuffle):
    """Generate and input function."""
    # creates a dict from the CSV file
    data = {}
    for j in features:
        data.update({j: data_set[j].values})
    # sets for input and output (x and y) in the input function
    x = pd.DataFrame(data)
    y = pd.Series(data_set[output].values)
    # creates input function
    input_fn = tf.estimator.inputs.pandas_input_fn(
        x=x, y=y, num_epochs=num_epochs, shuffle=shuffle)
    return input_fn


# trains the classifier in 10000 steps
classifier.train(input_fn=get_input_fn(
    training_set, num_epochs=None, shuffle=True), steps=10000)


# evaluates classifer and prints results
accuracy_score = classifier.evaluate(
    input_fn=get_input_fn(
        data_set=test_set, num_epochs=1, shuffle=False))["accuracy"]
print("\nTest Accuracy: {0:f}\n".format(accuracy_score))


# saves model so Java web application can access it
def serving_input_receiver_fn():
    """Build the serving inputs."""
    inputs = {}
    for k in features:
        inputs.update({k: tf.placeholder(shape=[None, 1], dtype=tf.float32)})
    return tf.estimator.export.ServingInputReceiver(inputs, inputs)


classifier.export_savedmodel(
    export_dir_base="/scitweetsInfo/NeuralNetwork/scitweetsJavaModel",
    serving_input_receiver_fn=serving_input_receiver_fn)
