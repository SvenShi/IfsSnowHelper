package com.ruimin.helper.provider;


import com.intellij.codeInsight.navigation.GotoTargetHandler;
import com.intellij.codeInsight.navigation.GotoTargetRendererProvider;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.xml.util.XmlUtil;
import com.ruimin.helper.util.DtstUtils;
import org.jetbrains.annotations.NotNull;

public class GotoDtstXmlSchemaRendererProvider implements GotoTargetRendererProvider {

    @Override
    public PsiElementListCellRenderer getRenderer(@NotNull PsiElement element, @NotNull GotoTargetHandler.GotoData gotoData) {
        if (element instanceof XmlTagImpl) {
            if (DtstUtils.isElementWithinDtstFile(element)) {
                return new MyRenderer();
            }
        }
        return null;
    }

    public static class MyRenderer extends PsiElementListCellRenderer<XmlTagImpl> {

        @Override
        public String getElementText(XmlTagImpl element) {
            XmlAttribute attr = element.getAttribute("flowid", XmlUtil.XML_SCHEMA_INSTANCE_URI);
            attr = attr == null ? element.getAttribute("flowid") : attr;
            return (attr == null || attr.getValue() == null ? element.getName() : attr.getValue());
        }

        @Override
        protected String getContainerText(XmlTagImpl element, String name) {
            return element.getContainingFile().getName();
        }


        @Override
        protected int getIconFlags() {
            return 0;
        }
    }
}
