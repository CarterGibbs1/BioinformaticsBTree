package cs321.btree;


/**
 * An object that is to be stored in a BTree. Specific placement in the BTree is
 * determined by previous TreeObjects and key values. A notable method is
 * compare(), which determines which object is larger in key value.
 *
 * @author Aaron Goin, Mesa Greear
 * @version Spring 2022
 *
 * @param <E> Generic Type for this TreeObject to hold
 */
public class TreeObject<E> {
    // valid treeObjects: "a", "t", "c", "g" (lowercase)
    // corresponding 2-bit binary value: 00, 11, 01, 10
    private long treeObjectKey;
    private int frequency;
    private int numZeros = -1;

    /**
     * Constructor: Creates a TreeObject with the specified key. Also sets the value
     * of b depending on the possibleKey. The key values in treeObjectKey must all
     * consist of "a", "c", "g", or "t" to convert to a usable long value. -1 will
     * be the value for instance variables if treeObjectKey is too large for a long
     * value or frequency is below 0/greater than the length of the object. If first
     * value is a or c, it will be replaced for future method utilization.
     *
     * @param treeObjectKey the key of the object
     * @param frequency     size of each, individual sequence in possibleKey
     */
    public TreeObject(E treeObjectKey, int frequency) {
        String s = (String) treeObjectKey;
        if (frequency < 1 || frequency > s.length()) {
            this.treeObjectKey = -1;
            this.frequency = -1;
            return;
        }
        if (s.charAt(0) == 'a' || s.charAt(0) == 'c') {
            getZeroPlacement(s);
        }

        String b = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'a') {
                b += "00";
            }
            if (s.charAt(i) == 'c') {
                b += "01";
            }
            if (s.charAt(i) == 'g') {
                b += "10";
            }
            if (s.charAt(i) == 't') {
                b += "11";
            }
        }
        if (b.length() > 62) {
            this.treeObjectKey = -1;
            this.frequency = -1;
        } else {
            this.treeObjectKey = Long.parseLong(b, 2);
            this.frequency = frequency;
        }
    }

    /**
     * Set values that will help get exact key values if key starts with a or c.
     *
     * @param s the String of the key
     */
    private void getZeroPlacement(String s) {
        int stopIndex = 0;
        numZeros = 0;
        while (s.length() > stopIndex && (s.charAt(stopIndex) != 'g' && s.charAt(stopIndex) != 't')) {
            if (s.charAt(stopIndex) == 'a') {
                stopIndex++;
                numZeros++;
                numZeros++;
            } else if (s.charAt(stopIndex) == 'c') {
                numZeros++;
                break;
            }
        }
    }

    /**
     * @return the key of the TreeObject
     */
    public long getKey() {
        return treeObjectKey;
    }

    /**
     * Replaces all placeholder values with their corresponding values
     *
     * @return a String value with the correct binary values
     */
    public String withZeros() {
        String s = "";
        if (numZeros != -1) {
            int j = 0;
            while (j < numZeros) {
                s += "0";
                j++;
            }
        }
        return s + Long.toBinaryString(treeObjectKey);
    }

    /**
     * @return the frequency of the TreeObject
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param newFrequency the integer that will replace the current frequency value
     *                     if it's valid
     */
    public void setFrequency(int newFrequency) {
        if (newFrequency < 1 || newFrequency > stringWithLetters().length()) {
            return;
        }
        this.frequency = newFrequency;
    }

    /**
     * Determines if the left TreeObject's b value is larger than the parameter
     * "right" TreeObject's key value TreeObjects that are being inserted into a
     * BTree go through this method. If left is equal or less than right, then they
     * go the same direction. The return value is exact in terms of the difference
     * of the two long values.
     *
     * @param right the TreeObject being compared to the current TreeObject
     * @return negative number if left is less than right, positive number if left
     *         is larger than right, 0 if equal
     */
    public int compare(TreeObject<E> right) {
        long left = this.getKey();// for easier code explanation in documentation
        if (right == null) {
            return Long.compare(left, -1);
        }
        return Long.compare(left, right.getKey());
    }

    /**
     * Creates a String array that holds all binary sequences of length frequency
     *
     * @return a String array with all binary sequences
     */
    public String[] binarySequences() {
        String[] kTSA = stringSequences();
        int j = 0;
        for (String s : kTSA) {
            String str = s.replaceAll("a", "00").replaceAll("t", "11").replaceAll("c", "01").replaceAll("g", "10");
            kTSA[j] = str;
            j++;
        }
        return kTSA;
    }

    /**
     * Creates a String array that holds all String sequences of length frequency
     *
     * @return a String array with all String sequences
     */
    public String[] stringSequences() {
        String tOk = stringWithLetters();
        String[] s = new String[tOk.length() - frequency + 1];
        for (int i = 0; i <= tOk.length() - frequency; i++) {
            String t = "";
            int freqCount = 0;
            int j = i;
            while (freqCount < frequency) {
                t += tOk.charAt(j);
                freqCount++;
                j++;
            }
            s[i] = t;
        }
        return s;
    }

    /**
     * @return the treeObjectKey in lettered form
     */
    private String stringWithLetters() {
        String s = withZeros();
        String b = "";
        for (int i = 0; i < s.length() - 1; i += 2) {
            String str = s.substring(i, i + 2);
            if (str.equals("00")) {
                b += "a";
            }
            if (str.equals("01")) {
                b += "c";
            }
            if (str.equals("10")) {
                b += "g";
            }
            if (str.equals("11")) {
                b += "t";
            }
        }
        return b;
    }

    /**
     * A String form of the TreeObject's "lettered" key and frequency, formatted for
     * dump files.
     *
     * @return a String representation of a TreeObject
     */
    public String toString() {
        return stringWithLetters() + ": " + frequency;
    }

}