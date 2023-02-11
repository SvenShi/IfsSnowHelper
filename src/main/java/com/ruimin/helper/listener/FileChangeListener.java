package com.ruimin.helper.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.ruimin.helper.common.RqlxKeyStorage;
import java.util.List;
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
        for (VFileEvent event : events) {
            @NotNull Project[] projects = ProjectManager.getInstance().getOpenProjects();
            VirtualFile file = event.getFile();
            if (file != null) {
                for (Project project : projects) {
                    boolean valid = file.isValid();
                    if (valid) {
                        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                        if (psiFile instanceof PsiJavaFile) {
                            RqlxKeyStorage.getInstance(project).removeFile(psiFile);
                        }
                    }
                }

            }
        }
    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        @NotNull Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file != null) {
                for (Project project : projects) {
                    boolean valid = file.isValid();
                    if (valid) {
                        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                        if (psiFile instanceof PsiJavaFile) {
                            RqlxKeyStorage.getInstance(project).refreshFile(psiFile);
                        }
                    }
                }
            }
        }
    }
}
