/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.test;

public class TestMove {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String[] attrValues = {"111","222","333","444","555"};
		String[] chgAttrValues = null; 
		
		// test up
		for (int iPos=0; iPos<attrValues.length; iPos++) {
			chgAttrValues = moveItem(iPos, "up", attrValues);
			printArray(chgAttrValues, iPos, "up");
		}
		// test down
		for (int iPos=0; iPos<attrValues.length; iPos++) {
			chgAttrValues = moveItem(iPos, "down", attrValues);
			printArray(chgAttrValues, iPos, "down");
		}
		// test top
		for (int iPos=0; iPos<attrValues.length; iPos++) {
			chgAttrValues = moveItem(iPos, "top", attrValues);
			printArray(chgAttrValues, iPos, "top");
		}
		// test bottom
		for (int iPos=0; iPos<attrValues.length; iPos++) {
			chgAttrValues = moveItem(iPos, "bottom", attrValues);
			printArray(chgAttrValues, iPos, "bottom");
		}

	}

	/**
	 * @param iPos
	 * @param position
	 * @param attrValues
	 * @return
	 */
	private static String[] moveItem(int iPos, String position, String[] attrValues) {
		String[] chgAttrValues = new String[attrValues.length];
		
		// get position type 
		if (position.equals("top")) {
			// build new array
			if (iPos > 0) {
				chgAttrValues = changeAttrValueOrder(attrValues, iPos, 0, true);
			} else {
				System.out.println("************ top failed for: " + iPos);
			}
		}
		if (position.equals("bottom")) {
			// build new array
			if (iPos < attrValues.length-1) {
				chgAttrValues = changeAttrValueOrder(attrValues, iPos, attrValues.length-1, false);
			} else {
				System.out.println("************ bottom failed for: " + iPos);
			}
		}
		if (position.equals("up")) {
			// build new array
			if (iPos > 0) {
				chgAttrValues = changeAttrValueOrder(attrValues, iPos, iPos-1, true);
			} else {
				System.out.println("************ up failed for: " + iPos);
			}
		}
		if (position.equals("down")) {
			// build new array
			if (iPos < attrValues.length-1) {
				chgAttrValues = changeAttrValueOrder(attrValues, iPos, iPos+1, false);
			} else {
				System.out.println("************ down failed for: " + iPos);
			}
		}
		return chgAttrValues;
	}
	
	/**
	 * @param attrValues
	 * @param iPos
	 * @param attrToMove
	 * @param chgAttrValues
	 */
	private static String[] changeAttrValueOrder(String[] attrValues, int curPos, int newPos, boolean moveUp) {
		String[] chgAttrValues = new String[attrValues.length];
		System.out.println("#### curPos: " + curPos + " / newPos: " + newPos);
		int j=0;
		for (int i=0; i<attrValues.length; i++) {
			if (i!=curPos && i!=newPos) {
				chgAttrValues[j++] = attrValues[i];
			} else if (i==newPos && moveUp) {
				chgAttrValues[j++] = attrValues[curPos];
				chgAttrValues[j++] = attrValues[newPos];
			} else if (i==newPos && !moveUp) {
				chgAttrValues[j++] = attrValues[newPos];
				chgAttrValues[j++] = attrValues[curPos];
			} 
		}
		return chgAttrValues;
	}

	private static void printArray(String[] sArray, int iPos, String op) {
		
		System.out.println("***** " + iPos + " / " + op + " *****");
		for (int i=0; i<sArray.length; i++) {
			System.out.println("Value[" + i + "] => " + sArray[i]);
		}
		
	}

}
