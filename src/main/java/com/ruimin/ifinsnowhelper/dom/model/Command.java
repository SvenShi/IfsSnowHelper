// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.ifinsnowhelper.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:CommandType interface.
 * @author shiwei
 */
public interface Command extends FlowIdDomElement {

	/**
	 * Returns the value of the id child.
	 * <pre>
	 * <h3>Attribute null:id documentation</h3>
	 * 按钮id
	 * </pre>
	 * @return the value of the id child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getId();


	/**
	 * Returns the value of the desc child.
	 * <pre>
	 * <h3>Attribute null:desc documentation</h3>
	 * 按钮名称
	 * </pre>
	 * @return the value of the desc child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getDesc();


	/**
	 * Returns the value of the type child.
	 * <pre>
	 * <h3>Attribute null:type documentation</h3>
	 * 提交类型
	 * </pre>
	 * @return the value of the type child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<Type> getType();



	/**
	 * Returns the value of the icon child.
	 * <pre>
	 * <h3>Attribute null:icon documentation</h3>
	 * 图标
	 * </pre>
	 * @return the value of the icon child.
	 */
	@NotNull
	GenericAttributeValue<String> getIcon();


	/**
	 * Returns the value of the txn child.
	 * @return the value of the txn child.
	 */
	@NotNull
	GenericAttributeValue<String> getTxn();


	/**
	 * Returns the value of the url child.
	 * @return the value of the url child.
	 */
	@NotNull
	GenericAttributeValue<String> getUrl();


	/**
	 * Returns the value of the tip child.
	 * @return the value of the tip child.
	 */
	@NotNull
	GenericAttributeValue<String> getTip();


	/**
	 * Returns the value of the submitdataset child.
	 * @return the value of the submitdataset child.
	 */
	@NotNull
	GenericAttributeValue<String> getSubmitdataset();


	/**
	 * Returns the value of the targetFrame child.
	 * @return the value of the targetFrame child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrame();


}
