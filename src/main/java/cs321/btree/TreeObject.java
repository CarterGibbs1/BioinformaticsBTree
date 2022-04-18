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
    private String treeObjectKey;
    private int frequency;
    private long keyLongVal;

    /**
     * Constructor: Creates a TreeObject with the specified String key. Also sets the value
     * of b depending on the String key. The key values in treeObjectKey must all
     * consist of "a", "c", "g", or "t" to convert to a usable long value. Everything is set.
     *
     * @param treeObjectKey the String key of the object
     * @param frequency     size of each, individual sequence in possibleKey
     */
    public TreeObject(String treeObjectKey, int frequency) {
        this.treeObjectKey = treeObjectKey;
        this.keyLongVal = setLongKey();
        this.frequency = frequency;
    }

    /**
     * Alternate constructor, long key instead of string is passed in. String key is a blank String.
     *
     * @param longKey
     * @param frequency
     */
    public TreeObject(long longKey, int frequency) {
        this.treeObjectKey = "";// needs to be set later possibly, created for compatibility with other methods
        this.keyLongVal = longKey;
        this.frequency = frequency;
    }

    /**
     * Blank constructor, only sets long key to -1
     */
    public TreeObject() {//for testing purposes
        this.keyLongVal = -1;
    }

    /**
     * @return the String key of the TreeObject
     */
    public String getStringKey() {
        return treeObjectKey;
    }

    /**
     * Sets the String key
     * @param newKey the String key that will replace the current one.
     */
    public void setKey(String newKey) {
        this.treeObjectKey = newKey;
    }

    /**
     * Intended to be used by another driver class when the string length is known, but the second constructor is used.
     *
     * @param stringLength length of string after being scanned
     */
    public void setBlankKeyWithStringLength(int stringLength) {
        String longKey = "";
        for (int i = 0; i < stringLength * 2; i++) {
            longKey += "0";
        }
        longKey += Long.toBinaryString(keyLongVal);
        longKey = longKey.substring(0, Long.toBinaryString(keyLongVal).length() + stringLength - 1);

        this.treeObjectKey = stringOfExactLongString(longKey);
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
     * @return the String of the corresponding letter, blank string if num isn't listed below
     */
    private String numToLetter(String num) {
        if (num.equals("00")) {
            return "a";
        }
        if (num.equals("01")) {
            return "c";
        }
        if (num.equals("10")) {
            return "g";
        }
        if (num.equals("11")) {
            return "t";
        }
        return "";
    }

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

    public long setLongKey() {
        if (keyLongVal == -1) {
            return -1;
        }
        return byteShift(treeObjectKey);
    }

    /**
     * Gets the long key.
     *
     * @return the long key
     */
    public long getKey() {
        return keyLongVal;
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
        for (int i = 0; i < s.length(); i++) {
            b += toByteVal(s.charAt(i));
            if (i < s.length() - 1) {
                b = b << 2;
            }
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
        if (c == 'a') {
            return 00;
        }
        if (c == 'c') {
            return 01;
        }
        if (c == 'g') {
            return 10;
        }
        if (c == 't') {
            return 11;
        }
        return -1;
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

    /**
     * A String form of the TreeObject's "lettered" key and frequency, formatted for
     * dump files.
     *
     * @return a String representation of a TreeObject
     */
    public String toString() {
        if (getKey() == -1 || this.treeObjectKey.equals(null)) {
            return "null";
        }
        if (treeObjectKey == "") {
            return keyLongVal + ": " + frequency;
        }
        return treeObjectKey + ": " + frequency;
    }

}