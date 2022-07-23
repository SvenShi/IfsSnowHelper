// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.ifinsnowhelper.dom.model;

import com.intellij.util.xml.*;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:FieldType interface.
 * @author shiwei
 */
public interface Field extends DomElement {

	/**
	 * Returns the value of the id child.
	 * @return the value of the id child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getId();


	/**
	 * Returns the value of the desc child.
	 * <pre>
	 * <h3>Attribute null:desc documentation</h3>
	 * 描述
	 * </pre>
	 * @return the value of the desc child.
	 */
	@NotNull
	GenericAttributeValue<String> getDesc();


	/**
	 * Returns the value of the ref child.
	 * <pre>
	 * <h3>Attribute null:ref documentation</h3>
	 * 关联数据集模型的id
	 * </pre>
	 * @return the value of the ref child.
	 */
	@NotNull
	GenericAttributeValue<String> getRef();


	/**
	 * Returns the value of the required child.
	 * <pre>
	 * <h3>Attribute null:required documentation</h3>
	 * 是否必需
	 * </pre>
	 * @return the value of the required child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRequired();


	/**
	 * Returns the value of the readonly child.
	 * <pre>
	 * <h3>Attribute null:readonly documentation</h3>
	 * 是否只读
	 * </pre>
	 * @return the value of the readonly child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getReadonly();


	/**
	 * Returns the value of the method child.
	 * <pre>
	 * <h3>Attribute null:method documentation</h3>
	 * 方法，常用于动态下拉反显
	 * </pre>
	 * @return the value of the method child.
	 */
	@NotNull
	GenericAttributeValue<String> getMethod();


	/**
	 * Returns the value of the width child.
	 * <pre>
	 * <h3>Attribute null:width documentation</h3>
	 * 控件宽度
	 * </pre>
	 * @return the value of the width child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getWidth();


	/**
	 * Returns the value of the tooltip child.
	 * <pre>
	 * <h3>Attribute null:tooltip documentation</h3>
	 * 提示
	 * </pre>
	 * @return the value of the tooltip child.
	 */
	@NotNull
	GenericAttributeValue<String> getTooltip();


	/**
	 * Returns the value of the status child.
	 * <pre>
	 * <h3>Attribute null:status documentation</h3>
	 * 状态
	 * </pre>
	 * @return the value of the status child.
	 */
	@NotNull
	GenericAttributeValue<Status> getStatus();


	/**
	 * Returns the value of the edittype child.
	 * <pre>
	 * <h3>Attribute null:edittype documentation</h3>
	 * 编辑类型
	 * </pre>
	 * @return the value of the edittype child.
	 */
	@NotNull
	GenericAttributeValue<Edittype> getEdittype();


	/**
	 * Returns the value of the datatype child.
	 * <pre>
	 * <h3>Attribute null:datatype documentation</h3>
	 * 数据类型
	 * </pre>
	 * @return the value of the datatype child.
	 */
	@NotNull
	GenericAttributeValue<Datatype> getDatatype();


	/**
	 * Returns the value of the rule child.
	 * <pre>
	 * <h3>Attribute null:rule documentation</h3>
	 * 规则名称
	 * </pre>
	 * @return the value of the rule child.
	 */
	@NotNull
	GenericAttributeValue<String> getRule();


	/**
	 * Returns the value of the errmsg child.
	 * <pre>
	 * <h3>Attribute null:errmsg documentation</h3>
	 * 验证失败信息
	 * </pre>
	 * @return the value of the errmsg child.
	 */
	@NotNull
	GenericAttributeValue<String> getErrmsg();


	/**
	 * Returns the value of the colspan child.
	 * <pre>
	 * <h3>Attribute null:colspan documentation</h3>
	 * 表单中占用的列宽
	 * </pre>
	 * @return the value of the colspan child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getColspan();


	/**
	 * Returns the value of the rows child.
	 * <pre>
	 * <h3>Attribute null:rows documentation</h3>
	 * 行数，用于文本域
	 * </pre>
	 * @return the value of the rows child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getRows();


	/**
	 * Returns the value of the size child.
	 * <pre>
	 * <h3>Attribute null:size documentation</h3>
	 * 长度
	 * </pre>
	 * @return the value of the size child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getSize();


	/**
	 * Returns the value of the scale child.
	 * <pre>
	 * <h3>Attribute null:scale documentation</h3>
	 * 精度
	 * </pre>
	 * @return the value of the scale child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getScale();


	/**
	 * Returns the value of the datasource child.
	 * <pre>
	 * <h3>Attribute null:datasource documentation</h3>
	 * 数据来源,目前支持三种:LIST DDIC CQDS
	 * </pre>
	 * @return the value of the datasource child.
	 */
	@NotNull
	GenericAttributeValue<String> getDatasource();


	/**
	 * Returns the value of the viewfield child.
	 * <pre>
	 * <h3>Attribute null:viewfield documentation</h3>
	 * 下拉显示字段，用于动态下拉
	 * </pre>
	 * @return the value of the viewfield child.
	 */
	@NotNull
	GenericAttributeValue<String> getViewfield();


	/**
	 * Returns the value of the searchfield child.
	 * <pre>
	 * <h3>Attribute null:searchfield documentation</h3>
	 * 查下拉询字段，用于动态下拉
	 * </pre>
	 * @return the value of the searchfield child.
	 */
	@NotNull
	GenericAttributeValue<String> getSearchfield();


	/**
	 * Returns the value of the fieldmap child.
	 * <pre>
	 * <h3>Attribute null:fieldmap documentation</h3>
	 * 下拉选择映射，dest1=src1,dest2=src2
	 * </pre>
	 * @return the value of the fieldmap child.
	 */
	@NotNull
	GenericAttributeValue<String> getFieldmap();


	/**
	 * Returns the value of the default child.
	 * <pre>
	 * <h3>Attribute null:default documentation</h3>
	 * 默认值
	 * </pre>
	 * @return the value of the default child.
	 */
	@NotNull
	GenericAttributeValue<String> getDefault();


	/**
	 * Returns the value of the cols child.
	 * <pre>
	 * <h3>Attribute null:cols documentation</h3>
	 * 一行的个数，用于radioboxs和checkboxs
	 * </pre>
	 * @return the value of the cols child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getCols();


	/**
	 * Returns the value of the databus child.
	 * <pre>
	 * <h3>Attribute null:databus documentation</h3>
	 * 查询时，是否缓存到databus中
	 * </pre>
	 * @return the value of the databus child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getDatabus();


	/**
	 * Returns the value of the multiple child.
	 * <pre>
	 * <h3>Attribute null:multiple documentation</h3>
	 * 是否多选，用于下拉选择
	 * </pre>
	 * @return the value of the multiple child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMultiple();


	/**
	 * Returns the value of the async child.
	 * <pre>
	 * <h3>Attribute null:async documentation</h3>
	 * 是否异步加载，用于动态下拉树
	 * </pre>
	 * @return the value of the async child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getAsync();


	/**
	 * Returns the value of the init child.
	 * <pre>
	 * <h3>Attribute null:init documentation</h3>
	 * 是否初始化，用于下拉选择
	 * </pre>
	 * @return the value of the init child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getInit();


	/**
	 * Returns the value of the placeholder child.
	 * @return the value of the placeholder child.
	 */
	@NotNull
	GenericAttributeValue<String> getPlaceholder();


	/**
	 * Returns the value of the xpath child.
	 * <pre>
	 * <h3>Attribute null:xpath documentation</h3>
	 * 数据映射
	 * </pre>
	 * @return the value of the xpath child.
	 */
	@NotNull
	GenericAttributeValue<String> getXpath();


	/**
	 * Returns the value of the editable child.
	 * @return the value of the editable child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEditable();


	/**
	 * Returns the value of the url child.
	 * @return the value of the url child.
	 */
	@NotNull
	GenericAttributeValue<String> getUrl();


	/**
	 * Returns the value of the align child.
	 * <pre>
	 * <h3>Attribute null:align documentation</h3>
	 * 单元格对齐方式
	 * </pre>
	 * @return the value of the align child.
	 */
	@NotNull
	GenericAttributeValue<Align> getAlign();


	/**
	 * Returns the value of the headalign child.
	 * <pre>
	 * <h3>Attribute null:headalign documentation</h3>
	 * 列头对齐方式
	 * </pre>
	 * @return the value of the headalign child.
	 */
	@NotNull
	GenericAttributeValue<Headalign> getHeadalign();


	/**
	 * Returns the value of the minsize child.
	 * <pre>
	 * <h3>Attribute null:minsize documentation</h3>
	 * 最小长度
	 * </pre>
	 * @return the value of the minsize child.
	 */
	@NotNull
	GenericAttributeValue<Integer> getMinsize();


}
