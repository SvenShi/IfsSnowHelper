package com.ruimin.helper.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 03:06
 * @description
 */
public interface FlowIdDomElement extends DomElement {

    /**
     * Returns the value of the flowid child.
     * <pre>
     * <h3>Attribute null:flowid documentation</h3>
     * 流程或组件路径
     * </pre>
     *
     * @return the value of the flowid child.
     */
    @Required
    @NameValue
    @Attribute("flowid")
    @NotNull GenericAttributeValue<String> getFlowid();

}
