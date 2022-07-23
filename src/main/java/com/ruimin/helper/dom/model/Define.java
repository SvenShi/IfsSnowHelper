// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:DefineType interface.
 * @author shiwei
 */
public interface Define extends FlowIdDomElement {

	/**
	 * Returns the value of the type child.
	 * <pre>
	 * <h3>Attribute null:type documentation</h3>
	 * 类别
	 * </pre>
	 * @return the value of the type child.
	 */
	@NotNull
	@Required
	@Attribute("type")
	GenericAttributeValue<Type> getType();


	/**
	 * Returns the value of the pagesize child.
	 * <pre>
	 * <h3>Attribute null:pagesize documentation</h3>
	 * 页大小
	 * </pre>
	 * @return the value of the pagesize child.
	 */
	@NotNull
	@Attribute("pagesize")
	GenericAttributeValue<Integer> getPagesize();


	/**
	 * Returns the value of the log child.
	 * <pre>
	 * <h3>Attribute null:log documentation</h3>
	 * 是否记录日志
	 * </pre>
	 * @return the value of the log child.
	 */
	@NotNull
	@Required
	@Attribute("log")
	GenericAttributeValue<Boolean> getLog();


	/**
	 * Returns the value of the desc child.
	 * <pre>
	 * <h3>Attribute null:desc documentation</h3>
	 * 页面描述
	 * </pre>
	 * @return the value of the desc child.
	 */
	@NotNull
	@Required
	@Attribute("desc")
	GenericAttributeValue<String> getDesc();


	/**
	 * Returns the value of the expmaxrcd child.
	 * <pre>
	 * <h3>Attribute null:expmaxrcd documentation</h3>
	 * 在线导出时的最大记录数
	 * </pre>
	 * @return the value of the expmaxrcd child.
	 */
	@NotNull
	@Required
	@Attribute("expmaxrcd")
	GenericAttributeValue<Integer> getExpmaxrcd();


	/**
	 * Returns the value of the flowctxid child.
	 * <pre>
	 * <h3>Attribute null:flowctxid documentation</h3>
	 * 流程上下文输入id
	 * </pre>
	 * @return the value of the flowctxid child.
	 */
	@NotNull
	@Attribute("flowctxid")
	GenericAttributeValue<String> getFlowctxid();


	/**
	 * Returns the value of the flowretid child.
	 * <pre>
	 * <h3>Attribute null:flowretid documentation</h3>
	 * 流程上下文输出id
	 * </pre>
	 * @return the value of the flowretid child.
	 */
	@NotNull
	@Attribute("flowretid")
	GenericAttributeValue<String> getFlowretid();


}
