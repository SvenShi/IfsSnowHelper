package com.ruimin.helper.provider.reference;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.constants.DtstConstants;
import com.ruimin.helper.reference.DatasetReference;
import com.ruimin.helper.reference.RqlxReference;
import com.ruimin.helper.util.RqlxUtils;
import com.sun.xml.txw2.TXW;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JavaToRqlxReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        PsiLiteralExpression expression = (PsiLiteralExpression) element;
        String text = expression.getText();
        if (StringUtils.isNotBlank(text)) {
            if (text.contains(".") && text.startsWith("\"") && text.endsWith("\"")) {
                PsiElement parent = element.getParent();
                if (parent instanceof PsiExpressionList) {
                    PsiElement prevSibling = parent.getPrevSibling();
                    if (prevSibling instanceof PsiReferenceExpression) {
                        for (PsiElement child : prevSibling.getChildren()) {
                            if (RqlxUtils.SQL_METHOD_NAME.contains(child.getText())) {
                                return new PsiReference[]{new RqlxReference(expression)};
                            }
                        }
                    }
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
