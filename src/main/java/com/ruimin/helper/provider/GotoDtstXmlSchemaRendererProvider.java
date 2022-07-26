package com.ruimin.helper.provider;


import com.intellij.codeInsight.navigation.GotoTargetPresentationProvider;
import com.intellij.navigation.TargetPresentation;
import com.intellij.navigation.TargetPresentationBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GotoDtstXmlSchemaRendererProvider implements GotoTargetPresentationProvider {

    @Override
    @Nullable
    public TargetPresentation getTargetPresentation(@NotNull PsiElement element, boolean differentNames) {
        if (element instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) element;
            String text = element.getText();

            String id = xmlTag.getAttributeValue("id");
            if (StringUtils.isNotBlank(id)) {
                text += "  " + id;
            } else {
                String desc = xmlTag.getAttributeValue("desc");
                text += "  " + desc;
            }
            TargetPresentationBuilder builder = TargetPresentation.builder(text);
            builder.containerText(element.getContainingFile().getName());
            return builder.presentation();
        }
        return null;
    }
}
