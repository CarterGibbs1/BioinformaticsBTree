package cs321.btree;

/**
 * Constructs a progress/loading bar that gives visual feedback that a program
 * is working. Once made, nothing should be printed to the console until the
 * loading is complete. Does not work on Eclipse due to the use of a carriage
 * return '\r'.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 */
public class ProgressBar {

	private StringBuilder bar;
	private int progress;
	
	private final char EMPTY; //default: -
	private final char FULL; //default: #
	private final boolean DESTROY; //default: true
	private final int BAR_LENGTH;
	private final int FINAL_PROGRESS;
	private final double SCALED_PROGRESS;
	private final String INVALID_CHARS = "\r\n\b\t\f";

	/**
	 * Construct a progress bar and print it to the console.
	 * 
	 * @param barLength     The length in chars that the progress bar will be + 2
	 *                      (> 0)
	 * @param finalProgress What the progress bar will be counting up to, i.e. what
	 *                      value represents 100%, complete, done, etc (> 0)
	 * @param destroy       True to wipe the progress bar once complete, false to
	 *                      keep the progress bar.
	 * @param empty         A regular char or a non format changing escape char that
	 *                      will represent an unloaded segment
	 * @param full          A regular char or a non format changing escape char that
	 *                      will represent a loaded segment
	 * 
	 * @throws IllegalArgumentException When barLength or finalCount <= 0, finalCount,
	 *         or full or empty are an escape character that effects formatting
	 */
	public ProgressBar(int barLength, int finalProgress, boolean destroy, char empty, char full) throws IllegalArgumentException{
		//check for valid values
		if(barLength <= 0) {
			throw new IllegalArgumentException("parameter 'barLength' is less than or equal to 0");
		}
		if(finalProgress <= 0) {
			throw new IllegalArgumentException("parameter 'finalProgress' is less than or equal to 0");
		}
//		if(finalProgress < (barLength* 2)) {
//			throw new IllegalArgumentException("parameter 'finalProgress' is less than parameter 'barLength' * 2");
//		}
		if(INVALID_CHARS.indexOf(empty) != -1) {
			throw new IllegalArgumentException("parameter 'empty' is an invalid char (\\r, \\n, \\b, \\t, or \\f)");
		}
		if(INVALID_CHARS.indexOf(full) != -1) {
			throw new IllegalArgumentException("parameter 'full' is an invalid char (\\r, \\n, \\b, \\t, or \\f)");
		}
		
		//initialize values
		BAR_LENGTH = barLength;
		SCALED_PROGRESS = ((double) finalProgress / BAR_LENGTH);
		FINAL_PROGRESS = finalProgress;
		progress = 0;
		DESTROY = destroy;
		EMPTY = empty;
		FULL = full;
		bar = new StringBuilder();
		
		//construct progress bar using StringBuilder
		bar.append("[");
		for (int i = 0; i < BAR_LENGTH; i++) {
				bar.append(EMPTY);
		}
		bar.append("]\r");
		System.out.print(bar);
	}
	
	/**
	 * Construct a progress bar and print it to the console. Will wipe the
	 * progress bar once complete.
	 * 
	 * @param barLength     The length in chars that the progress bar will be + 2
	 *                      (> 0)
	 * @param finalProgress What the progress bar will be counting up to, i.e. what
	 *                      value represents 100%, complete, done, etc (> 0, >= barLength*2)
	 * @param empty         A regular char or a non format changing escape char that
	 *                      will represent an unloaded segment
	 * @param full          A regular char or a non format changing escape char that
	 *                      will represent a loaded segment
	 * 
	 * @throws IllegalArgumentException When barLength or finalCount <= 0, finalCount
	 *         < barLength*2, or full or empty are an escape character that effects
	 *         formatting
	 */
	public ProgressBar(int barLength, int finalProgress, char empty, char full) throws IllegalArgumentException{
		this(barLength, finalProgress, true, empty, full);
	}
	
	/**
	 * Construct a progress bar and print it to the console. Will will appear
	 * similar to "[##----]."
	 * 
	 * @param barLength     The length in chars that the progress bar will be + 2
	 *                      (> 0)
	 * @param finalProgress What the progress bar will be counting up to, i.e. what
	 *                      value represents 100%, complete, done, etc (> 0, >= barLength*2)
	 * @param destroy       True to wipe the progress bar once complete, false to
	 *                      keep the progress bar.
	 * 
	 * @throws IllegalArgumentException When barLength or finalCount <= 0 or finalCount
	 *         < barLength*2
	 */
	public ProgressBar(int barLength, int finalProgress, boolean destroy) throws IllegalArgumentException{
		this(barLength, finalProgress, destroy, '-', '#');
	}
	
	/**
	 * Construct a progress bar and print it to the console. Will wipe the
	 * progress bar once complete and will appear similar to "[##----]."
	 * 
	 * @param barLength     The length in chars that the progress bar will be + 2
	 *                      (> 0)
	 * @param finalProgress What the progress bar will be counting up to, i.e. what
	 *                      value represents 100%, complete, done, etc (> 0, >= barLength*2)
	 * 
	 * @throws IllegalArgumentException When barLength or finalCount <= 0 or finalCount
	 *         < barLength*2
	 */
	public ProgressBar(int barLength, int finalProgress) throws IllegalArgumentException{
		this(barLength, finalProgress, true, '-', '#');
	}
	
	//=================================================================================================================
	
	/**
	 * Indicates whether the progress bar is complete or not.
	 * 
	 * @return True if this ProgressBar's loading is complete (progress =
	 *         finalCount), false otherwise
	 */
	public boolean isComplete() {
		return (progress >= FINAL_PROGRESS);
	}
	
	/**
	 * Accessor method for progress.
	 * 
	 * @return progress (How many times increaseProgress has been called)
	 */
	public int getProgress() {
		return progress;
	}
	
	/**
	 * Accessor method for FINAL_PROGRESS.
	 * 
	 * @return FINAL_PROGRESS (how many progress increments until complete)
	 */
	public int getFinalProgress() {
		return FINAL_PROGRESS;
	}

	/**
	 * Increment this object's internal progress and update the progress bar if
	 * necessary. The first time True is returned, the loading is complete and
	 * a future call will throw a IllegalStateException.
	 * 
	 * @return True if this ProgressBar's loading is complete (progress =
	 *         finalProgress), false otherwise
	 * 
	 * @throws IllegalStateException When called after ProgressBar is complete
	 */
	public boolean increaseProgress() throws IllegalStateException {
		//if bar is complete, throw exception
		if(isComplete()) {
			System.out.println(progress + " " + FINAL_PROGRESS);
			throw new IllegalStateException("Attempted to use method 'increaseProgress' on a completed ProgressBar");
		}
		
		progress++;

		//if enough progress has been made, visually increase the progress bar
		if (progress % SCALED_PROGRESS <= 1) {
			//only print bar if it isn't complete
			if(!isComplete()) {
				bar.setCharAt((int) (progress / SCALED_PROGRESS) + 1, FULL);
				System.out.print(bar);
			}
			// if the progress/loading is complete, return true and wrap up
			else {
				// if the bar is set to be destroyed, wipe it
				// else keep it and add some new lines
				if (DESTROY) {
					for (int i = 0; i < (BAR_LENGTH + 2); i++) {
						bar.setCharAt(i, ' ');
					}
					bar.append('\n');
				} else {
					bar.setCharAt(BAR_LENGTH + 2, '\n'); // get rid of '\r'
				}

				System.out.print(bar);

				return true;
			}
		}
		
		return false;
	}
}
