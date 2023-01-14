package com.ruimin.helper.listener;

import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager.PostStartupActivity;
import com.intellij.util.Processor;
import com.ruimin.helper.common.RqlxKeyStore;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/15 上午 03:33
 * @description
 */
public class ProjectStartupListener extends PostStartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        ProgressManager.getInstance().run(new Backgroundable(null, "IfsSnowHelper init", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                Processor<ProgressIndicator> objectProcessor = ReadActionProcessor.wrapInReadAction(
                    new ReadActionProcessor<>() {
                        @Override
                        public boolean processInReadAction(ProgressIndicator progressIndicator) {
                            RqlxKeyStore.getInstance(project).init();
                            return true;
                        }
                    });
                objectProcessor.process(indicator);
            }
        });
    }



}
