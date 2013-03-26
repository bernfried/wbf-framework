/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.test;

import java.util.Iterator;
import java.util.Map;

import de.webertise.ds.manager.targetmgr.config.TMOBracket;
import de.webertise.ds.manager.targetmgr.config.TargetManagerConfig;
import de.webertise.ds.manager.targetmgr.config.TargetManagerObject;

/**
 * @author bernfried
 *
 */
public class TestTMOHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestTargetManagerConfig();
		
	}

	/**
	 * 
	 */
	private static void TestTargetManagerConfig() {
		
		// start
		System.out.println("Test TargetManagerConfig - Start");
		TargetManagerConfig config = TargetManagerConfig.getInstance();
		
		// get channelNews1 config
		TargetManagerObject channelNews1 = config.getConfig("channelNews1");
		System.out.println("** Name: " + channelNews1.getName());

		// show operations
		System.out.println("**** Brackets");
		Map<String, TMOBracket> brackets = channelNews1.getBrackets();
		
		// show all brackets
		Iterator<String> bracketList = brackets.keySet().iterator();
		while (bracketList.hasNext()) {
			String key = bracketList.next();
			TMOBracket bracket = brackets.get(key);
			System.out.println("Key: " + bracket.getKey());
			System.out.println("Id: " + bracket.getId());
			System.out.println("Parent: " + bracket.getParent());
			System.out.println("Value-Separator: " + bracket.getValueSeparator());
			System.out.println("Comp-Op: " + bracket.getCompareOperator());
			System.out.println("Log-Op-Bracket: " + bracket.getLogicalOperatorBracket());
			System.out.println("Log-Op-Value: " + bracket.getLogicalOperatorValue());
			System.out.println("Type: " + bracket.getType());
			System.out.println("*************************************");
		}

		// end
		System.out.println("Test TargetManagerConfig - End");
	}

}
