// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.model;

/**
 * http://www.rmitec.cn/dtst-mapper:alignAttrType enumeration.
 * @author shiwei
 */
public enum Align implements com.intellij.util.xml.NamedEnum {
	CENTER ("center"),
	LEFT ("left"),
	RIGHT ("right");

	private final String value;
	private Align(String value) { this.value = value; }
	public String getValue() { return value; }

}
