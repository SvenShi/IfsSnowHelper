package com.ruimin.helper.dtst.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.common.reference.FlowIdReference;
import com.ruimin.helper.common.util.DataUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class DatasetFlowIdReference extends FlowIdReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public DatasetFlowIdReference(@NotNull XmlAttributeValue element, @NotNull String flowId) {
        super(element, flowId, new TextRange(1, DataUtils.mustPositive(flowId.length() + 1, 1)));
    }

}
