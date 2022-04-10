package cs321.btree;

/**
 * An object that is to be stored in a BTree.
 * Specific placement in the BTree is determined by previous TreeObjects and key values.
 * A notable method is compare(), which determines which object is larger in key value.
 *
 * @authors Aaron Goin, Mesa Greear
 * @version Spring 2022
 *
 * @param <E> Generic Type for this TreeObject to hold
 */
public class TreeObject<E> {
    //valid treeObjects:        "A", "T", "C", "G"
    //corresponding byte value:  00,  11,  01,  10
    private E treeObjectKey;
    private byte b;

    /**
     * Constructor: Creates a TreeObject with the specified key.
     * Also sets the value of b depending on the possibleKey.
     * The key must be "A", "C", "G", or "T" for treeObjectKey to not be null.
     * -1 is used to equal b if the possibleKey is invalid
     *
     * @param possibleKey a key that may get assigned to the object, if listed above
     */
    public TreeObject(E possibleKey) {
        b = toBinary(possibleKey);
        if (b == -1) {
            treeObjectKey = null;
        } else {
            treeObjectKey = possibleKey;
        }
    }

    /**
     * Converts treeObjectKey to a 2-bit binary number.
     * This helps preserve memory.
     * Used in constructor
     *
     * @param possibleKey a key that is being evaluated
     * @return byte that represents the key, or -1 if treeObjectKey isn't valid
     */
    private byte toBinary(E possibleKey) {
        byte b;
        if (possibleKey.equals("A")) {
            b = 0b00;
        } else if (possibleKey.equals("T")) {
            b = 0b11;
        } else if (possibleKey.equals("C")) {
            b = 0b01;
        } else if (possibleKey.equals("G")) {
            b = 0b10;
        } else {
            b = -1;
        }
        return b;
    }

    /**
     * @return the key of the TreeObject
     */
    public E getElement() {
        return treeObjectKey;
    }

    /**
     * Setter for treeObjectKey (and corresponding b) if newKey is "A", "C", "G", or "T".
     *
     * @param newKey the new key of the TreeObject, if listed above
     */
    public void setElement(E newKey) {
        if (newKey == "A" || newKey == "C" || newKey == "G" || newKey == "T") {
            treeObjectKey = newKey;
            b = toBinary(newKey);
        }
    }

    /**
     * @return b, the byte representation of the key
     */
    public byte getB() {
        return b;
    }

    /**
     * Determines if the left TreeObject's b value is larger than the parameter "right" TreeObject's b value
     * TreeObjects that are being inserted into a BTree go through this method.
     * If left is equal or less than right, then they go the same direction.
     * The return value is exact in terms of the difference of the two byte values.
     *
     * @param right the TreeObject being compared to the current TreeObject
     * @return negative number if left is less than right, positive number if left is larger than right,
     * 0 if equal
     */
    public int compare(TreeObject<E> right) {
        byte left = b;// for easier code explanation in documentation
        return Byte.compare(left, right.getB());
    }

    /**
     * A String form of the TreeObject. It's simply the key in raw and binary form
     *
     * @return a String representation of a TreeObject
     */
    public String toString() {
        String byteS;
        if (b == 0) {
            byteS = "00";
        } else if (b == 11) {
            byteS = "11";
        } else if (b == 1) {
            byteS = "01";
        } else if (b == 10) {
            byteS = "10";
        } else {
            byteS = "-1";
        }

        return "TreeObject Key: " + treeObjectKey + "\n TreeObject Binary Number: " + byteS + "\n";
    }
}