// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dtst.dom.model;

/**
 * http://www.rmitec.cn/dtst-mapper:typeAttrType enumeration.
 * @author shiwei
 */
public enum Type implements com.intellij.util.xml.NamedEnum {
	ADDRECORD ("addrecord"),
	ASYNCDELETE ("asyncdelete"),
	ASYNCSUBMIT ("asyncsubmit"),
	DELRECORD ("delrecord"),
	HREF ("href"),
	NONE ("none"),
	SYNCSUBMIT ("syncsubmit"),
	CALL ("call");

	private final String value;
	private Type(String value) { this.value = value; }
	public String getValue() { return value; }

}
