#################################################
## demo 1: classification using iris data #######
#################################################

from sklearn import datasets, cross_validation, metrics

from pyspark.mllib.classification import NaiveBayes
from pyspark.mllib.regression import LabeledPoint

iris_data = datasets.load_iris()

X = iris_data.data
Y = iris_data.target

X_train, X_test, Y_train, Y_test = cross_validation.train_test_split(X, Y, random_state=0, test_size=0.4)

# https://spark.apache.org/docs/latest/api/python/pyspark.mllib.html#pyspark.mllib.classification.NaiveBayesModel

iris_train_data = zip(Y_train, X_train.tolist())

# create rdd from a list
iris_train_data_rdd = sc.parallelize(iris_train_data)
iris_train_data_rdd.take(5)

iris_train_rdd = iris_train_data_rdd.map(lambda (k,v): LabeledPoint(k,v))
iris_train_rdd.take(5)

model = NaiveBayes.train(iris_train_rdd)


# use model to predict individual flowers
model.predict([2.3,1.6,6.2,2.7])
model.predict([6.2,3.2,4.1,0.3])


# use model to predict testing samples
test_rdd = sc.parallelize(X_test)
Y_predict = model.predict(test_rdd).collect()

# score
metrics.accuracy_score(Y_test, Y_predict)




#################################################
## demo 2: classification using IBM stock data ##
#################################################
from pandas.io.data import DataReader
from datetime import datetime
from pyspark.mllib.regression import LabeledPoint
from pyspark.mllib.classification import LogisticRegressionWithSGD
import numpy as np

df = DataReader('IBM',  'yahoo', datetime(2013,1,1), datetime(2016,1,1))

# predict next day is bull or bear
# create target: assign 1 if price goes up on this day, otherwise 0
df['target'] = (df.Close - df.Open).apply(lambda x: x > 0).astype(int)


# create 2 features
# f1: difference between next day's openning price and today's closing price
# f2: difference between volumns
lst_open = df.Open.tolist()
lst_close = df.Close.tolist()
lst_volume = df.Volume.tolist()

lst_f1 = np.array(lst_open[1:]) - np.array(lst_close[:-1])
lst_f2 = np.array(lst_volume[1:]) - np.array(lst_volume[:-1])

# remove first row
df = df[1:]

df['f1'] = lst_f1
df['f2'] = lst_f2

# save data
file_path = 'ibm.csv'
df.to_csv(file_path)

# create rdd from text file
ibm_rdd = sc.textFile(file_path)
ibm_rdd.take(5)

# remove header
header = ibm_rdd.first()
ibm_data_rdd = ibm_rdd.filter(lambda x: x != header) \
				.map(lambda x: x.split(',')) \
				.map(lambda x: LabeledPoint(x[7],[x[8],x[9]]))

ibm_data_rdd.take(5)


# train and test model for 10 times
lst_score = []
for i in range(10):
	ibm_train_rdd, ibm_test_rdd = ibm_data_rdd.randomSplit([.6,.4])
	lrm = (LogisticRegressionWithSGD.train(ibm_train_rdd,
											iterations=100, 
											step=1.0, 
											miniBatchFraction=1.0, 
											initialWeights=None, 
											regParam=0.01, 
											regType='l2'))
	lst_predicted = (ibm_test_rdd.map(lambda x: x.features)
								.map(lambda x: lrm.predict(x))
								.collect())
	lst_truth = ibm_test_rdd.map(lambda x: x.label).collect()
	score = metrics.accuracy_score(lst_truth,lst_predicted)
	lst_score.append(score)

print np.mean(lst_score)




#################################################
## demo 3: recommender system using ALS #########
#################################################

import json
from pyspark.mllib.recommendation import ALS

# use steam_data file and create a dictionary
def loadAppInfo(raw_string):
	data = json.loads(raw_string)
	steam_appid = data.get('steam_appid')
	name = data.get('name')
	return (steam_appid, name)

file_path = 'steam_data.txt'
app_rdd = sc.textFile(file_path).map(loadAppInfo)
dict_app_name = app_rdd.collectAsMap()
set_appid = set(dict_app_name.keys())

# how to read each line in the raw data
def readFile(raw_string):
	data = json.loads(raw_string)
	userId = data.keys()[0]
	lst_inventories = data.values()[0]
	lst_game_hour_pairs = []
	if (lst_inventories == []) or (lst_inventories == None):
		pass
	else:
		for game in lst_inventories:
			playtime_forever = game.get('playtime_forever')
			if playtime_forever == 0:
				pass
			else:
				appid = game.get('appid')
				if appid in set_appid:
					lst_game_hour_pairs.append((int(appid), playtime_forever))
				else:
					pass
	return (int(userId), lst_game_hour_pairs)



file_path = 'drUserInventory.txt'
playtime_rdd = sc.textFile(file_path).map(readFile)
playtime_rdd.first()

# replace userID with index, because original id is too big and ALS model doesn't support bigint
inventory_w_index_rdd = playtime_rdd.zipWithIndex()
inventory_w_index_rdd.first()

# prepare training rdd
inventory_rdd = inventory_w_index_rdd.map(lambda ((userId, lst_pairs),index): (index, lst_pairs))
inventory_rdd.first()

training_rdd = inventory_rdd.flatMapValues(lambda x: x) \
				.map(lambda (user_index, (appid, playtime_forever)): (user_index, appid, playtime_forever))
training_rdd.take(5)


model = ALS.train(training_rdd, 3, seed=0)

# recommend top 10 games for user no. 1300
top_10 = model.recommendProducts(1300,10)
for i in top_10: i


# but... index is useless, we need human readable info
# here is the top 10 games recommended for this user
for i in top_10:
	productId = i.product
	print dict_app_name.get(int(productId)), i.rating



