package com.ruimin.helper.jsp.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.xml.XmlAttributeValue;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JspPaginationbarReference extends JspButtonIdReference implements PsiPolyVariantReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspPaginationbarReference(@NotNull XmlAttributeValue element, String buttonId, int startIndex,
        int endIndex) {
        super(Objects.requireNonNull(element), buttonId, new TextRange(Math.max(startIndex, 0), endIndex));
    }
}
