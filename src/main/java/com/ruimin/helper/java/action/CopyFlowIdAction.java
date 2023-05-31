package com.ruimin.helper.java.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/05/30 下午 05:09
 * @description
 */
public class CopyFlowIdAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            PsiClass containingClass = method.getContainingClass();
            if (containingClass != null) {
                CopyPasteManager.getInstance()
                        .setContents(new StringSelection(containingClass.getQualifiedName() + ":" + method.getName()));
            }
        }
    }
    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(element instanceof PsiMethod);
    }

}
