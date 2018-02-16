import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

/**
  * a simple spark app in Scala
  */

object ScalaApp {
  def main(args: Array[String]) {
    val configuration = new SparkConf()
      .setAppName("simple app")
      .setMaster("local")
    val sc = new SparkContext(configuration)

    val spark = SparkSession.builder().getOrCreate()

    val data = spark.read.format("libsvm")
      .load("/Users/soichi/Projects/spark-environment/code/data/sample_multiclass_classification_data.txt")

    val splits = data.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)
    val layers = Array[Int](4, 5, 4, 3)

    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(128)
      .setSeed(1234L)
      .setMaxIter(100)

    val model = trainer.fit(train)
    val result = model.transform(test)
    val predictionAndLabels = result.select("prediction", "label")
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")

    println("test set accuracy = " + evaluator.evaluate(predictionAndLabels))
  }
}
