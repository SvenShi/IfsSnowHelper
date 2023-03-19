package com.ruimin.helper.js.utils;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.js.constants.JSConstants;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import org.jetbrains.annotations.NotNull;

public class SnowJSUtils {


    /**
     * 根据方法中的参数获取方法调用者
     *
     * @param literal 元素
     * @return {@link JSReferenceExpression}
     */
    public static JSReferenceExpression getDataSetMethodCaller(@NotNull PsiElement literal) {
        PsiFile file = literal.getContainingFile();
        if (file instanceof XmlFile) {
            PsiElement jsArgumentList = literal.getParent();
            if (jsArgumentList instanceof JSArgumentList) {
                PsiElement jsReferenceExpression = jsArgumentList.getPrevSibling();
                if (jsReferenceExpression instanceof JSReferenceExpression) {
                    PsiElement callMethod = jsReferenceExpression.getLastChild();
                    String text = callMethod.getText();
                    if (JSConstants.JS_METHODS.contains(text)) {
                        PsiElement datasetReference = jsReferenceExpression.getFirstChild();
                        return datasetReference instanceof JSReferenceExpression
                            ? (JSReferenceExpression) datasetReference : null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据dataset的变量获取调用的dataset的xmlTag
     *
     * @param methodCaller 方法调用者
     * @return {@link XmlTag}
     */
    public static XmlTag getDataSetTagByVar(JSReferenceExpression methodCaller) {
        PsiReference reference = methodCaller.getReference();
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
                return tag;
            }
        }
        return null;
    }
}
