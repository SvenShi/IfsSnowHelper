// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.model;

/**
 * http://www.rmitec.cn/dtst-mapper:statusAttrType enumeration.
 * @author shiwei
 */
public enum Status implements com.intellij.util.xml.NamedEnum {
	Status_A ("A"),
	Status_D ("D"),
	Status_F ("F"),
	Status_N ("N");

	private final String value;
	private Status(String value) { this.value = value; }
	public String getValue() { return value; }

}
