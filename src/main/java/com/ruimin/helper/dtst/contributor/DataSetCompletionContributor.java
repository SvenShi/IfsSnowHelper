package com.ruimin.helper.dtst.contributor;

import static com.intellij.patterns.PlatformPatterns.psiElement;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.XmlPatterns;
import com.ruimin.helper.dtst.provider.DataSetCompletionProvider;

public class DataSetCompletionContributor extends CompletionContributor {

    public DataSetCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().inside(XmlPatterns.xmlAttributeValue()),
            new DataSetCompletionProvider());
    }


}
