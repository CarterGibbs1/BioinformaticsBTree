package cs321.btree;
/**
 * An object that is to be stored in a BTree. Specific placement in the BTree is
 * determined by previous TreeObjects and key values. A notable method is
 * compare(), which determines which object is larger in key value.
 *
 * @author Aaron Goin, Mesa Greear
 * @version Spring 2022
 *
 */
public class TreeObject {
    // valid treeObjects: "a", "t", "c", "g" (lowercase)
    // corresponding 2-bit binary value: 00, 11, 01, 10
    private int frequency;
    private long keyLongVal;
    
    private static int sequenceLength;
    
   	//=================================================================================================================
	//                                                CONSTRUCTORS
	//=================================================================================================================

    /**
     * Constructor: Creates a TreeObject with the specified String key. Also sets the value
     * of b depending on the String key. The key values in treeObjectKey must all
     * consist of "a", "c", "g", or "t" to convert to a usable long value. Everything is set.
     *
     * @param treeObjectKey The String key of the object to be compressed to a long
     */
    public TreeObject(String treeObjectKey) {
        this.keyLongVal = setLongKey(treeObjectKey);
        frequency = 1;
    }

    /**
     * Constructor: A long key is passed in. Useful if read from RAF
     *
     * @param longKey   The key of this TreeObject
     * @param frequency How many occurrences of the key there are in the BTree
     */
    public TreeObject(long longKey, int frequency) {
        this.keyLongVal = longKey;
        this.frequency = frequency;
    }
    
   	//=================================================================================================================
	//                                         BTREE FUNCTIONALITY METHODS
	//=================================================================================================================

    /** return string
     * Intended to be used by another driver class when the string length is known, but the second constructor is used.
     *
     * @param stringLength length of string after being scanned
     */
    public String keyWithStringLength(int stringLength) {
        String longKey = "";
        for (int i = 0; i < stringLength * 2; i++) {
            longKey += "0";
        }
        longKey += Long.toBinaryString(keyLongVal);
        longKey = longKey.substring(0, Long.toBinaryString(keyLongVal).length() + stringLength - 1);

        return stringOfExactLongString(longKey);
    }

    /**
     * Takes the String version of a long key, and returns the lettered version of it to be set to String key.
     *
     * @param longKey the String version of the long value
     * @return the String of the treeObjectKey, used as a setter
     */
    private String stringOfExactLongString(String longKey) {
        String stringKey = "";
        for (int j = 0; j < longKey.length(); j += 2) {
            stringKey += numToLetter(longKey.substring(j, j + 2));
        }
        return stringKey;
    }

    /**
     * Coverts 2-bit binary String to it's corresponding letter.
     *
     * @param num the String representation of the 2-bit binary number
     * @return the char of the corresponding letter, n string if num isn't listed below
     */
    private char numToLetter(String num) {
      	switch(num) {
	    	case "00":
	    		return 'a';
	    	case "01":
	    		return 'c';
	    	case "10":
	    		return 'g';
	    	case "11":
	    		return 't';
	    	default:
	    		return 'n';
    	}
    }

    /**
     * Sets long key from a dna string value.
     *
     * @param treeObjectKey the string, contains dna value
     * @return the long key that will be set for this TreeObject
     */
    public long setLongKey(String treeObjectKey) {
        if (keyLongVal == -1) {
            return -1;
        }
        return byteShift(treeObjectKey);
    }
    
    /**
     * Creates a long value from a treeObjectKey in String form
     *
     * @param s the treeObjectKey in String form
     * @return the corresponding long value according to 2-bit keys: a = 00, c = 01,
     *         g = 10, t = 11
     */
    private long byteShift(String s) {
        long b = 0;
        long x = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            //grab letters starting at end of string and work down
            x = toByteVal(s.charAt((s.length() - 1) - i));
            //add a shifted over x to b
            b += x << (2 * i);
        }
        return b;
    }

    /**
     * Converts a string char to a byte val if it's legit (a, c, g, t)
     *
     * @param c the char
     * @return the corresponding long val
     */
    private long toByteVal(char c) {
    	switch(c) {
	    	case 'a':
	    		return 0;
	    	case 'c':
	    		return 1;
	    	case 'g':
	    		return 2;
	    	case 't':
	    		return 3;
	    	default:
	    		return -1;
    	}
    }
    
    /**
     * Converts the given number to a char.
     * 
     * @param num Number to convert
     * 
     * @return Corresponding letter to num, 'n' if num is invalid
     */
    private char numToChar(long num) {
    	switch((int)num) {
	    	case 0:
	    		return 'a';
	    	case 1:
	    		return 'c';
	    	case 2:
	    		return 'g';
	    	case 3:
	    		return 't';
	    	default:
	    		return 'n';
    	}
    }
    
    /**
     * Convert this key to a string.
     * 
     * @return String representation of this TreeObject's key
     */
    public String keyToString() {
        String ret = "";
        long reducedKey = keyLongVal;

        for (int i = sequenceLength - 1; i >= 0; i--) {
            //read bits starting at end of key and work down
        	ret += numToChar(reducedKey >> (2 * i));
        	reducedKey -= (reducedKey >> (2 * i)) << (2 * i);
        }
        return ret;
    }

    /**
     * Determines if the left TreeObject's b value is larger than the parameter
     * "right" TreeObject's key value TreeObjects that are being inserted into a
     * BTree go through this method. If left is equal or less than right, then they
     * go the same direction. The return value is exact in terms of the difference
     * of the two long values.
     *
     * @param right the TreeObject being compared to the current TreeObject
     * @return -1 if left is less than right, + 1 if left is larger than right, 0 if
     *         equal
     */
    public int compare(TreeObject right) {
        long leftK = this.getKey();// for easier code explanation in documentation
        long rightK = right.getKey();
        if (leftK < rightK) {
            return -1;
        }
        if (leftK > rightK) {
            return 1;
        }
        return 0;
    }
    
    
   	//=================================================================================================================
	//                                            GET/SET/UTILITY METHODS
	//=================================================================================================================
    

    /**
     * @return the frequency of the TreeObject
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param newFrequency the integer that will replace the current frequency value
     */
    public void setFrequency(int newFrequency) {
        this.frequency = newFrequency;
    }

    /**
     * Increment the frequency of this object by 1
     */
    public void incrementFrequency() {
        frequency++;
    }
    
    /**
     * Gets the long key.
     *
     * @return the long key
     */
    public long getKey() {
        return keyLongVal;
    }

    @Override
    public String toString() {
        return keyToString() + " : " + frequency;
    }
    //suggestion: return keyToString() + ": " + frequency;, to match dump project specifications
    
   	//=================================================================================================================
	//                                            STATIC METHODS
	//=================================================================================================================
    
    /**
     * Set the shared static sequence length of all TreeObjects.
     * 
     * @param k How long read sequences will be
     * 
     * @throws IllegalArgumentException If given k is invalid (< 1 || > 31)
     */
    static public void setSequenceLength(int k) throws IllegalArgumentException{
    	if(k < 1 || k > 31) {
    		throw new IllegalArgumentException("Invalid sequence length set: " + k);
    	}
    	sequenceLength = k;
    }

}