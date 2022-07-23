// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.model;

/**
 * http://www.rmitec.cn/dtst-mapper:datatypeAttrType enumeration.
 * @author shiwei
 */
public enum Datatype implements com.intellij.util.xml.NamedEnum {
	BOOLEAN ("boolean"),
	CURRENCY ("currency"),
	DATE ("date"),
	DOUBLE ("double"),
	INT ("int"),
	NUMBER ("number"),
	STRING ("string"),
	TIME ("time"),
	TIMESTAMP ("timestamp");

	private final String value;
	private Datatype(String value) { this.value = value; }
	public String getValue() { return value; }

}
