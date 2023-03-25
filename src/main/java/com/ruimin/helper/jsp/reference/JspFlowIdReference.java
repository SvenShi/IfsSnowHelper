package com.ruimin.helper.jsp.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.dtst.reference.DatasetFlowIdReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/26 上午 12:33
 * @description
 */
public class JspFlowIdReference extends DatasetFlowIdReference {

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspFlowIdReference(@NotNull XmlAttributeValue element, String flowId) {
        super(element, flowId, new TextRange(1, flowId.length() + 1));
    }
}
