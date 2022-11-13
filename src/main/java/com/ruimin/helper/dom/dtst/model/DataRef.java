// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.dtst.model;

import com.intellij.util.xml.*;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:DataRefType interface.
 * @author shiwei
 */
public interface DataRef extends DomElement {

	/**
	 * Returns the value of the id child.
	 * <pre>
	 * <h3>Attribute null:id documentation</h3>
	 * 数据集别名
	 * </pre>
	 * @return the value of the id child.
	 */
	@NotNull
	@Required
	@Attribute("id")
	GenericAttributeValue<String> getId();


	/**
	 * Returns the value of the dspath child.
	 * <pre>
	 * <h3>Attribute null:dspath documentation</h3>
	 * 数据集路径
	 * </pre>
	 * @return the value of the dspath child.
	 */
	@NotNull
	@Required
	@Attribute("dspath")
	GenericAttributeValue<String> getDspath();


}
