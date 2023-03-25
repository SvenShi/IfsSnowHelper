package com.ruimin.helper.java.contributor;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ThreeState;
import com.ruimin.helper.rqlx.utils.RqlxUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/25 下午 03:34
 * @description
 */
public class SnowLiteralCompletionConfidence extends CompletionConfidence {


    @NotNull
    @Override
    public ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
        if (isInStringLiteral(contextElement)) {
            PsiLiteralExpression expression;
            if (contextElement instanceof PsiJavaToken) {
                expression = (PsiLiteralExpression) contextElement.getParent();
            } else if (contextElement instanceof PsiLiteralExpression) {
                expression = (PsiLiteralExpression) contextElement;
            } else if (contextElement instanceof PsiLiteral) {
                expression = (PsiLiteralExpression) contextElement.getParent();
            } else {
                return ThreeState.YES;
            }
            PsiMethodCallExpression callExpression = RqlxUtils.getLatestMethodCallExpressionFromParent(expression);
            if (callExpression != null) {
                PsiElement referenceExpression = callExpression.getFirstChild();
                if (RqlxUtils.isRqlxMethodName(referenceExpression.getText()) || RqlxUtils.isSpliceRqlxKey(
                    referenceExpression)) {
                    return ThreeState.NO;
                }
            }
            return ThreeState.YES;
        }
        return ThreeState.UNSURE;
    }

    public static boolean isInStringLiteral(PsiElement element) {
        ParserDefinition definition = LanguageParserDefinitions.INSTANCE.forLanguage(
            PsiUtilCore.findLanguageFromElement(element));
        return definition != null && (isStringLiteral(element, definition) || isStringLiteral(element.getParent(),
            definition));
    }

    private static boolean isStringLiteral(PsiElement element, ParserDefinition definition) {
        return PlatformPatterns.psiElement().withElementType(definition.getStringLiteralElements()).accepts(element);
    }
}
