package com.ruimin.helper.provider.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.util.RqlxUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.reference.JavaToRqlxReference;
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
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiElement callExpression = expression.getParent().getParent();
            if (callExpression instanceof PsiMethodCallExpression) {
                PsiElement referenceExpression = callExpression.getFirstChild();
                if (RqlxUtils.isRqlxMethodName(referenceExpression.getText())) {
                    // 直接就是rqlx select的方法
                    return new PsiReference[]{
                        new JavaToRqlxReference(expression, StringUtils.removeQuot(expression.getText()))};
                } else if (RqlxUtils.isSpliceRqlxKey(referenceExpression)) {
                    //     调用方法拼接的
                    String rqlxKey = RqlxUtils.getSplicedRqlxKey(referenceExpression,
                        StringUtils.removeQuot(expression.getText()));
                    if (StringUtils.isNotBlank(rqlxKey)){
                        return new PsiReference[]{
                            new JavaToRqlxReference(expression, rqlxKey)};
                    }
                }
            }


        }
        return PsiReference.EMPTY_ARRAY;
    }
}
