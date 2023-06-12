import java.io.{File, PrintWriter}

import scala.io.BufferedSource

class Happy {
	type LAS = List[Array[String]]
  
  /**
  * Generate prolog file based on list files provided 
  * 
  **/
  def generatePrologFile(srcFileName: String, likes:LAS, sells:LAS, frequents:LAS, rates:LAS): Unit = {

    val writer = new PrintWriter(new File(srcFileName))

    //Prolog defining functions 
    writer.println(":- discontiguous frequents/2.")
    writer.println(":- discontiguous likes/2.")
    writer.println(":- discontiguous r_rating/2.")  
    writer.println(":- discontiguous sells/5.")

    var id = 0
    
    //Writing to prolog file from list structure
    sells.foreach(row => writer.println("sells(" + row(0) + ", " + row(1) + ", " + row(2) + "," + row(0).substring(0, 2) + row(1).substring(1, row(1).length - 1) + row(2) + "', " + row(0) + ")."))
    
    rates.foreach(row => writer.println("rates(" + row(0) + ", " + row(1) + ")."))

    frequents.foreach(row => row.tail.foreach(col => writer.println("frequents(" + row.head + ", " + col + ").")))

    likes.foreach(row => row.tail.foreach(col => writer.println("likes(" + row.head + ", " + col + ").")))
      
    writer.println()
    writer.close()
  }

  // --- Native method
  @native def callPrologHappyIndex(person: String, priceIndex: Int): Double
}

object Happy {
  def main(args: Array[String]): Unit = {
    if (args.length != 5) {
      println("Usage scala Happy likes.csv sells.csv frequents.csv rates.csv params.csv")
      System.exit(1)
    }

    System.loadLibrary("CallPrologFromScala")
    val happy = new Happy

    //Source file data
    val likesSource = io.Source.fromFile(args(0))
    val sellsSource = io.Source.fromFile(args(1))
    val freqsSource = io.Source.fromFile(args(2))
    val ratesSource = io.Source.fromFile(args(3))
    val paramsSource = io.Source.fromFile(args(4))
      
    //Turning source files in list to iterate
    val likes = likesSource.getLines().toList.map(l => l.split(",").map(_.trim))
    val sells = sellsSource.getLines().toList.map(l => l.split(",").map(_.trim))
    val freqs = freqsSource.getLines().toList.map(l => l.split(",").map(_.trim))
    val rates = ratesSource.getLines().toList.map(l => l.split(",").map(_.trim))
    val params = paramsSource.getLines().toList.map(l => l.split(",").map(_.trim))

    //Generating Prolog file to use in calculation
    happy.generatePrologFile("data.pl", likes, sells, freqs, rates)
    
    //Printing results
    println("For input file " + args(4) + ", the expected output is: ")
    println("Ranking based on the Happiness Index is:") 
     
    val result = params.map(person => (person(0), happy.callPrologHappyIndex(person(0), person(1).toInt))).sortBy(_._2)
    result.foreach(person => println(person._1 + ": " + (Math.round(person._2 * 100) / 100.0)))
  }
}
