package com.ruimin.helper.js.provider.reference;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.js.constants.JSConstants;
import com.ruimin.helper.js.reference.JavaScriptFieldReference;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JSLiteralReferenceProvider extends PsiReferenceProvider {


    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        PsiFile file = element.getContainingFile();
        if (file instanceof XmlFile) {
            JSLiteralExpression literal = (JSLiteralExpression) element;
            PsiElement jsArgumentList = literal.getParent();
            if (jsArgumentList instanceof JSArgumentList) {
                PsiElement jsReferenceExpression = jsArgumentList.getPrevSibling();
                if (jsReferenceExpression instanceof JSReferenceExpression) {
                    PsiElement callMethod = jsReferenceExpression.getLastChild();
                    String text = callMethod.getText();
                    if (JSConstants.JS_METHODS.contains(text)) {
                        PsiElement datasetReference = jsReferenceExpression.getFirstChild();
                        if (datasetReference instanceof JSReferenceExpression) {
                            PsiReference reference = datasetReference.getReference();
                            if (reference != null) {
                                PsiElement resolve = reference.resolve();
                                if (resolve instanceof XmlAttributeValue) {
                                    XmlTag tag = SnowJspUtils.findTag((XmlElement) resolve);
                                    if (JspConstants.QUERY_TAG_NAME.equals(tag.getName())) {
                                        XmlAttribute attribute = tag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                                        if (attribute != null) {
                                            XmlAttributeValue valueElement = attribute.getValueElement();
                                            if (valueElement != null) {
                                                reference = valueElement.getReference();
                                                if (reference != null) {
                                                    resolve = reference.resolve();
                                                    if (resolve instanceof XmlAttributeValue) {
                                                        tag = SnowJspUtils.findTag((XmlElement) resolve);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    return new PsiReference[]{new JavaScriptFieldReference(literal, tag)};
                                }
                            }


                        }
                    }
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
