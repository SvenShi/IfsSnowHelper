package com.ruimin.helper.java.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.java.reference.JavaRqlxKeyReference;
import com.ruimin.helper.rqlx.utils.RqlxUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JavaReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        PsiLiteralExpression expression = (PsiLiteralExpression) element;
        PsiMethodCallExpression callExpression = RqlxUtils.getLatestMethodCallExpressionFromParent(expression);
        if (callExpression != null) {
            PsiElement referenceExpression = callExpression.getFirstChild();
            if (RqlxUtils.isRqlxMethodName(referenceExpression.getText())) {
                // 直接就是rqlx select的方法
                return new PsiReference[]{
                    new JavaRqlxKeyReference(expression, StringUtils.removeQuot(element.getText()))};
            } else if (RqlxUtils.isSpliceRqlxKey(referenceExpression)) {
                //     调用方法拼接的
                String rqlxKey = RqlxUtils.getSplicedRqlxKey(referenceExpression,
                    StringUtils.removeQuot(element.getText()));
                if (StringUtils.isNotBlank(rqlxKey)) {
                    return new PsiReference[]{new JavaRqlxKeyReference(expression, rqlxKey)};
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
