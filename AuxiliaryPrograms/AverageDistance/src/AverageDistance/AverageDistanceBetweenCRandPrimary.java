package AverageDistance;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class AverageDistanceBetweenCRandPrimary {
	
	static RandomEngine randEngine;

	public static void main(String[] args) {
		randEngine = new MersenneTwister(new Date());
		Uniform.staticSetRandomEngine(randEngine);
		double radius;
		double primaryRadius;
		int numberOfCR;
		int numberOfPrimary;
		int runtime;
		Scanner sc = new Scanner(System.in);
		while(true){
			System.out.println("Please enter CR radius:");
			radius = Double.parseDouble(sc.next());
			System.out.println("Please enter primary radius:");
			primaryRadius = Double.parseDouble(sc.next());
			System.out.println("Please enter number of CR users:");
			numberOfCR = sc.nextInt();
			System.out.println("Please enter number of Primary users:");
			numberOfPrimary = sc.nextInt();
			System.out.println("Please enter runtime:");
			runtime = sc.nextInt();

			ArrayList<Point2D.Double> primary = new ArrayList<Point2D.Double>();
			ArrayList<Point2D.Double> cr = new ArrayList<Point2D.Double>();
			for(int i = 0; i < numberOfPrimary; i++){
				primary.add(random(primaryRadius));
			}
			for(int i = 0; i < numberOfCR ; i++){
				cr.add(random(radius));
			}
			double sumOfAverages = 0;
			double sumOfStdDev = 0;
			
			for(int j=0;j<runtime;j++){
				ArrayList<Double> dist = new ArrayList<Double>();
				double mu = 0;
				for(int i=0;i<numberOfCR;i++){
					dist.add(findAverageDistance(cr.get(i), primary));
					mu += dist.get(i);
				}
				mu /= numberOfCR;
				sumOfAverages += mu;
				double std = 0;
				for(int i=0;i<numberOfCR;i++){
					std += (dist.get(i) - mu)*(dist.get(i) - mu);
				}
				std /= (numberOfCR-1);
				std = Math.sqrt(std);
				sumOfStdDev += std;
			}
			
			sumOfAverages /= runtime;
			sumOfStdDev /= runtime;
			System.out.println("Mean: "+sumOfAverages);
			System.out.println("Std Dev: "+sumOfStdDev);
		}
	}
	
	static double findAverageDistance(Point2D.Double x, ArrayList<Point2D.Double> y)
	{
		double res=0;
		for(int i=0;i<y.size();i++)
			res+=x.distance(y.get(i));
		return res/y.size();
	}
	
	static Point2D.Double random(double radius)
	{
		double angle = Uniform.staticNextDoubleFromTo(0.0,360.0);
		double distance = Uniform.staticNextDoubleFromTo(0.0,radius);
		double x=distance*Math.cos(angle*Math.PI/180.0);
		double y=distance*Math.sin(angle*Math.PI/180.0);
		return new Point2D.Double(x, y);
	}
}
