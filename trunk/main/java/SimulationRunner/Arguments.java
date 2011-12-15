package SimulationRunner;

import cern.jet.random.engine.RandomSeedTable;
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
	private int numberOfSectors = 0;
	private double dNumber = 0;
	private int numberOfAlphaSlices = 0;
	private int alphaInDegrees;
	private double radius = 0;
	private double primaryRadius = 0;

	private int numberOfCrNodes = 0;
	private int numberOfPriNodes = 0;

	private double averageNumberOfCalls = 0;
	private double averageCallDur = 0;
	private long simulationDuration = 0;

	private int numberOfFreq = 0;
	private int numberOfSensingSlots = 0;
	private double transmitPower = 0;
	private double powerThreshold = 0;
	private int numberOfZones = 0;

	private double sensingSlotDur = 0.0;
	private double senseScheduleAdvertisementDur = 0.0;
	private double commScheduleAdvertisementDur = 0.0;
	private double commDur = 0.0;
	private double senseResultAdvertisementDur = 0.0;
	private ArrayList<Double> setOfD = null;
	private int bandwidth;
	private int seed = 0;
	
	private ArrayList<Integer> sectorNumbers = null;
	private ArrayList<Integer> dNumbers = null;
	private ArrayList<Integer> alphaNumbers = null;
	private ArrayList<Integer> numbersOfCrUsersInZone = null;
	
	private double timeUnit = 1;
	
	private int trafficModel = 0;
	private int seedModel = 0;
	
	private boolean batchMode = false;
	private boolean plotOn = false;
	private boolean animationOn = false;
	private int progress = 0;
    final private int numberOfReports = 30;
    
	/**
	 * Creates an arguments object that holds all related parameters of simulation.
	 */
	public Arguments() {
		setOfD = new ArrayList<Double>();
		sectorNumbers = new ArrayList<Integer>();
		dNumbers = new ArrayList<Integer>();
		alphaNumbers = new ArrayList<Integer>();
		numbersOfCrUsersInZone = new ArrayList<Integer>();
		progress = 0;
	}
	
	/**
	 * Parses the arguments from GUI.
	 * @param sr GUI of simulation that holds parameters
	 * @return <ul>
	 *				<li><i>True </i> if there is no parsing errors
	 *				<li><i>False </i> if there are parsing errors
	 *		   </ul>
	 */
	public boolean parseArguments(GraphicalUserInterface sr)
	{
		try{
			batchMode = false;
					
			sensingSlotDur = Double.parseDouble(sr.getSlotDurField().getText());
			senseScheduleAdvertisementDur = Double.parseDouble(sr.getSenseScheduleField().getText());
			commScheduleAdvertisementDur = Double.parseDouble(sr.getCommScheduleField().getText());
			commDur = Double.parseDouble(sr.getCommDurField().getText());
			senseResultAdvertisementDur = Double.parseDouble(sr.getSensingResultField().getText());
			
			numberOfSectors = Integer.parseInt(sr.getSectorNo().getText());			//Get number of sectors in the cell
			dNumber = Integer.parseInt(sr.getdNo().getText());				//Get number of d's
			numberOfAlphaSlices = Integer.parseInt(sr.getAlphaNo().getText());			//Get number of alpha's
			alphaInDegrees = (360/numberOfSectors)/numberOfAlphaSlices;							//Evaluate the angle associated to alpha
			radius = Double.parseDouble(sr.getRadiusField().getText())*100;			//Get radius of the cell
			primaryRadius = radius + 1500;
			
			numberOfFreq = Integer.parseInt(sr.getNoFreqs().getText());						//Get number of frequencies
			transmitPower = Double.parseDouble(sr.getTransmitPower().getText());							//Get max SNR value
			powerThreshold = Double.parseDouble(sr.getTauField().getText());
			
			numberOfPriNodes = Integer.parseInt(sr.getNoPriNodes().getText());	//Get number of primary nodes
			numberOfSensingSlots = Integer.parseInt(sr.getNoSlotField().getText());		//Get max number of frequencies a node can sense
			numberOfZones = Integer.parseInt(sr.getNoZones().getText());		//Get the number of zones to be simulated
			
			averageNumberOfCalls = Double.parseDouble(sr.getNoCalls().getText());		//Get number of calls per hour
			averageCallDur = Double.parseDouble(sr.getCallDur().getText());	//Get call duration in terms of min
			
			simulationDuration = Long.parseLong(sr.getSimDur().getText());			//Get duration of the simulation in terms of min
			simulationDuration *= 60000;
			
			bandwidth = Integer.parseInt(sr.getChannelBandwithField().getText())*1000;
			
			for(int i = 1;i<=dNumber;i++)
				setOfD.add(radius * Math.sqrt((double)i/dNumber));									//Create set of d's
			
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
			else {
				seed = RandomSeedTable.getSeedAtRowColumn((int)System.currentTimeMillis(),
														  (int)System.currentTimeMillis());
			}
			
			if(GraphicalUserInterface.animationOnButton.isSelected())					//Get unit time duration in terms of milliseconds
				timeUnit = Double.parseDouble(sr.getUnitTime().getText());
			else
				timeUnit = 1;
			
			trafficModel = sr.getTrafficModel().getSelectedIndex();
			plotOn = GraphicalUserInterface.plotOnButton.isSelected();
			animationOn = GraphicalUserInterface.animationOnButton.isSelected();
		} catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(sr, "Invalid argument:\n"+nfe.getMessage(),
					"Simulation", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**
	 * Parses arguments from a given file.
	 * @param fileName Name of the file that holds simulation parameters
	 * @return <ul>
	 *				<li><i>True </i> if there is no parsing errors
	 *				<li><i>False </i> if there are parsing errors
	 *		   </ul>
	 */
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
			simulationDuration = input.nextLong();				//Get duration of the simulation in terms of min
			simulationDuration *= 60000;
			transmitPower = Double.parseDouble(input.next());			//Get transmit power value in terms of dBm
			powerThreshold = Double.parseDouble(input.next());
			seedModel = input.nextInt();
			if(seedModel != 0){						//If seed model is not random
				seed = input.nextInt();				//Otherwise get seed from user
			}
			else {
				seed = RandomSeedTable.getSeedAtRowColumn((int)System.currentTimeMillis(),
														  (int)System.currentTimeMillis());
			}
			int plot = input.nextInt();
			if(plot == 0)
				plotOn = false;
			else
				plotOn = true;
			
			input.nextLine();
			input.nextLine();						//Start parsing Traffic options
			trafficModel = input.nextInt();
			averageNumberOfCalls = Double.parseDouble(input.next());		//Get number of calls per hour
			averageCallDur = Double.parseDouble(input.next());			//Get call duration in terms of min
			
			input.nextLine();
			input.nextLine();						//Start parsing Frame options
			numberOfSensingSlots = input.nextInt();			//Get max number of frequencies a node can sense
			sensingSlotDur = Double.parseDouble(input.next());
			senseScheduleAdvertisementDur = Double.parseDouble(input.next());
			senseResultAdvertisementDur = Double.parseDouble(input.next());
			commScheduleAdvertisementDur = Double.parseDouble(input.next());
			commDur = Double.parseDouble(input.next());
			
			input.nextLine();
			input.nextLine();						//Start parsing Frequency options
			numberOfFreq = input.nextInt();			//Get number of frequencies
			bandwidth = input.nextInt()*1000;
			
			input.nextLine();
			input.nextLine();						//Start parsing Zone options
			numberOfSectors = input.nextInt();				//Get number of sectors in the cell
			dNumber = input.nextInt();				//Get number of d's
			numberOfAlphaSlices = input.nextInt();				//Get number of alpha's
			alphaInDegrees = (360/numberOfSectors)/numberOfAlphaSlices;	//Evaluate the angle associated to alpha
			radius = Double.parseDouble(input.next())*100;			//Get radius of the cell
			primaryRadius = radius + 1500;
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
			
			for(int i = 1;i<=dNumber;i++)
				setOfD.add(radius * Math.sqrt((double)i/dNumber));					//Create set of d's

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

	/**
	 * Returns number of alpha slices in a sector
	 * @return Number of alpha slices in a sector
	 */
	public int getNumberOfAlphaSlices() {
		return numberOfAlphaSlices;
	}

	/**
	 * Returns the degree of alpha slices
	 * @return The degree of alpha slices
	 */
	public int getAlphaInDegrees() {
		return alphaInDegrees;
	}

	/**
	 * Returns alpha numbers of registered zones
	 * @return Alpha numbers of registered zones
	 */
	public ArrayList<Integer> getAlphaNumbers() {
		return alphaNumbers;
	}

	/**
	 * Returns whether the animation is on or not
	 * @return Whether the animation is on or not
	 */
	public boolean isAnimationOn() {
		return animationOn;
	}

	/**
	 * Returns bandwidth of channels
	 * @return Bandwidth of channels
	 */
	public int getBandwidth() {
		return bandwidth;
	}

	/**
	 * Returns whether the simulation is running on batch mode or not
	 * @return Whether the simulation is running on batch mode or not
	 */
	public boolean isBatchMode() {
		return batchMode;
	}

	/**
	 * Returns average call duration of nodes in terms of minutes
	 * @return Average call duration of nodes in terms of minutes
	 */
	public double getAverageCallDur() {
		return averageCallDur;
	}

	/**
	 * Returns communication duration in a frame in terms of msec
	 * @return Communication duration in a frame in terms of msec
	 */
	public double getCommDur() {
		return commDur;
	}

	/**
	 * Returns communication schedule advertisement duration in a frame in terms of msec
	 * @return Communication schedule advertisement duration in a frame in terms of msec
	 */
	public double getCommScheduleAdvertisementDur() {
		return commScheduleAdvertisementDur;
	}

	/**
	 * Returns number of d segments in a slice
	 * @return Number of d segments in a slice
	 */
	public double getdNumber() {
		return dNumber;
	}

	/**
	 * Returns d numbers of registered zones
	 * @return d numbers of registered zones
	 */
	public ArrayList<Integer> getdNumbers() {
		return dNumbers;
	}

	/**
	 * Returns number of sensing slots
	 * @return Number of sensing slots
	 */
	public int getNumberOfSensingSlots() {
		return numberOfSensingSlots;
	}

	/**
	 * Returns transmit power in terms of dBm
	 * @return transmit power in terms of dBm
	 */
	public double getTransmitPower() {
		return transmitPower;
	}

	/**
	 * Returns average number of calls in an hour
	 * @return Average number of calls in an hour
	 */
	public double getAverageNumberOfCalls() {
		return averageNumberOfCalls;
	}

	/**
	 * Returns number of CR users
	 * @return Number of CR users
	 */
	public int getNumberOfCrNodes() {
		return numberOfCrNodes;
	}

	/**
	 * Returns number of available frequencies
	 * @return Number of available frequencies
	 */
	public int getNumberOfFreq() {
		return numberOfFreq;
	}

	/**
	 * Returns number of primary users
	 * @return Number of primary users
	 */
	public int getNumberOfPriNodes() {
		return numberOfPriNodes;
	}

	/**
	 * Returns number of registered zones
	 * @return Number of registered zones
	 */
	public int getNumberOfZones() {
		return numberOfZones;
	}

	/**
	 * Returns number of CR users in registered zones
	 * @return Number of CR users in registered zones
	 */
	public ArrayList<Integer> getNumbersOfCrUsersInZone() {
		return numbersOfCrUsersInZone;
	}

	/**
	 * Returns Whether the plots will be drawn or not
	 * @return whether the plots will be drawn or not
	 */
	public boolean isPlotOn() {
		return plotOn;
	}

	/**
	 * Returns radius of cell in terms of 100 meters
	 * @return Radius of cell in terms of 100 meters
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Returns sectors of registered zones
	 * @return Sectors of registered zones
	 */
	public ArrayList<Integer> getSectorNumbers() {
		return sectorNumbers;
	}

	/**
	 * Returns number of sectors in a cell
	 * @return Number of sectors in a cell
	 */
	public int getNumberOfSectors() {
		return numberOfSectors;
	}

	/**
	 * Returns the seed value
	 * @return The seed value
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * Returns seed model
	 * @return Seed model
	 */
	public int getSeedModel() {
		return seedModel;
	}

	/**
	 * Returns duration of sensing result advertisement in terms of msec
	 * @return Duration of sensing result advertisement in terms of msec
	 */
	public double getSenseResultAdvertisementDur() {
		return senseResultAdvertisementDur;
	}

	/**
	 * Returns duration of sensing schedule advertisement in terms of msec
	 * @return Duration of sensing schedule advertisement in terms of msec
	 */
	public double getSenseScheduleAdvertisementDur() {
		return senseScheduleAdvertisementDur;
	}

	/**
	 * Returns set of maximum distances of zones
	 * @return Set of maximum distances of zones
	 */
	public ArrayList<Double> getSetOfD() {
		return setOfD;
	}

	/**
	 * Returns total simulation duration
	 * @return Total simulation duration
	 */
	public long getSimulationDuration() {
		return simulationDuration;
	}

	/**
	 * Returns duration of a sensing slot
	 * @return Duration of a sensing slot
	 */
	public double getSensingSlotDur() {
		return sensingSlotDur;
	}

	/**
	 * Returns unit of 1 simulation msec in terms of real msec
	 * @return Unit of 1 simulation msec in terms of real msec
	 */
	public double getTimeUnit() {
		return timeUnit;
	}

	/**
	 * Returns traffic model
	 * @return Traffic model
	 */
	public int getTrafficModel() {
		return trafficModel;
	}

	/**
	 * Returns energy threshold for CR users to decide whether a channel is vacant or not
	 * @return Energy threshold for CR users to decide whether a channel is vacant or not
	 */
	public double getPowerThreshold() {
		return powerThreshold;
	}
	
	/**
	 * Returns Current progress of the simulation
	 * @return Current progress of the simulation
	 */
	public int getProgress() {
		return progress;
	}

    public int getNumberOfReports() {
        return numberOfReports;
    }
    
	/**
	 * Sets the current progress of the simulation
	 * @param progress
	 */
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
				GraphicalUserInterface.progressBar.setValue(progress);
			else{
				GraphicalUserInterface.progressBar.setValue(0);
				GraphicalUserInterface.progressBar.setVisible(false);
			}
		}
	}

	public double getPrimaryRadius() {
		return primaryRadius;
	}
}
