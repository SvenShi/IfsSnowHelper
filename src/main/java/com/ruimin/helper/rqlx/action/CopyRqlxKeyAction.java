package com.ruimin.helper.rqlx.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.rqlx.constans.RqlxConstants;
import com.ruimin.helper.rqlx.utils.RqlxUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/05/30 下午 07:16
 * @description
 */
public class CopyRqlxKeyAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        if (element instanceof PomTargetPsiElement) {
            PsiElement context = element.getContext();
            if (context instanceof XmlAttributeValue){
                XmlAttributeValue attributeValue = (XmlAttributeValue) context;
                PsiFile containingFile = attributeValue.getContainingFile();
                String rqlxKey = RqlxUtils.getRqlxKey(containingFile, attributeValue.getValue());
                CopyPasteManager.getInstance().setContents(new StringSelection(rqlxKey));
            }
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        boolean flag = false;
        if (element instanceof PomTargetPsiElement) {
            PsiElement context = element.getContext();
            if (context instanceof XmlAttributeValue && RqlxUtils.isRqlxFile(context.getContainingFile())) {
                XmlAttributeValue attributeValue = (XmlAttributeValue) context;
                String localName = XmlAttributeValuePattern.getLocalName(attributeValue);
                if (RqlxConstants.ATTR_NAME_ID.equals(localName)) {
                    flag = true;
                }
            }
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }

}
