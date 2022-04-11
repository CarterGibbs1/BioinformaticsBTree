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
    // valid treeObjects: "A", "T", "C", "G"
    // corresponding byte value: 00, 11, 01, 10
    private E treeObjectKey;
    private byte b = 0;
    private String stringOfBytes;

    /**
     * Constructor: Creates a TreeObject with the specified key.
     * Also sets the value of b depending on the possibleKey.
     * The key must be "A", "C", "G", or "T" for treeObjectKey to not be null.
     * -1 is used to equal b if the possibleKey is invalid
     *
     * @param possibleKey a key that may get assigned to the object, if all chars are listed above
     */
    public TreeObject(E possibleKey) {
        char[] c = keyToCharArray(possibleKey);
        StringBuilder toStringChars = new StringBuilder();
        for (int i = 0; i < c.length; i++) {
            byte index = toBinary(c[i]);
            if (index == -1) {
                b = -1;
                break;
            }
            toStringChars.append(charToBinary(c[i]));
            b += index;
        }
        if (b == -1) {
            treeObjectKey = null;
            stringOfBytes = null;
        } else {
            treeObjectKey = possibleKey;
            stringOfBytes = toStringChars.toString();
        }
    }

    /**
     * Used to simplify processes where a char array is needed to find an overall byte value or toString.
     *
     * @param objectKey an element value that may get converted to a treeObjectKey
     * @return a char array of the objectKey
     */
    private char[] keyToCharArray(E objectKey) {
        String s = (String) objectKey;
        return s.toCharArray();
    }

    /**
     * Converts a char in a treeObjectKey to a 2-bit binary number. This helps
     * preserve memory. Used in constructor.
     *
     * @param possibleKeyChar a char in a key that is being evaluated
     * @return byte that represents the key, or -1 if treeObjectKey isn't valid
     */
    private byte toBinary(char possibleKeyChar) {
        byte b;
        if (possibleKeyChar == 'A') {
            b = 0b00;
        } else if (possibleKeyChar == 'T') {
            b = 0b11;
        } else if (possibleKeyChar == 'C') {
            b = 0b01;
        } else if (possibleKeyChar == 'G') {
            b = 0b10;
        } else {
            b = -1;
        }
        return b;
    }

    /**
     * Converts a char to a 2-bit binary number if it is a valid TreeObject
     *
     * @param c a character that can get converted to its respective 2-bit binary number
     * @return a String of the corresponding binary number of the char
     */
    private String charToBinary(char c) {
        String byteS;
        if (c == 'A') {
            byteS = "00";
        } else if (c == 'T') {
            byteS = "11";
        } else if (c == 'C') {
            byteS = "01";
        } else if (c == 'G') {
            byteS = "10";
        } else {
            byteS = "-1";
        }
        return byteS;
    }

    /**
     * @return the key of the TreeObject
     */
    public E getElement() {
        return treeObjectKey;
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
        return stringOfBytes;
    }
}
/*
 * The following are ideas I have for a new way to implement this class if needed, as I read the frequency will
 * need to be something this object knows about.
 */
//
//
///**
// * An object that is to be stored in a BTree. Specific placement in the BTree is
// * determined by previous TreeObjects and key values. A notable method is
// * compare(), which determines which object is larger in key value.
// *
// * @author Aaron Goin
// * @version Spring 2022
// *
// * @param <E> Generic Type for this TreeObject to hold
// */
//public class TreeObjectOldTwo<E> {
//	// valid treeObjects: "A", "T", "C", "G"
//	// corresponding byte value: 00, 11, 01, 10
//	private E treeObjectKey;
//	private int frequency;
//
//	/**
//	 * Constructor: Creates a TreeObject with the specified key. Also sets the value
//	 * of b depending on the possibleKey. The key values must all be "A", "C", "G",
//	 * or "T" for treeObjectKey to not be null. -1 is used to equal b if the
//	 * possibleKey is invalid
//	 *
//	 * @param possibleKey the key of the object
//	 * @param frequency size of each, individual sequence in possibleKey
//	 */
//	public TreeObjectOldTwo(E treeObjectKey, int frequency) {
//		this.treeObjectKey = treeObjectKey;
//		this.frequency = frequency;
//	}
//
//	/**
//	 * @return the key of the TreeObject
//	 */
//	public E getElement() {
//		return treeObjectKey;
//	}
//
//	/**
//	 * Determines if the left TreeObject's b value is larger than the parameter
//	 * "right" TreeObject's b value TreeObjects that are being inserted into a BTree
//	 * go through this method. If left is equal or less than right, then they go the
//	 * same direction. The return value is exact in terms of the difference of the
//	 * two byte values.
//	 *
//	 * @param right the TreeObject being compared to the current TreeObject
//	 * @return negative number if left is less than right, positive number if left
//	 *         is larger than right, 0 if equal
//	 */
//	public int compare(TreeObjectOldTwo<E> right) {
//		byte left = this.getBValue();// for easier code explanation in documentation
//		return Byte.compare(left, right.getBValue());
//	}
//
//	/**
//	 * @return b, the byte representation of the key
//	 */
//	private byte getBValue() {
//		String s = (String) treeObjectKey;
//		char[] c = s.toCharArray();
//		byte b = 0;
//		for (int i = 0; i < c.length; i++) {
//			byte index = toBinary(c[i]);
//			if (index == -1) {
//				return -1;
//			}
//			b += index;
//		}
//		return b;
//	}
//
//	/**
//	 * Converts a char in a treeObjectKey to a 2-bit binary number. This helps
//	 * preserve memory. Used in constructor.
//	 *
//	 * @param possibleKeyChar a char in a key that is being evaluated
//	 * @return byte that represents the key, or -1 if treeObjectKey isn't valid
//	 */
//	private byte toBinary(char possibleKeyChar) {
//		byte b;
//		if (possibleKeyChar == 'A') {
//			b = 0b00;
//		} else if (possibleKeyChar == 'T') {
//			b = 0b11;
//		} else if (possibleKeyChar == 'C') {
//			b = 0b01;
//		} else if (possibleKeyChar == 'G') {
//			b = 0b10;
//		} else {
//			b = -1;
//		}
//		return b;
//	}
//
//	public String[] getEntireB() {// all possible sequences
//		String l[] = new String[howManySequences()];
//		String b = binarySequences();
//		String s[] = keyToStringArray();
//		int i = 0;
//		int j = 0;
//		for(String str : s) {
//			char ch[] = str.toCharArray();
//			String string = "";
//			j = frequency * i;
//			for (char c : ch) {
//				string += b.charAt(j);
//				j++;
//				string += b.charAt(j);
//				j++;
//			}
//			l[i] = string;
//			i++;
//		}
//		return l;
//	}
//
//	private int howManySequences() {
//		int start = treeObjectKey.toString().length() + 1;
//		int x = -1 * (frequency);
//		return x + start;
//	}
//
//	/**
//	 * Used to simplify processes where a char array is needed to find an overall byte value or toString.
//	 *
//	 * @return a char array of the objectKey
//	 */
//	public String[] keyToStringArray() {
//		String tOK = treeObjectKey.toString();
//		String[] s = new String[tOK.length() - frequency + 1];
//		for (int i = 0; i <= tOK.length() - frequency; i++) {
//			String t = "";
//			int freqCount = 0;
//			int j = i;
//			while (freqCount < frequency) {
//				t += tOK.charAt(j);
//				freqCount++;
//				j++;
//			}
//			s[i] = t;
//		}
//		return s;
//	}
//
//	public String binarySequences() {
//		String bS = treeObjectKey.toString().replaceAll("A", "00").replaceAll("T", "11")
//				.replaceAll("C", "01").replaceAll("G", "10");
//		return bS;
//	}
//
////	public byte getEntireB() {
////		String s = binarySequences();
//		String[] t = keyToStringArray();
////		byte a[] = new byte[s.length()];
////		for (int i = 0; i < s.length(); i++) {
////
////			for (int j = 0; j < frequency; j++) {
////
////			}
////			a[i] = Byte.parseByte();
////		}
////		ByteBuffer b = ByteBuffer.wrap(s);
////		return b.get();
////	}
//
//
//	/**
//	 * A String form of the TreeObject. It's simply the key in raw and binary form
//	 *
//	 * @return a String representation of a TreeObject
//	 */
//	public String toString() {
//		return (String) treeObjectKey;
//	}
//
//}