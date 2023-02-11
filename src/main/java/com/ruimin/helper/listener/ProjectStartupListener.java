package com.ruimin.helper.listener;

import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task.Modal;
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
        ProgressManager.getInstance().run(new Modal(project, "IfsSnowHelper 初始化Rqlx Key缓存", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("正在确保所有Java文件加载完毕");
                boolean loading = true;
                RqlxKeyStorage rqlxKeyStorage = RqlxKeyStorage.getInstance(project);
                GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
                AtomicInteger previousSize = new AtomicInteger(-1);
                Processor<AtomicInteger> checkProcessor = ReadActionProcessor.wrapInReadAction(
                    new ReadActionProcessor<>() {
                        @Override
                        public boolean processInReadAction(AtomicInteger previousSize) {
                            int nowSize = JavaUtils.findAllJavaFile(projectScope).size();
                            indicator.setText2("已加载" + nowSize);
                            if (previousSize.get() != nowSize) {
                                log.info(project.getName() + " 项目未初始化成功，等待初始化成功后，启动! 上次java文件个数："
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
                        if (loading) {
                            Thread.sleep(2000);
                        }
                    } catch (Exception e) {
                        log.info("发生异常：" + e.getMessage());
                    }
                } while (loading);

                Processor<RqlxKeyStorage> rqlxKeyStorageProcessor = ReadActionProcessor.wrapInReadAction(
                    new ReadActionProcessor<>() {
                        @Override
                        public boolean processInReadAction(RqlxKeyStorage rqlxKeyStorage) {
                            rqlxKeyStorage.init(indicator);
                            return true;
                        }
                    });
                rqlxKeyStorageProcessor.process(rqlxKeyStorage);
            }
        });


    }
}
