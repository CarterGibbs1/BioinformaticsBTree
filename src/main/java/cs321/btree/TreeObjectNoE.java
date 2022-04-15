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
public class TreeObjectNoE {
    // valid treeObjects: "a", "t", "c", "g" (lowercase)
    // corresponding 2-bit binary value: 00, 11, 01, 10
    private long treeObjectKey;
    private int frequency;
    private int stringLengthStartAC = -1;

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
    public TreeObjectNoE(String treeObjectKey, int frequency) {
        String s = treeObjectKey;
        if (s == null || frequency < 1 || frequency > s.length() || treeObjectKey.length() > 31) {
            this.treeObjectKey = -1;
            this.frequency = -1;
            return;
        }
        if (s.charAt(0) == 'a' || s.charAt(0) == 'c') {
            getZeroPlacement(s);
        }

        this.treeObjectKey = byteShift(s);
        this.frequency = frequency;
    }

    /**
     * Set values that will help get exact key values if key starts with a or c.
     *
     * @param s the String of the key
     */
    private void getZeroPlacement(String s) {
        stringLengthStartAC = s.length();
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
            b += charToByteVal(s.charAt(i));
            if (i < s.length() - 1) {
                b = b << 2;
            }
        }
        return b;
    }

    /**
     * A key for byteShift to determine the correct value for each letter
     *
     * @param c the letter being evaluated
     * @return the corresponding 2-bit binary value
     */
    private long charToByteVal(char c) {
        if (c == 'a') {
            return 0;
        }
        if (c == 'c') {
            return 1;
        }
        if (c == 'g') {
            return 2;
        }
        if (c == 't') {
            return 3;
        }
        return -1;
    }

    /**
     * @return the key of the TreeObject
     */
    public long getKey() {
        return treeObjectKey;
    }

    /**
     * Sets new key for current TreeObject. By doing so, a new object must be
     * created to ensure correct values are maintained.
     *
     * @param treeObjectKey the new treeObjectKey for this TreeObject
     */
    public void setKey(String treeObjectKey) {
        TreeObjectNoE replacement = new TreeObjectNoE(treeObjectKey, frequency);
        this.treeObjectKey = replacement.getKey();
        this.frequency = replacement.getFrequency();
        this.stringLengthStartAC = replacement.stringLengthStartAC;
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
     * @return -1 if left is less than right, + 1 if left is larger than right, 0 if
     *         equal
     */
    public int compare(TreeObjectNoE right) {
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
        if (treeObjectKey == -1) {
            return "null";
        }
        return stringWithLetters() + ": " + frequency;
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
     * Replaces all placeholder values with their corresponding values
     *
     * @return a String value with the correct binary values
     */
    public String withZeros() {
        if (treeObjectKey == -1) {
            return "null";
        }
        String s = "";
        String bS = Long.toBinaryString(treeObjectKey);
        if (stringLengthStartAC != -1) {
            int j = bS.length();
            while (j < stringLengthStartAC * 2) {
                s += "0";
                j++;
            }
            return s + bS;
        }
        return bS;
    }

}