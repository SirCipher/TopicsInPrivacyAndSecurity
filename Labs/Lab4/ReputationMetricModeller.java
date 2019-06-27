import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReputationMetricModeller {
	// Ratings array for a product that received: ratings[0] 1* ratings; ratings[1] 2* ratings; etc.
	// E.g., for a product with no 1* rating, 12 2* ratings, 29 3* ratings and 38 4* ratings (on a four-star scale),
	// ratings[0]=0, ratings[1]=12, ratings[2]=29, ratings[3]=38.
	private int[] ratings;
	
	// Attack type flag - true is a self-promoting attack is analysed, false is a slandering attack is analysed 
	private boolean selfPromotion;
	
	// Range for the number of fake ratings that need to be modelled
	private int minAttack;
	private int maxAttack;
	
	// Constructor - do not modify
	public ReputationMetricModeller(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    if ((line = br.readLine()) == null) {
		    	System.out.println("Invalid input file.");
				System.exit(-1);
		    }
		    String[] vals = line.split(",");
		    Integer numratings = Integer.parseInt(vals[0]);
		    if (numratings<=1 || numratings>50 || vals.length != numratings+1) {
		    	System.out.println("Invalid number of ratings.");
				System.exit(-1);
		    }
		    this.ratings = new int[numratings];
		    for (int i=0; i<numratings; i++) {
		    	ratings[i] = Integer.parseInt(vals[i+1]);
		    	if (ratings[i]<0 || ratings[i]>10000) {
		    		System.out.println("Invalid number of " + (i+1) + "* ratings.");
		    		System.exit(-1);
		    	}
		    }
		    if ((line = br.readLine()) == null) {
		    	System.out.println("Invalid input file.");
				System.exit(-1);
		    }
		    switch (line.substring(0, 1)) {
		    case "+":
		    	this.selfPromotion = true;
		    	break;
		    case "-":
		    	this.selfPromotion = false;
		    	break;
		    default:
		    	System.out.println("Invalid type of attack.");
				System.exit(-1);
		    }
		    vals = line.substring(1).split(",");
		    if (vals.length != 2) {
		    	System.out.println("Invalid attack template.");
				System.exit(-1);
		    }
		    this.minAttack = Integer.parseInt(vals[0]);
		    this.maxAttack = Integer.parseInt(vals[1]);
		    if (this.minAttack<1 || this.minAttack>this.maxAttack || this.maxAttack>10000) {
		    	System.out.println("Invalid attack template.");
				System.exit(-1);
		    }
		} catch (FileNotFoundException e) {
			System.out.println("No such file: " + filename);
			System.exit(-1);
		} catch (Exception e) {
			System.out.println("Invalid input file.");
			System.exit(-1);
		}
	}
	
	// Helper method: returns the value of the rating with index idx - do not modify
	private int getRatingAt(int[] values, int idx) {
		int crtidx=0;
		for (int i=0; i<values.length; i++) {
			crtidx += values[i];
			if (idx<=crtidx) {
				return i+1;
			}
		}
		return -1;
	}
	
	// Helper method: returns the total number of ratings from the values parameter - do not modify
	private int totalRatings(int[] values) {
		int n = 0;
		for (int i=0; i<values.length; i++)
			n += values[i];
		return n;
	}
	
    // Method that returns an array with the numbers of 1*, 2*, etc. ratings after an attack of attackSize
	// Implement this method 
	private int[] modifyRatings(int attackSize) {
		int[] modifiedRatings = new int[this.ratings.length];

		// Add your code here

		return modifiedRatings;
	}
	
	// Method that calculates the mean of the ratings from the array values
	// Implement this method
	private double mean(int[] values) {

		// Add your code here

	}
	
	// Method that calculates the mean of the ratings from the parameter values
	// Implement this method (hint: use the helper method getRatingsAt)
	private double median(int[] values) {

		// Add your code here

	}
	
	// Method that returns the perc% trimmed mean for the ratings from the values parameter
	// Implement this method (Hint: Have a look at the windsorizedMean method below)
	private double trimmedMean(int[] values, int perc) {

		// Add your code here

	}
	
	// Method that returns the perc% Windsorized mean for the ratings from the values parameter - do not modify
	private double windsorizedMean(int[] values, int perc) {
		// Take a copy of the values
		int[] wValues = new int[values.length];
		for (int i=0; i<values.length; i++) {
			wValues[i] = values[i];
		}
		
		// Calculate number of windsorized elements
		int wSize = (this.totalRatings(values) * perc) / 100;
		
		// Windsorize left
		int leftValue = this.getRatingAt(values, wSize+1);
		int extraRatings = 0;
		for (int i=0; i<leftValue-1; i++) {
			extraRatings += values[i];
			wValues[i] = 0;
		}
		wValues[leftValue-1] += extraRatings;
		
		// Windsorize right
		int rightValue = this.getRatingAt(values, this.totalRatings(values)-wSize-1);
		extraRatings = 0;
		for (int i=values.length-1; i>rightValue-1; i--) {
			extraRatings += values[i];
			wValues[i] = 0;
		}
		wValues[rightValue-1] += extraRatings;
		
		// Now calculate the mean
		return mean(wValues);
	}
	
	// Analyse the effect of an attack of attackSize for all examined measures of central location - do not modify
	private void analyseAttack(int attackSize) {
		int[] modifiedRatings = this.modifyRatings(attackSize);
		System.out.print(attackSize + ",");
		System.out.print((mean(modifiedRatings) - mean(this.ratings)) + ",");
		System.out.print((median(modifiedRatings) - median(this.ratings)) + ",");
		System.out.print((trimmedMean(modifiedRatings,5) - trimmedMean(this.ratings,5)) + ",");
		System.out.print((trimmedMean(modifiedRatings,10) - trimmedMean(this.ratings,10)) + ",");
		System.out.print((windsorizedMean(modifiedRatings,5) - windsorizedMean(this.ratings,5)) + ",");
		System.out.print((windsorizedMean(modifiedRatings,10) - windsorizedMean(this.ratings,10)));		
		System.out.println();
	}
		
	// Analyse the effect of the attacks of all required attack sizes - do not modify
	private void analyse() {
		for (int i=this.minAttack; i<=this.maxAttack; i++) {
			this.analyseAttack(i);
		}
	}
	
	// Main method - do not modify
	public static void main(String[] args) {
		// Validate arguments
		if (args.length != 1) {
			System.out.println("Provide input file name as the only command-line parameter.");
			System.exit(-1);
		}

		// Instantiate modeller
		ReputationMetricModeller modeller = new ReputationMetricModeller(args[0]);
		
		// Analyse the effect of all attacks
		modeller.analyse();
	}

}
