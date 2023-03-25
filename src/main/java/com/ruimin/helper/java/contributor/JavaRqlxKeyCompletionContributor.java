package com.ruimin.helper.java.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.ruimin.helper.java.provider.JavaRqlxKeyCompletionProvider;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/24 下午 11:28
 * @description
 */
public class JavaRqlxKeyCompletionContributor extends CompletionContributor  {

    public JavaRqlxKeyCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(PsiJavaPatterns.literalExpression()), new JavaRqlxKeyCompletionProvider());
    }

}
