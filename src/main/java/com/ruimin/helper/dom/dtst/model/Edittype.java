// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.dtst.model;

/**
 * http://www.rmitec.cn/dtst-mapper:edittypeAttrType enumeration.
 * @author shiwei
 */
public enum Edittype implements com.intellij.util.xml.NamedEnum {
	CHECKBOX ("checkbox"),
	CHECKBOXS ("checkboxs"),
	COMBOBOX ("combobox"),
	COMBODIALOG ("combodialog"),
	COMBOGRID ("combogrid"),
	COMBOTREE ("combotree"),
	CURRENCYBOX ("currencybox"),
	DATALABEL ("datalabel"),
	DATEBOX ("datebox"),
	DATETIMEBOX ("datetimebox"),
	DOUBLEBOX ("doublebox"),
	FILEBOX ("filebox"),
	INTEGERBOX ("integerbox"),
	NUMBERBOX ("numberbox"),
	PASSWORD ("password"),
	PROGRESS ("progress"),
	RADIOBOX ("radiobox"),
	RADIOBOXS ("radioboxs"),
	TEXTAREA ("textarea"),
	TEXTBOX ("textbox"),
	TIMEBOX ("timebox");

	private final String value;
	private Edittype(String value) { this.value = value; }
	public String getValue() { return value; }

}
