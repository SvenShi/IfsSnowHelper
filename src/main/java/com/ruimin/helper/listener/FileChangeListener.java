package com.ruimin.helper.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.ruimin.helper.common.RqlxKeyStore;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/15 上午 01:23
 * @description
 */
public class FileChangeListener implements BulkFileListener {

    @Override
    public void before(@NotNull List<? extends @NotNull VFileEvent> events) {
        Set<Project> projects = RqlxKeyStore.getProjects();
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file != null) {
                PsiFile psiFile = null;
                for (Project project : projects) {
                    psiFile = PsiManager.getInstance(project).findFile(file);
                }
                if (psiFile != null) {
                    RqlxKeyStore.clearCache(psiFile);
                }
            }
        }
    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        Set<Project> projects = RqlxKeyStore.getProjects();
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file != null) {
                PsiFile psiFile = null;
                for (Project project : projects) {
                    psiFile = PsiManager.getInstance(project).findFile(file);
                }
                if (psiFile != null) {
                    RqlxKeyStore.refreshFile(psiFile);
                }
            }
        }
    }
}
