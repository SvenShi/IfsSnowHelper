package com.ruimin.helper.common;

import com.intellij.codeInsight.CommentUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiUtil;
import com.ruimin.helper.common.util.JavaUtils;
import com.ruimin.helper.common.util.RqlxUtils;
import com.ruimin.helper.common.util.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 下午 10:40
 * @description 保存key和class的关联关系
 */

public class RqlxKeyStorage {

    public static final Logger log = Logger.getInstance(RqlxKeyStorage.class);


    /**
     * 每个项目都有独立的实例
     */
    private static final HashMap<Project, RqlxKeyStorage> instanceMap = new HashMap<>();


    /**
     * 初始化中
     */
    private static final int INITING = 1;

    /**
     * 运行中
     */
    private static final int RUNNING = 2;

    /**
     * 已停止
     */
    private static final int STOPPED = 3;


    /**
     * 存储
     */
    private final HashMap<Module, HashMap<String, List<PsiElement>>> storage = new HashMap<>();


    /**
     * 文件对应的rqlx key
     */
    private final HashMap<String, Set<String>> fileMap = new HashMap<>();


    /**
     * 读取文件记录
     */
    private final HashSet<String> readFileRecord = new HashSet<>();


    /**
     * 项目
     */
    private final Project project;

    /**
     * 当前状态
     */
    private int state = 3;

    /**
     * 仓库总长度
     */
    private int length = 0;


    private RqlxKeyStorage(Project project) {
        this.project = project;
    }

    public static RqlxKeyStorage getInstance(Project project) {
        RqlxKeyStorage rqlxKeyStorage = instanceMap.get(project);
        if (rqlxKeyStorage == null) {
            synchronized (RqlxKeyStorage.class) {
                if (instanceMap.get(project) == null) {
                    rqlxKeyStorage = new RqlxKeyStorage(project);
                    instanceMap.put(project, rqlxKeyStorage);
                }
            }
        }
        return instanceMap.get(project);
    }

    /**
     * 获取元素
     *
     * @param module 模块
     * @param RqlxKey rqlx关键
     * @return {@link List}<{@link PsiElement}>
     */
    @Nullable
    public List<PsiElement> getElements(@NotNull Module module, @NotNull String RqlxKey) {
        if (state == RUNNING) {
            HashMap<String, List<PsiElement>> elementMap = storage.get(module);
            if (elementMap != null) {
                return elementMap.get(RqlxKey);
            }
        }
        return null;
    }


    /**
     * 初始化
     */
    public void init(@NotNull ProgressIndicator indicator) {
        synchronized (this) {
            if (state == STOPPED) {
                try {
                    state = INITING;
                    indicator.setIndeterminate(false);
                    log.info(project.getName() + " 开始初始化rqlx key 存储器！");
                    for (Module mod : ModuleManager.getInstance(project).getModules()) {
                        indicator.setText("正在扫描 " + mod.getName() + " Java文件中的Rqlx key");
                        HashMap<String, List<PsiElement>> elementHashMap = new HashMap<>();
                        Collection<VirtualFile> allJavaFile = JavaUtils.findAllJavaFile(mod.getModuleScope());
                        double progress = 0.0;
                        double step = 1.0 / allJavaFile.size();
                        log.info("等待扫描的java文件共" + allJavaFile.size() + "个");
                        // 遍历所有的java文件
                        for (VirtualFile virtualFile : allJavaFile) {
                            indicator.setText2(virtualFile.getName());
                            readFileRecord.add(virtualFile.getPath());
                            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                            storeElement(elementHashMap, file);
                            indicator.setFraction(progress += step);
                        }
                        storage.put(mod, elementHashMap);
                    }
                    state = RUNNING;
                    log.info("初始化完毕！共" + length + "个元素存储完毕");
                } catch (Exception e) {
                    log.info("发生异常！等待重新尝试！", e);
                    storage.clear();
                    state = INITING;
                    init(indicator);
                }
            }
        }
    }


    /**
     * 更新文件
     *
     * @param file 文件
     */
    public void refreshFile(PsiFile file) {
        synchronized (this) {
            if (state == RUNNING) {
                if (file instanceof PsiJavaFile) {
                    log.info(
                        project.getName() + " 更新文件[" + file.getName() + "]的rqlx key 缓存，存储器总长度：" + length);
                    removeFile(file);
                    Module module = ModuleUtil.findModuleForFile(file);
                    if (module != null) {
                        readFileRecord.add(file.getVirtualFile().getPath());
                        HashMap<String, List<PsiElement>> elementHashMap = storage.get(module);
                        if (elementHashMap == null) {
                            elementHashMap = new HashMap<>();
                            storage.put(module, elementHashMap);
                        }
                        int size = storeElement(elementHashMap, file);
                        log.info(project.getName() + " 文件[" + file.getName() + "]rqlx key 缓存更新完毕，更新" + size
                            + "个元素，存储器总长度：" + length);
                    }
                }
            }
        }
    }

    /**
     * 清除缓存
     *
     * @param file 文件
     */
    public void removeFile(PsiFile file) {
        synchronized (this) {
            if (state == RUNNING) {
                VirtualFile handleVirtualFile = file.getVirtualFile();
                if (file instanceof PsiJavaFile && isHasRead(handleVirtualFile)) {
                    log.info(project.getName() + " 开始清除文件[" + file.getName() + "]的缓存，存储器总长度：" + length);
                    int tempLength = length;
                    String fileKey = handleVirtualFile.getPath();
                    readFileRecord.remove(fileKey);
                    Set<String> keys = fileMap.get(fileKey);
                    if (CollectionUtils.isNotEmpty(keys)) {
                        for (String key : keys) {
                            Module module = ModuleUtil.findModuleForFile(file);
                            if (module != null) {
                                List<PsiElement> elements = getElements(module, key);
                                if (CollectionUtils.isNotEmpty(elements)) {
                                    Iterator<PsiElement> iterator = elements.iterator();
                                    while (iterator.hasNext()) {
                                        PsiElement element = iterator.next();
                                        PsiElement elementAt = file.findElementAt(element.getTextOffset());
                                        if (elementAt != null && element.getText().equals(elementAt.getText())) {
                                            iterator.remove();
                                            length--;
                                        }
                                    }
                                }
                            }
                        }
                        fileMap.remove(fileKey);
                    }
                    log.info(
                        project.getName() + " 文件[" + file.getName() + "]缓存清除成功！共清除[" + (tempLength - length)
                            + "]个元素，存储器总长度：" + length);
                }
            }
        }
    }


    /**
     * 得到项目
     *
     * @return {@link Set}<{@link Project}>
     */
    public Project getProject() {
        return project;
    }

    /**
     * 得到包含rqlx key 的元素
     *
     * @param file 文件
     * @return boolean
     */
    @NotNull
    private ArrayList<PsiElement> getRqlxKeyElement(PsiFile file) {
        ArrayList<PsiElement> elements = new ArrayList<>();
        // 所有的方法名
        for (String rqlxMethodName : RqlxUtils.SQL_METHOD_NAMES) {
            List<Integer> indexes = StringUtils.indexOfAll(file.getText(), rqlxMethodName);
            if (CollectionUtils.isNotEmpty(indexes)) {
                // 是否包含方法名
                for (Integer index : indexes) {
                    // 得到该位置的元素
                    if (index < file.getTextLength()) {
                        PsiElement elementAtOffset = PsiUtil.getElementAtOffset(file, index);
                        if (elementAtOffset instanceof PsiIdentifier && rqlxMethodName.equals(elementAtOffset.getText())
                            && !CommentUtil.isComment(elementAtOffset)) {
                            PsiElement express = elementAtOffset.getParent();
                            if (express instanceof PsiReferenceExpression) {
                                PsiElement callExpress = express.getParent();
                                if (callExpress instanceof PsiMethodCallExpression) {
                                    PsiElement expressList = callExpress.getLastChild();
                                    PsiElement[] children = expressList.getChildren();
                                    if (children.length > 1) {
                                        PsiElement child = children[1];
                                        if (child instanceof PsiLiteralExpression) {
                                            PsiLiteralExpression rqlxElement = (PsiLiteralExpression) child;
                                            String text = rqlxElement.getText();
                                            if (text.contains(".") && text.startsWith("\"") && text.endsWith("\"")) {
                                                elements.add(rqlxElement);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("获取元素时下标超长！index:" + index + ",fileTextLength:" + file.getTextLength());
                    }
                }
            }
        }
        return elements;
    }

    /**
     * 存储元素
     *
     * @param elementHashMap 散列映射元素
     * @param file 文件
     */
    private int storeElement(HashMap<String, List<PsiElement>> elementHashMap, PsiFile file) {
        if (file != null) {
            VirtualFile virtualFile = file.getVirtualFile();
            String filePath = virtualFile.getPath();
            Set<String> keys = fileMap.get(filePath);
            if (keys == null) {
                keys = new HashSet<>();
                fileMap.put(filePath, keys);
            }

            ArrayList<PsiElement> rqlxKeyElement = getRqlxKeyElement(file);
            for (PsiElement rqlxElement : rqlxKeyElement) {
                String text = rqlxElement.getText();
                // 存放资源 去掉双引号
                String rqlxKey = text.substring(1, text.length() - 1);
                List<PsiElement> psiElements = elementHashMap.get(rqlxKey);
                if (psiElements == null) {
                    psiElements = new ArrayList<>();
                    elementHashMap.put(rqlxKey, psiElements);
                }
                psiElements.add(rqlxElement);
                keys.add(rqlxKey);
                length++;
            }
            return rqlxKeyElement.size();
        }
        return 0;
    }

    public void destroy() {
        storage.clear();
        fileMap.clear();
        instanceMap.remove(project);
        state = STOPPED;
        log.info(project.getName() + " rqlx key 存储器 已销毁！");
    }

    /**
     * 获得读取的文件长度
     *
     * @return int
     */
    public int getReadFileSize() {
        return readFileRecord.size();
    }

    /**
     * 是否读取过文件
     *
     * @param file 文件
     * @return boolean
     */
    public boolean isHasRead(VirtualFile file) {
        return readFileRecord.contains(file.getPath());
    }

    public boolean isInitializing() {
        return state == INITING;
    }

    public boolean isRunning() {
        return state == RUNNING;
    }

    public boolean isStopped() {
        return state == STOPPED;
    }


}
