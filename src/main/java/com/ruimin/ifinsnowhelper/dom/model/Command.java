// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.ifinsnowhelper.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
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
	@Attribute("id")
	@NameValue
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
	@Attribute("type")
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
	@Attribute("type")
	GenericAttributeValue<String> getIcon();


	/**
	 * Returns the value of the txn child.
	 * @return the value of the txn child.
	 */
	@NotNull
	@Attribute("txn")
	GenericAttributeValue<String> getTxn();


	/**
	 * Returns the value of the url child.
	 * @return the value of the url child.
	 */
	@NotNull
	@Attribute("url")
	GenericAttributeValue<String> getUrl();


	/**
	 * Returns the value of the tip child.
	 * @return the value of the tip child.
	 */
	@NotNull
	@Attribute("tip")
	GenericAttributeValue<String> getTip();


	/**
	 * Returns the value of the submitdataset child.
	 * @return the value of the submitdataset child.
	 */
	@NotNull
	@Attribute("submitdataset")
	GenericAttributeValue<String> getSubmitdataset();


	/**
	 * Returns the value of the targetFrame child.
	 * @return the value of the targetFrame child.
	 */
	@NotNull
	@Attribute("targetFrame")
	GenericAttributeValue<String> getTargetFrame();


}
