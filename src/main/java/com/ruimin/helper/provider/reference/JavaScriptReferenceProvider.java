package com.ruimin.helper.provider.reference;

import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.constants.SnowPageConstants;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.reference.SnowPageJsDataSetReference;
import com.ruimin.helper.reference.SnowPageJsQueryReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JavaScriptReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        PsiFile file = element.getContainingFile();
        if (file instanceof XmlFile) {
            JSReferenceExpression expression = (JSReferenceExpression) element;
            String text = expression.getText();
            if (StringUtils.endsWith(text, SnowPageConstants.QUERY_EXPRESSION_SUFFIX)) {
                return new PsiReference[]{new SnowPageJsQueryReference(expression)};
            } else if (StringUtils.endsWith(text, SnowPageConstants.DTST_EXPRESSION_SUFFIX)) {
                return new PsiReference[]{new SnowPageJsDataSetReference(expression)};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
