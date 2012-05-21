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
	private String logFileDirectory = null;
	private double radius = 0;
	private double primaryRadius = 0;
	private double interferenceDistance;

	private int numberOfCrNodes = 0;
	private int numberOfPriNodes = 0;

	private double averageNumberOfCalls = 0;
	private double averageCallDur = 0;
	private long simulationDuration = 0;

	private int numberOfFreq = 0;
	private double transmitPower = 0;
	private double powerThreshold = 0;
	private double noiseFloor = 0;
	private double noiseStdDev = 0;
	
	private int numberOfZones = 0;
	private double sensingSlotDur = 0.0;
	private double senseScheduleAdvertisementDur = 0.0;
	private double commScheduleAdvertisementDur = 0.0;
	private double commDur = 0.0;
	private double senseResultAdvertisementDur = 0.0;
	private int bandwidth;
	private int seed = 0;
	
	private double timeUnit = 1;
	
	private int trafficModel = 0;
	private int seedModel = 0;
	
	private boolean batchMode = false;
	private boolean animationOn = false;
	private int progress = 0;
    final private int numberOfReports = 30;
    
	/**
	 * Creates an arguments object that holds all related parameters of simulation.
	 */
	public Arguments() {
		progress = 0;
	}
	
	/**
	 * Parses the arguments from GUI.
	 * @param gui GUI of simulation that holds parameters
	 * @return <ul>
	 *				<li><i>True </i> if there is no parsing errors
	 *				<li><i>False </i> if there are parsing errors
	 *		   </ul>
	 */
	public boolean parseArguments(GraphicalUserInterface gui)
	{
		try{
			batchMode = false;
					
			sensingSlotDur = Double.parseDouble(gui.getSlotDurField().getText());
			senseScheduleAdvertisementDur = Double.parseDouble(gui.getSenseScheduleField().getText());
			commScheduleAdvertisementDur = Double.parseDouble(gui.getCommScheduleField().getText());
			commDur = Double.parseDouble(gui.getCommDurField().getText());
			senseResultAdvertisementDur = Double.parseDouble(gui.getSensingResultField().getText());
			
			radius = Double.parseDouble(gui.getRadiusField().getText())*100;			//Get radius of the cell
			
			numberOfFreq = Integer.parseInt(gui.getNoFreqs().getText());						//Get number of frequencies
			transmitPower = Double.parseDouble(gui.getTransmitPower().getText());							//Get max SNR value
			powerThreshold = Double.parseDouble(gui.getTauField().getText());
			noiseFloor = Double.parseDouble(gui.getNoiseFloorField().getText());
			noiseStdDev = Double.parseDouble(gui.getNoiseStdDevField().getText());
			interferenceDistance = powerThreshold - transmitPower + 38.4;
			interferenceDistance /= (-35);
			interferenceDistance = Math.pow(10, interferenceDistance)*1000.0;
			primaryRadius = radius + interferenceDistance;
			
			numberOfPriNodes = Integer.parseInt(gui.getNoPriNodes().getText());	//Get number of primary nodes
			numberOfZones = Integer.parseInt(gui.getNoZones().getText());		//Get the number of zones to be simulated
			
			averageNumberOfCalls = Double.parseDouble(gui.getNoCalls().getText());		//Get number of calls per hour
			averageCallDur = Double.parseDouble(gui.getCallDur().getText());	//Get call duration in terms of min
			
			simulationDuration = Long.parseLong(gui.getSimDur().getText());			//Get duration of the simulation in terms of min
			simulationDuration *= 60000;
			
			bandwidth = Integer.parseInt(gui.getChannelBandwithField().getText())*1000;
			
			seedModel = gui.getSeedModel().getSelectedIndex();
			if(seedModel != 0){				//If seed model is not random
				seed = Integer.parseInt(gui.getSeedValue().getText());	//Otherwise get seed from user
			}
			else {
				seed = RandomSeedTable.getSeedAtRowColumn((int)System.currentTimeMillis(),
														  (int)System.currentTimeMillis());
			}
			
			if(GraphicalUserInterface.animationOnButton.isSelected())					//Get unit time duration in terms of milliseconds
				timeUnit = Double.parseDouble(gui.getUnitTime().getText());
			else
				timeUnit = 1;
			
			trafficModel = gui.getTrafficModel().getSelectedIndex();
			animationOn = GraphicalUserInterface.animationOnButton.isSelected();
			logFileDirectory = "C:\\Out\\";
		} catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(gui, "Invalid argument:\n"+nfe.getMessage(),
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
		Scanner input = null;
		try {
			input = new Scanner(new File(fileName));
		} catch (FileNotFoundException ex) {
			System.err.println("File "+fileName+" not FOUND!!!");
			System.err.println(ex.getMessage());
			System.err.println(ex.getLocalizedMessage());
			return false;
		}
		
		try {
			input.nextLine();				//Start parsing Main options
			numberOfPriNodes = input.nextInt();		//Get number of primary nodes
			simulationDuration = input.nextLong();				//Get duration of the simulation in terms of min
			simulationDuration *= 60000;
			transmitPower = Double.parseDouble(input.next());			//Get transmit power value in terms of dB
			noiseFloor = Double.parseDouble(input.next());
			noiseStdDev = Double.parseDouble(input.next());
			powerThreshold = Double.parseDouble(input.next());
			seedModel = input.nextInt();
			if(seedModel != 0){						//If seed model is not random
				seed = input.nextInt();				//Otherwise get seed from user
			}
			else {
				seed = RandomSeedTable.getSeedAtRowColumn((int)System.currentTimeMillis(),
														  (int)System.currentTimeMillis());
			}
			
			input.nextLine();
			input.nextLine();						//Start parsing Traffic options
			trafficModel = input.nextInt();
			averageNumberOfCalls = Double.parseDouble(input.next());		//Get number of calls per hour
			averageCallDur = Double.parseDouble(input.next());			//Get call duration in terms of min
			
			input.nextLine();
			input.nextLine();						//Start parsing Frame options
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
			radius = Double.parseDouble(input.next())*100;			//Get radius of the cell
			primaryRadius = radius + 1500;
			numberOfZones = input.nextInt();		//Get the number of zones to be simulated
			
			input.nextLine();
			input.nextLine();						//Start parsing Individual Zone options
			numberOfCrNodes = 0;		//TODO Change file format accordingly

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
     * @param args Arguments
	 * @return <ul>
	 *				<li><i>True </i> if there is no parsing errors
	 *				<li><i>False </i> if there are parsing errors
	 *		   </ul>
	 */
	public boolean parseArguments(String []args)
	{
		for(int i = 0; i< args.length ; i++){
			System.out.println(args[i]);
		}
		batchMode = true;
		animationOn = false;
		int crUsers;
		try {
			numberOfPriNodes = Integer.parseInt(args[0]);
			crUsers = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid Argument!!!");
			System.out.println(nfe.getMessage());
			return false;
		}
		
		logFileDirectory = args[2];
		
		simulationDuration = 300;				//Get duration of the simulation in terms of min
		simulationDuration *= 60000;
		transmitPower = -10.0;			//Get transmit power value in terms of dB
		noiseFloor = -85.0;
		noiseStdDev = 20;
		powerThreshold = -62.5;
		//powerThreshold = -60;
		seedModel = 0;
		seed = RandomSeedTable.getSeedAtRowColumn((int)System.currentTimeMillis(),
													  (int)System.currentTimeMillis());
		trafficModel = 0;
		averageNumberOfCalls = 0.5;		//Get number of calls per hour
		averageCallDur = 2.0;			//Get call duration in terms of min

		sensingSlotDur = 10.0;
		senseScheduleAdvertisementDur = 10.0;
		senseResultAdvertisementDur = 10.0;
		commScheduleAdvertisementDur = 10.0;
		commDur = 630;

		numberOfFreq = 60;			//Get number of frequencies
		bandwidth = 8000000;

		radius = 1500;			//Get radius of the cell
		interferenceDistance = powerThreshold - transmitPower + 38.4;
		interferenceDistance /= (-35);
		interferenceDistance = Math.pow(10, interferenceDistance)*1000.0;
		primaryRadius = radius + interferenceDistance;
		numberOfZones = Integer.parseInt(args[3]);		//Get the number of zones to be simulated

		numberOfCrNodes = crUsers;

		timeUnit = 1;		
		return true;
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
	 * Returns radius of cell in terms of 100 meters
	 * @return Radius of cell in terms of 100 meters
	 */
	public double getRadius() {
		return radius;
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
    
    /**
     * Returns how many reports will be done in the log file during the simulation.
     * @return how many reports will be done in the log file during the simulation
     */
    public int getNumberOfReports() {
        return numberOfReports;
    }
    
	/**
	 * Sets the current progress of the simulation
	 * @param progress progress of the simulation
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
    
    /**
     * Returns the radius of the circle that primary users can be deployed.
     * @return the radius of the circle that primary users can be deployed.
     */
	public double getPrimaryRadius() {
		return primaryRadius;
	}
    
    /**
     * Returns the noise floor.
     * @return the noise floor.
     */
	public double getNoiseFloor() {
		return noiseFloor;
	}
    
    /**
     * Returns the standard deviation of the noise
     * @return the standard deviation of the noise
     */
	public double getNoiseStdDev() {
		return noiseStdDev;
	}

    /**
     * Returns location of the log file.
     * @return location of the log file.
     */
	public String getLogFileDirectory() {
		return logFileDirectory;
	}

    /**
     * Sets location of the log file.
     * @param logFileDirectory log file directory
     */
	public void setLogFileDirectory(String logFileDirectory) {
		this.logFileDirectory = logFileDirectory;
	}

	/**
	 * Returns the maximum distance between two nodes which could cause interference
	 * @return Maximum distance between two nodes which could cause interference
	 */
	public double getInterferenceDistance() {
		return interferenceDistance;
	}
	
	public final static int MAJORITY = 0;
	public final static int NK = 1;
	
	public static int cooperationRule = 0;
}
