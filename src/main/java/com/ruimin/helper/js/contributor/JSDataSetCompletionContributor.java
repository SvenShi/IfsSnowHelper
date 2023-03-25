package com.ruimin.helper.js.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.javascript.patterns.JSPatterns;
import com.intellij.patterns.PlatformPatterns;
import com.ruimin.helper.js.provider.JSDataSetCompletionProvider;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/24 下午 11:28
 * @description
 */
public class JSDataSetCompletionContributor extends CompletionContributor  {

    public JSDataSetCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(JSPatterns.jsReferenceExpression()), new JSDataSetCompletionProvider());
    }

}
