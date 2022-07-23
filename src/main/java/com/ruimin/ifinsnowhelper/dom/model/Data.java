// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.ifinsnowhelper.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:DataElemType interface.
 * @author shiwei
 */
@Namespace("DtstXml")
public interface Data extends DomElement {

	/**
	 * Returns the list of Define children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:Define documentation</h3>
	 * 数据集配置
	 * </pre>
	 * @return the list of Define children.
	 */
	@NotNull
	@SubTagList("Define")
	java.util.List<Define> getDefines();
	/**
	 * Adds new child to the list of Define children.
	 * @return created child
	 */
	@SubTagList("Define")
	Define addDefine();


	/**
	 * Returns the list of DataRef children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:DataRef documentation</h3>
	 * 数据集引用
	 * </pre>
	 * @return the list of DataRef children.
	 */
	@NotNull
	@SubTagList("DataRef")
	java.util.List<DataRef> getDataRefs();
	/**
	 * Adds new child to the list of DataRef children.
	 * @return created child
	 */
	@SubTagList("DataRef")
	DataRef addDataRef();


	/**
	 * Returns the list of Fields children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:Fields documentation</h3>
	 * 数据集字段
	 * </pre>
	 * @return the list of Fields children.
	 */
	@NotNull
	@SubTagList("Fields")
	java.util.List<Fields> getFieldses();
	/**
	 * Adds new child to the list of Fields children.
	 * @return created child
	 */
	@SubTagList("Fields")
	Fields addFields();


	/**
	 * Returns the list of Commands children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:Commands documentation</h3>
	 * 数据集操作
	 * </pre>
	 * @return the list of Commands children.
	 */
	@NotNull
	@SubTagList("Commands")
	java.util.List<Commands> getCommandses();
	/**
	 * Adds new child to the list of Commands children.
	 * @return created child
	 */
	@SubTagList("Commands")
	Commands addCommands();


}
