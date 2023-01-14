// Generated on Sun Nov 13 21:42:10 HKT 2022
// DTD/Schema  :    http://sqlmap.rql.org/rql-mapper
package com.ruimin.helper.dom.rql.model;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

/**
 * http://sqlmap.rql.org/rql-mapper:ifElemType interface.
 * @author shiwei
 */
public interface If extends DomElement {

	/**
	 * Returns the value of the test child.
	 * @return the value of the test child.
	 */
	@NotNull
	@Attribute ("test")
	@Required
	GenericAttributeValue<String> getTestAttr();


	/**
	 * Returns the value of the test child.
	 * @return the value of the test child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getTest();


	/**
	 * Returns the value of the simple content.
	 * @return the value of the simple content.
	 */
	@NotNull
	@Required
	String getValue();
	/**
	 * Sets the value of the simple content.
	 * @param value the new value to set
	 */
	void setValue(@NotNull String value);


}
