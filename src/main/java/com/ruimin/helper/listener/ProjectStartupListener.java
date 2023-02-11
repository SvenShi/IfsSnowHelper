package com.ruimin.helper.listener;

import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager.PostStartupActivity;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.ruimin.helper.common.RqlxKeyStorage;
import com.ruimin.helper.common.util.JavaUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/15 上午 03:33
 * @description
 */
public class ProjectStartupListener extends PostStartupActivity {

    public static final Logger log = Logger.getInstance(ProjectStartupListener.class);

    @Override
    public void runActivity(@NotNull Project project) {
        ProgressManager.getInstance().run(new Backgroundable(null, "IfsSnowHelper init", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                boolean loading = true;
                GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
                AtomicInteger previousSize = new AtomicInteger();
                Processor<AtomicInteger> checkProcessor = ReadActionProcessor.wrapInReadAction(
                    new ReadActionProcessor<>() {
                        @Override
                        public boolean processInReadAction(AtomicInteger previousSize) {
                            int nowSize = JavaUtils.findAllJavaFile(projectScope).size();
                            if (previousSize.get() != nowSize) {
                                log.info(project.getName() + " 项目未初始化成功，等待初始化成功后，启动 rqlx key存储器的初始化任务！上次java文件个数："
                                    + previousSize + "，本次文件个数：" + nowSize);
                                previousSize.set(nowSize);
                                return true;
                            } else {
                                log.info(project.getName() + " 初始化完成！上次java文件个数：" + previousSize + "，本次文件个数："
                                    + nowSize);
                                return false;
                            }
                        }
                    });


                do {
                    try {
                        loading = checkProcessor.process(previousSize);
                        if (loading){
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        log.info("发生异常：" + e.getMessage());
                    }
                } while (loading);

                Processor<Project> objectProcessor = ReadActionProcessor.wrapInReadAction(new ReadActionProcessor<>() {
                    @Override
                    public boolean processInReadAction(Project project) {
                        RqlxKeyStorage.getInstance(project).init();
                        return true;
                    }
                });
                objectProcessor.process(project);
            }
        });
    }


}
