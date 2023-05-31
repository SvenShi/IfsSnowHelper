package com.ruimin.helper.dtst.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiFile;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/05/31 上午 10:58
 * @description
 */
public class CopyDataSetPathAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(e.getDataContext());
        if (psiFile != null && DataSetUtils.isDtstFile(psiFile)){
            CopyPasteManager.getInstance()
                    .setContents(new StringSelection(DataSetUtils.getDataSetPath(psiFile)));
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile data = CommonDataKeys.PSI_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(data != null && DataSetUtils.isDtstFile(data));
    }
}
