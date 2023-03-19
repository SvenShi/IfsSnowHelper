package com.ruimin.helper.js.reference;

import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.JspPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JavaScriptQueryReference extends PsiReferenceBase<JSReferenceExpression> implements
    PsiPolyVariantReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JavaScriptQueryReference(@NotNull JSReferenceExpression element) {
        super(Objects.requireNonNull(element), new TextRange(0, element.getTextLength() - 1));
    }


    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or {@code null} if it was not possible to resolve the reference to a valid target.
     * @see PsiPolyVariantReference#multiResolve(boolean)
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }


    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     * being resolved is considered incomplete, and the method may return additional
     * invalid results.
     * @return the array of results for resolving the reference.
     */
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        JspFile jspFile = JspPsiUtil.getJspFile(myElement);
        if (jspFile != null) {
            List<XmlTag> queryTags = SnowJspUtils.findAllTagInFile(jspFile,
                JspConstants.QUERY_TAG_NAME);
            ArrayList<ResolveResult> resolveResults = new ArrayList<>();
            String text = myElement.getText();
            for (XmlTag xmlTag : queryTags) {
                XmlAttribute attribute = xmlTag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                if (attribute != null) {
                    XmlAttributeValue valueElement = attribute.getValueElement();
                    if (valueElement != null) {
                        String value = valueElement.getValue();
                        if (StringUtils.isNotBlank(value)) {
                            String dataSetExpression = value + JspConstants.QUERY_EXPRESSION_SUFFIX;
                            if (dataSetExpression.equals(text)) {
                                resolveResults.add(new PsiElementResolveResult(valueElement));
                            }
                        }
                    }
                }
            }
            return resolveResults.toArray(ResolveResult.EMPTY_ARRAY);
        }
        return ResolveResult.EMPTY_ARRAY;
    }


}
