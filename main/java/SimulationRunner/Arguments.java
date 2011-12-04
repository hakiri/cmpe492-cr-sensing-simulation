package SimulationRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * This class holds necessary arguments for the simulation.
 */
public class Arguments {
	private int sectrNo = 0;
	private double dNumber = 0;
	private int alpha = 0;
	private int alphaInDegrees;
	private double radius = 0;

	private int numberOfCrNodes = 0;
	private int numberOfPriNodes = 0;

	private double numberOfCalls = 0;
	private double callDura = 0;
	private long simDura = 0;

	private int numberOfFreq = 0;
	private int maxFreqCR = 0;
	private double maxSnr = 0;
	private double sinrThreshold = 0;
	private int crAlpha = 0;
	private int crSector = 0;
	private int crD = 0;
	private int numberOfZones = 0;

	private double slotDur = 0.0;
	private double senseScheduleAdvertisement = 0.0;
	private double commScheduleAdvertisement = 0.0;
	private double commDur = 0.0;
	private double senseResultAdvertisement = 0.0;
	private ArrayList<Double> setOfD = null;
	private int bandwidth;
	private int seed = 0;
	
	private ArrayList<Integer> sectorNumbers = null;
	private ArrayList<Integer> dNumbers = null;
	private ArrayList<Integer> alphaNumbers = null;
	private ArrayList<Integer> numbersOfCrUsersInZone = null;
	
	private double timeUnit = 1;
	
	private int channelModel = 0;
	private int trafficModel = 0;
	private int seedModel = 0;
	
	private boolean batchMode = false;
	private boolean plotOn = false;
	private boolean animationOn = false;
	private int progress = 0;

	public Arguments() {
		setOfD = new ArrayList<Double>();
		sectorNumbers = new ArrayList<Integer>();
		dNumbers = new ArrayList<Integer>();
		alphaNumbers = new ArrayList<Integer>();
		numbersOfCrUsersInZone = new ArrayList<Integer>();
		progress = 0;
	}
	
	public boolean parseArguments(SimulationRunner sr)
	{
		try{
			batchMode = false;
					
			slotDur = Double.parseDouble(sr.getSlotDurField().getText());
			senseScheduleAdvertisement = Double.parseDouble(sr.getSenseScheduleField().getText());
			commScheduleAdvertisement = Double.parseDouble(sr.getCommScheduleField().getText());
			commDur = Double.parseDouble(sr.getCommDurField().getText());
			senseResultAdvertisement = Double.parseDouble(sr.getSensingResultField().getText());
			
			sectrNo = Integer.parseInt(sr.getSectorNo().getText());			//Get number of sectors in the cell
			dNumber = Integer.parseInt(sr.getdNo().getText());				//Get number of d's
			alpha = Integer.parseInt(sr.getAlphaNo().getText());			//Get number of alpha's
			alphaInDegrees = (360/sectrNo)/alpha;							//Evaluate the angle associated to alpha
			radius = Double.parseDouble(sr.getRadiusField().getText());		//Get radius of the cell
			
			numberOfFreq = Integer.parseInt(sr.getNoFreqs().getText());						//Get number of frequencies
			maxSnr = Double.parseDouble(sr.getMaxSNR().getText());							//Get max SNR value
			sinrThreshold = Double.parseDouble(sr.getSinrThresholdFied().getText());
			
			numberOfPriNodes = Integer.parseInt(sr.getNoPriNodes().getText());	//Get number of primary nodes
			maxFreqCR = Integer.parseInt(sr.getNoSlotField().getText());		//Get max number of frequencies a node can sense
			numberOfZones = Integer.parseInt(sr.getNoZones().getText());		//Get the number of zones to be simulated
			
			numberOfCalls = Double.parseDouble(sr.getNoCalls().getText());		//Get number of calls per hour
			callDura = Double.parseDouble(sr.getCallDur().getText());	//Get call duration in terms of min
			
			simDura = Long.parseLong(sr.getSimDur().getText());			//Get duration of the simulation in terms of min
			simDura *= 60000;
			
			bandwidth = Integer.parseInt(sr.getChannelBandwithField().getText())*1000;
			
			double temp = radius / dNumber;							//Evaluate length of each d as they will be equal
			double inc = temp;
			for(int i = 0;i<dNumber;i++,temp+=inc)
				setOfD.add(temp);									//Create set of d's
			
			numberOfCrNodes = 0;
			for(int i = 0; i<numberOfZones ; i++){
				int sectorNumber = Integer.parseInt(sr.getZoneSectorNos().get(i).getText());		//Get sector number CR nodes will be in
				int alphaNumber = Integer.parseInt(sr.getZoneAlphaNos().get(i).getText());			//Get alpha number CR nodes will be in
				int dNmber = Integer.parseInt(sr.getZoneDNos().get(i).getText());					//Get d interval CR nodes will be in
				int numberOfCrUsersInZone = Integer.parseInt(sr.getZoneCRUsers().get(i).getText());	//Get number of CR nodes in zone
				numberOfCrNodes += numberOfCrUsersInZone;
				sectorNumbers.add(sectorNumber);
				alphaNumbers.add(alphaNumber);
				numbersOfCrUsersInZone.add(numberOfCrUsersInZone);
				dNumbers.add(dNmber);
			}
			
			seedModel = sr.getSeedModel().getSelectedIndex();
			if(seedModel != 0){				//If seed model is not random
				seed = Integer.parseInt(sr.getSeedValue().getText());	//Otherwise get seed from user
			}
			
			if(SimulationRunner.animationOnButton.isSelected())					//Get unit time duration in terms of milliseconds
				timeUnit = Double.parseDouble(sr.getUnitTime().getText());
			else
				timeUnit = 1;
			
			channelModel = sr.getChannelModel().getSelectedIndex();
			trafficModel = sr.getTrafficModel().getSelectedIndex();
			plotOn = SimulationRunner.plotOnButton.isSelected();
			animationOn = SimulationRunner.animationOnButton.isSelected();
		} catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(sr, "Invalid argument:\n"+nfe.getMessage(),
					"Simulation", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	public boolean parseArguments(String fileName)
	{
		batchMode = true;
		animationOn = false;
		plotOn = true;		//TODO make optional
		Scanner input = null;
		try {
			input = new Scanner(new File(fileName));
		} catch (FileNotFoundException ex) {
			System.err.println("File "+fileName+" not FOUND!!!");
			return false;
		}
		
		try {
			input.nextLine();				//Start parsing Main options
			numberOfPriNodes = input.nextInt();		//Get number of primary nodes
			channelModel = input.nextInt();
			simDura = input.nextLong();				//Get duration of the simulation in terms of min
			simDura *= 60000;
			maxSnr = input.nextDouble();			//Get max SNR value
			sinrThreshold = input.nextDouble();
			seedModel = input.nextInt();
			if(seedModel != 0){						//If seed model is not random
				seed = input.nextInt();				//Otherwise get seed from user
			}
			int plot = input.nextInt();
			if(plot == 0)
				plotOn = false;
			else
				plotOn = true;
			
			input.nextLine();
			input.nextLine();						//Start parsing Traffic options
			trafficModel = input.nextInt();
			numberOfCalls = input.nextDouble();		//Get number of calls per hour
			callDura = input.nextDouble();			//Get call duration in terms of min
			
			input.nextLine();
			input.nextLine();						//Start parsing Frame options
			maxFreqCR = input.nextInt();			//Get max number of frequencies a node can sense
			slotDur = input.nextDouble();
			senseScheduleAdvertisement = input.nextDouble();
			senseResultAdvertisement = input.nextDouble();
			commScheduleAdvertisement = input.nextDouble();
			commDur = input.nextDouble();
			
			input.nextLine();
			input.nextLine();						//Start parsing Frequency options
			numberOfFreq = input.nextInt();			//Get number of frequencies
			bandwidth = input.nextInt()*1000;
			
			input.nextLine();
			input.nextLine();						//Start parsing Zone options
			sectrNo = input.nextInt();				//Get number of sectors in the cell
			dNumber = input.nextInt();				//Get number of d's
			alpha = input.nextInt();				//Get number of alpha's
			alphaInDegrees = (360/sectrNo)/alpha;	//Evaluate the angle associated to alpha
			radius = input.nextDouble();			//Get radius of the cell
			numberOfZones = input.nextInt();		//Get the number of zones to be simulated
			
			input.nextLine();
			input.nextLine();						//Start parsing Individual Zone options
			numberOfCrNodes = 0;
			for(int i = 0; i<numberOfZones ; i++){
				int sectorNumber = input.nextInt();				//Get sector number CR nodes will be in
				int dNmber = input.nextInt();					//Get d interval CR nodes will be in
				int alphaNumber = input.nextInt();				//Get alpha number CR nodes will be in
				int numberOfCrUsersInZone = input.nextInt();	//Get number of CR nodes in zone
				numberOfCrNodes += numberOfCrUsersInZone;
				sectorNumbers.add(sectorNumber);
				alphaNumbers.add(alphaNumber);
				numbersOfCrUsersInZone.add(numberOfCrUsersInZone);
				dNumbers.add(dNmber);
			}
			
			double temp = radius / dNumber;			//Evaluate length of each d as they will be equal
			double inc = temp;
			for(int i = 0;i<dNumber;i++,temp+=inc)
				setOfD.add(temp);					//Create set of d's

			timeUnit = 1;
		} catch(InputMismatchException ime) {
			System.err.println("Input missmatch ERROR!!!\n"+ime.getMessage());
			return false;
		} catch(NoSuchElementException nsee) {
			System.err.println("Missing Arguments ERROR!!!\n"+nsee.getMessage());
			return false;
		} finally {
			input.close();
		}
		
		return true;
	}

	public int getAlpha() {
		return alpha;
	}

	public int getAlphaInDegrees() {
		return alphaInDegrees;
	}

	public ArrayList<Integer> getAlphaNumbers() {
		return alphaNumbers;
	}

	public boolean isAnimationOn() {
		return animationOn;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public boolean isBatchMode() {
		return batchMode;
	}

	public double getCallDura() {
		return callDura;
	}

	public int getChannelModel() {
		return channelModel;
	}

	public double getCommDur() {
		return commDur;
	}

	public double getCommScheduleAdvertisement() {
		return commScheduleAdvertisement;
	}

	public int getCrAlpha() {
		return crAlpha;
	}

	public int getCrD() {
		return crD;
	}

	public int getCrSector() {
		return crSector;
	}

	public double getdNumber() {
		return dNumber;
	}

	public ArrayList<Integer> getdNumbers() {
		return dNumbers;
	}

	public int getMaxFreqCR() {
		return maxFreqCR;
	}

	public double getMaxSnr() {
		return maxSnr;
	}

	public double getNumberOfCalls() {
		return numberOfCalls;
	}

	public int getNumberOfCrNodes() {
		return numberOfCrNodes;
	}

	public int getNumberOfFreq() {
		return numberOfFreq;
	}

	public int getNumberOfPriNodes() {
		return numberOfPriNodes;
	}

	public int getNumberOfZones() {
		return numberOfZones;
	}

	public ArrayList<Integer> getNumbersOfCrUsersInZone() {
		return numbersOfCrUsersInZone;
	}

	public boolean isPlotOn() {
		return plotOn;
	}

	public double getRadius() {
		return radius;
	}

	public ArrayList<Integer> getSectorNumbers() {
		return sectorNumbers;
	}

	public int getSectrNo() {
		return sectrNo;
	}

	public int getSeed() {
		return seed;
	}

	public int getSeedModel() {
		return seedModel;
	}

	public double getSenseResultAdvertisement() {
		return senseResultAdvertisement;
	}

	public double getSenseScheduleAdvertisement() {
		return senseScheduleAdvertisement;
	}

	public ArrayList<Double> getSetOfD() {
		return setOfD;
	}

	public long getSimDura() {
		return simDura;
	}

	public double getSinrThreshold() {
		return sinrThreshold;
	}

	public double getSlotDur() {
		return slotDur;
	}

	public double getTimeUnit() {
		return timeUnit;
	}

	public int getTrafficModel() {
		return trafficModel;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		if(batchMode){
			if(this.progress != progress && progress != -1){
				System.out.println(progress + "%");
				this.progress = progress;
			}
		}
		else{
			this.progress = progress;
			if(progress >= 0)
				SimulationRunner.progressBar.setValue(progress);
			else{
				SimulationRunner.progressBar.setValue(0);
				SimulationRunner.progressBar.setVisible(false);
			}
		}
	}
}
