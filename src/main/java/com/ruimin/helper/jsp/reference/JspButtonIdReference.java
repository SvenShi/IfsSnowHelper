package com.ruimin.helper.jsp.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.common.reference.ButtonIdReference;
import com.ruimin.helper.common.util.DataUtils;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JspButtonIdReference extends ButtonIdReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspButtonIdReference(@NotNull XmlAttributeValue element, String buttonId) {
        super(Objects.requireNonNull(element), buttonId,
            new TextRange(1, DataUtils.mustPositive(element.getTextLength() - 1, 1)));
    }

}
