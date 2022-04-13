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
    // valid treeObjects: "A", "T", "C", "G"
    // corresponding 2-bit binary value: 00, 11, 01, 10
    // placeholder values: 99(for 00) 98(for 01)
    long treeObjectKey;
    private int frequency;

    /**
     * Constructor: Creates a TreeObject with the specified key. Also sets the value
     * of b depending on the possibleKey. The key values in treeObjectKey must all
     * consist of "A", "C", "G", or "T" to convert to a usable long value.
     * Placeholder values are used initially for easier access in other methods.
     *
     * @param treeObjectKey the key of the object
     * @param frequency   size of each, individual sequence in possibleKey
     */
    public TreeObject(E treeObjectKey, int frequency) {
        String s = (String) treeObjectKey;
        String str = s.replaceAll("A", "99").replaceAll("T", "11").replaceAll("C", "98").replaceAll("G", "10");
        this.treeObjectKey = Long.parseLong(str);
        this.frequency = frequency;
    }

    /**
     * Returns the long value of the treeObjectKey after getting rid of placeholder
     * values.
     *
     * @return the key of the TreeObject
     */
    public long getKey() {
        return Long.parseLong(withZeros());
    }

    /**
     * Replaces all placeholder values with their corresponding values
     *
     * @return a String value with the correct binary values
     */
    public String withZeros() {
        String s = Long.toString(treeObjectKey);
        return s.replaceAll("99", "00").replaceAll("98", "01");
    }

    /**
     * @return the frequency of the TreeObject
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Determines if the left TreeObject's b value is larger than the parameter
     * "right" TreeObject's key value TreeObjects that are being inserted into a BTree
     * go through this method. If left is equal or less than right, then they go the
     * same direction. The return value is exact in terms of the difference of the
     * two long values.
     *
     * @param right the TreeObject being compared to the current TreeObject
     * @return negative number if left is less than right, positive number if left
     *         is larger than right, 0 if equal
     */
    public int compare(TreeObject<E> right) {
        long left = this.getKey();// for easier code explanation in documentation
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
            String str = s.replaceAll("A", "00").replaceAll("T", "11").replaceAll("C", "01").replaceAll("G", "10");
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
        String tOk = Long.toString(treeObjectKey);
        tOk = tOk.replaceAll("99", "A").replaceAll("98", "C").replaceAll("10", "G").replaceAll("11", "T");
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
     * A String form of the TreeObject's key and frequency, formatted for dump files.
     *
     * @return a String representation of a TreeObject
     */
    public String toString() {
        String s = Long.toString(treeObjectKey).replaceAll("99", "A").replaceAll("11", "T").replaceAll("98", "C")
                .replaceAll("10", "G");
        s += ": " + frequency;
        return s;
    }

}