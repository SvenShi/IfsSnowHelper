// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:FieldModelElemType interface.
 * @author shiwei
 */
public interface FieldModel extends DomElement {

	/**
	 * Returns the list of Field children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:Field documentation</h3>
	 * 字段信息
	 * </pre>
	 * @return the list of Field children.
	 */
	@NotNull
	@SubTagList("Field")
	java.util.List<Field> getFields();
	/**
	 * Adds new child to the list of Field children.
	 * @return created child
	 */
	@SubTagList("Field")
	Field addField();


}
