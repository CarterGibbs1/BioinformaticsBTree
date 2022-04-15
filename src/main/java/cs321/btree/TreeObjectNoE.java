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
    private String stringTreeObjectKey;
    private long treeObjectKey;
    private int frequency;

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
    public TreeObjectNoE(String stringTreeObjectKey, long treeObjectKey, int frequency) {
        this.stringTreeObjectKey = stringTreeObjectKey;
        this.treeObjectKey = treeObjectKey;
        this.frequency = frequency;
    }

    public String getStringTreeObjectKey() {
        return stringTreeObjectKey;
    }

    /**
     * @return the key of the TreeObject
     */
    public long getLongKey() {
        return treeObjectKey;
    }

    public void setKeys(String stringTreeObjectKey, long treeObjectKey) {
        this.stringTreeObjectKey = stringTreeObjectKey;
        this.treeObjectKey = treeObjectKey;
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
        long leftK = this.getLongKey();// for easier code explanation in documentation
        long rightK = right.getLongKey();
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
        return stringTreeObjectKey + ": " + frequency;
    }

}