// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.ifinsnowhelper.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagsList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * http://www.rmitec.cn/dtst-mapper:DataElemType interface.
 * @author shiwei
 */
public interface Data extends DomElement {

	@SubTagsList({"Define","Command"})
	List<FlowIdDomElement> getFlowIdDomElements();

	/**
	 * Returns the list of Define children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:Define documentation</h3>
	 * 数据集配置
	 * </pre>
	 * @return the list of Define children.
	 */
	@NotNull
	java.util.List<Define> getDefines();
	/**
	 * Adds new child to the list of Define children.
	 * @return created child
	 */
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
	java.util.List<DataRef> getDataRefs();
	/**
	 * Adds new child to the list of DataRef children.
	 * @return created child
	 */
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
	java.util.List<Fields> getFieldses();
	/**
	 * Adds new child to the list of Fields children.
	 * @return created child
	 */
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
	java.util.List<Commands> getCommandses();
	/**
	 * Adds new child to the list of Commands children.
	 * @return created child
	 */
	Commands addCommands();


}
