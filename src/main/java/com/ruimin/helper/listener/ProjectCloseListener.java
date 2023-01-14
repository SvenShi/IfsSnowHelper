package com.ruimin.helper.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.ruimin.helper.common.RqlxKeyStore;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/15 上午 03:33
 * @description
 */
public class ProjectCloseListener implements ProjectManagerListener {

    /**
     * Invoked on project close before any closing activities
     *
     * @param project
     */
    @Override
    public void projectClosing(@NotNull Project project) {
        RqlxKeyStore.getInstance(project).destroy();
    }
}
