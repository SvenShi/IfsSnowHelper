// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.ifinsnowhelper.dom.model;

import com.intellij.util.xml.*;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:FieldType interface.
 *
 * @author shiwei
 */
public interface Field extends DomElement {

    /**
     * Returns the value of the id child.
     *
     * @return the value of the id child.
     */
    @NotNull
    @Required
    @Attribute("id")
    GenericAttributeValue<String> getId();


    /**
     * Returns the value of the desc child.
     * <pre>
     * <h3>Attribute null:desc documentation</h3>
     * 描述
     * </pre>
     *
     * @return the value of the desc child.
     */
    @NotNull
    @Attribute("desc")
    GenericAttributeValue<String> getDesc();


    /**
     * Returns the value of the ref child.
     * <pre>
     * <h3>Attribute null:ref documentation</h3>
     * 关联数据集模型的id
     * </pre>
     *
     * @return the value of the ref child.
     */
    @NotNull
    @Attribute("ref")
    GenericAttributeValue<String> getRef();


    /**
     * Returns the value of the required child.
     * <pre>
     * <h3>Attribute null:required documentation</h3>
     * 是否必需
     * </pre>
     *
     * @return the value of the required child.
     */
    @NotNull
    @Attribute("required")
    GenericAttributeValue<Boolean> getRequired();


    /**
     * Returns the value of the readonly child.
     * <pre>
     * <h3>Attribute null:readonly documentation</h3>
     * 是否只读
     * </pre>
     *
     * @return the value of the readonly child.
     */
    @NotNull
    @Attribute("readonly")
    GenericAttributeValue<Boolean> getReadonly();


    /**
     * Returns the value of the method child.
     * <pre>
     * <h3>Attribute null:method documentation</h3>
     * 方法，常用于动态下拉反显
     * </pre>
     *
     * @return the value of the method child.
     */
    @NotNull
    @Attribute("method")
    GenericAttributeValue<String> getMethod();


    /**
     * Returns the value of the width child.
     * <pre>
     * <h3>Attribute null:width documentation</h3>
     * 控件宽度
     * </pre>
     *
     * @return the value of the width child.
     */
    @NotNull
    @Attribute("width")
    GenericAttributeValue<Integer> getWidth();


    /**
     * Returns the value of the tooltip child.
     * <pre>
     * <h3>Attribute null:tooltip documentation</h3>
     * 提示
     * </pre>
     *
     * @return the value of the tooltip child.
     */
    @NotNull
    @Attribute("tooltip")
    GenericAttributeValue<String> getTooltip();


    /**
     * Returns the value of the status child.
     * <pre>
     * <h3>Attribute null:status documentation</h3>
     * 状态
     * </pre>
     *
     * @return the value of the status child.
     */
    @NotNull
    @Attribute("status")
    GenericAttributeValue<Status> getStatus();


    /**
     * Returns the value of the edittype child.
     * <pre>
     * <h3>Attribute null:edittype documentation</h3>
     * 编辑类型
     * </pre>
     *
     * @return the value of the edittype child.
     */
    @NotNull
    @Attribute("edittype")
    GenericAttributeValue<Edittype> getEdittype();


    /**
     * Returns the value of the datatype child.
     * <pre>
     * <h3>Attribute null:datatype documentation</h3>
     * 数据类型
     * </pre>
     *
     * @return the value of the datatype child.
     */
    @NotNull
    @Attribute("datatype")
    GenericAttributeValue<Datatype> getDatatype();


    /**
     * Returns the value of the rule child.
     * <pre>
     * <h3>Attribute null:rule documentation</h3>
     * 规则名称
     * </pre>
     *
     * @return the value of the rule child.
     */
    @NotNull
    @Attribute("rule")
    GenericAttributeValue<String> getRule();


    /**
     * Returns the value of the errmsg child.
     * <pre>
     * <h3>Attribute null:errmsg documentation</h3>
     * 验证失败信息
     * </pre>
     *
     * @return the value of the errmsg child.
     */
    @NotNull
    @Attribute("errmsg")
    GenericAttributeValue<String> getErrmsg();


    /**
     * Returns the value of the colspan child.
     * <pre>
     * <h3>Attribute null:colspan documentation</h3>
     * 表单中占用的列宽
     * </pre>
     *
     * @return the value of the colspan child.
     */
    @NotNull
    @Attribute("colspan")
    GenericAttributeValue<Integer> getColspan();


    /**
     * Returns the value of the rows child.
     * <pre>
     * <h3>Attribute null:rows documentation</h3>
     * 行数，用于文本域
     * </pre>
     *
     * @return the value of the rows child.
     */
    @NotNull
    @Attribute("rows")
    GenericAttributeValue<Integer> getRows();


    /**
     * Returns the value of the size child.
     * <pre>
     * <h3>Attribute null:size documentation</h3>
     * 长度
     * </pre>
     *
     * @return the value of the size child.
     */
    @NotNull
    @Attribute("size")
    GenericAttributeValue<Integer> getSize();


    /**
     * Returns the value of the scale child.
     * <pre>
     * <h3>Attribute null:scale documentation</h3>
     * 精度
     * </pre>
     *
     * @return the value of the scale child.
     */
    @NotNull
    @Attribute("scale")
    GenericAttributeValue<Integer> getScale();


    /**
     * Returns the value of the datasource child.
     * <pre>
     * <h3>Attribute null:datasource documentation</h3>
     * 数据来源,目前支持三种:LIST DDIC CQDS
     * </pre>
     *
     * @return the value of the datasource child.
     */
    @NotNull
    @Attribute("datasource")
    GenericAttributeValue<String> getDatasource();


    /**
     * Returns the value of the viewfield child.
     * <pre>
     * <h3>Attribute null:viewfield documentation</h3>
     * 下拉显示字段，用于动态下拉
     * </pre>
     *
     * @return the value of the viewfield child.
     */
    @NotNull
    @Attribute("viewfield")
    GenericAttributeValue<String> getViewfield();


    /**
     * Returns the value of the searchfield child.
     * <pre>
     * <h3>Attribute null:searchfield documentation</h3>
     * 查下拉询字段，用于动态下拉
     * </pre>
     *
     * @return the value of the searchfield child.
     */
    @NotNull
    @Attribute("searchfield")
    GenericAttributeValue<String> getSearchfield();


    /**
     * Returns the value of the fieldmap child.
     * <pre>
     * <h3>Attribute null:fieldmap documentation</h3>
     * 下拉选择映射，dest1=src1,dest2=src2
     * </pre>
     *
     * @return the value of the fieldmap child.
     */
    @NotNull
    @Attribute("fieldmap")
    GenericAttributeValue<String> getFieldmap();


    /**
     * Returns the value of the default child.
     * <pre>
     * <h3>Attribute null:default documentation</h3>
     * 默认值
     * </pre>
     *
     * @return the value of the default child.
     */
    @NotNull
    @Attribute("default")
    GenericAttributeValue<String> getDefault();


    /**
     * Returns the value of the cols child.
     * <pre>
     * <h3>Attribute null:cols documentation</h3>
     * 一行的个数，用于radioboxs和checkboxs
     * </pre>
     *
     * @return the value of the cols child.
     */
    @NotNull
    @Attribute("cols")
    GenericAttributeValue<Integer> getCols();


    /**
     * Returns the value of the databus child.
     * <pre>
     * <h3>Attribute null:databus documentation</h3>
     * 查询时，是否缓存到databus中
     * </pre>
     *
     * @return the value of the databus child.
     */
    @NotNull
    @Attribute("databus")
    GenericAttributeValue<Boolean> getDatabus();


    /**
     * Returns the value of the multiple child.
     * <pre>
     * <h3>Attribute null:multiple documentation</h3>
     * 是否多选，用于下拉选择
     * </pre>
     *
     * @return the value of the multiple child.
     */
    @NotNull
    @Attribute("multiple")
    GenericAttributeValue<Boolean> getMultiple();


    /**
     * Returns the value of the async child.
     * <pre>
     * <h3>Attribute null:async documentation</h3>
     * 是否异步加载，用于动态下拉树
     * </pre>
     *
     * @return the value of the async child.
     */
    @NotNull
    @Attribute("async")
    GenericAttributeValue<Boolean> getAsync();


    /**
     * Returns the value of the init child.
     * <pre>
     * <h3>Attribute null:init documentation</h3>
     * 是否初始化，用于下拉选择
     * </pre>
     *
     * @return the value of the init child.
     */
    @NotNull
    @Attribute("init")
    GenericAttributeValue<Boolean> getInit();


    /**
     * Returns the value of the placeholder child.
     *
     * @return the value of the placeholder child.
     */
    @NotNull
    @Attribute("placeholder")
    GenericAttributeValue<String> getPlaceholder();


    /**
     * Returns the value of the xpath child.
     * <pre>
     * <h3>Attribute null:xpath documentation</h3>
     * 数据映射
     * </pre>
     *
     * @return the value of the xpath child.
     */
    @NotNull
    @Attribute("xpath")
    GenericAttributeValue<String> getXpath();


    /**
     * Returns the value of the editable child.
     *
     * @return the value of the editable child.
     */
    @NotNull
    @Attribute("editable")
    GenericAttributeValue<Boolean> getEditable();


    /**
     * Returns the value of the url child.
     *
     * @return the value of the url child.
     */
    @NotNull
    @Attribute("url")
    GenericAttributeValue<String> getUrl();


    /**
     * Returns the value of the align child.
     * <pre>
     * <h3>Attribute null:align documentation</h3>
     * 单元格对齐方式
     * </pre>
     *
     * @return the value of the align child.
     */
    @NotNull
    @Attribute("align")
    GenericAttributeValue<Align> getAlign();


    /**
     * Returns the value of the headalign child.
     * <pre>
     * <h3>Attribute null:headalign documentation</h3>
     * 列头对齐方式
     * </pre>
     *
     * @return the value of the headalign child.
     */
    @NotNull
    @Attribute("headalign")
    GenericAttributeValue<Headalign> getHeadalign();


    /**
     * Returns the value of the minsize child.
     * <pre>
     * <h3>Attribute null:minsize documentation</h3>
     * 最小长度
     * </pre>
     *
     * @return the value of the minsize child.
     */
    @NotNull
    @Attribute("minsize")
    GenericAttributeValue<Integer> getMinsize();


}
