package com.ruimin.helper.common;

import com.intellij.codeInsight.CommentUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
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
public class RqlxKeyStore {


    private static final HashMap<Project, RqlxKeyStore> instanceMap = new HashMap<>();

    private static final int INIT = 0;

    private static final int INITING = 1;

    private static final int RUNNING = 2;

    private static final int STOPPED = 3;


    private final HashMap<Module, HashMap<String, List<PsiElement>>> store = new HashMap<>();


    private final HashMap<PsiFile, Set<String>> fileMap = new HashMap<>();

    private final Project project;

    private int state = 0;

    private int length = 0;


    private RqlxKeyStore(Project project) {
        this.project = project;
    }

    public static RqlxKeyStore getInstance(Project project) {
        RqlxKeyStore rqlxKeyStore = instanceMap.get(project);
        if (rqlxKeyStore == null) {
            synchronized (RqlxKeyStore.class) {
                if (instanceMap.get(project) == null) {
                    rqlxKeyStore = new RqlxKeyStore(project);
                    instanceMap.put(project, rqlxKeyStore);
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
            HashMap<String, List<PsiElement>> elementMap = store.get(module);
            if (elementMap != null) {
                return elementMap.get(RqlxKey);
            }
        }
        return null;
    }


    /**
     * 初始化
     */
    public void init() {
        synchronized (this) {
            if (state == INITING || state == RUNNING) {
                return;
            }
            if (state == INIT) {
                try {
                    state = INITING;
                    for (Module mod : ModuleManager.getInstance(project).getModules()) {
                        HashMap<String, List<PsiElement>> elementHashMap = new HashMap<>();
                        Collection<VirtualFile> allJavaFile = JavaUtils.findAllJavaFile(mod.getModuleScope(false));
                        // 遍历所有的java文件
                        for (VirtualFile virtualFile : allJavaFile) {
                            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                            if (file != null) {
                                ArrayList<PsiElement> rqlxKeyElement = getRqlxKeyElement(file);
                                if (CollectionUtils.isNotEmpty(rqlxKeyElement)) {
                                    storeElement(elementHashMap, file, rqlxKeyElement);
                                }
                            }
                        }
                        store.put(mod, elementHashMap);
                    }
                    state = RUNNING;
                } catch (Exception e) {
                    store.clear();
                    state = INITING;
                    init();
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
                    ArrayList<PsiElement> rqlxKeyElement = getRqlxKeyElement(file);
                    if (CollectionUtils.isNotEmpty(rqlxKeyElement)) {
                        Module module = ModuleUtil.findModuleForFile(file);
                        if (module != null) {
                            HashMap<String, List<PsiElement>> elementHashMap = store.get(module);
                            if (elementHashMap == null) {
                                elementHashMap = new HashMap<>();
                                store.put(module, elementHashMap);
                            }
                            storeElement(elementHashMap, file, rqlxKeyElement);
                        }
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
    public void clearCache(PsiFile file) {
        synchronized (this) {
            if (state == RUNNING) {
                if (file instanceof PsiJavaFile) {
                    PsiManager psiManager = PsiManager.getInstance(file.getProject());
                    Set<String> keys = fileMap.get(file);
                    if (CollectionUtils.isNotEmpty(keys)) {
                        for (String key : keys) {
                            Module module = ModuleUtil.findModuleForFile(file);
                            if (module != null) {
                                List<PsiElement> elements = getElements(module, key);
                                if (CollectionUtils.isNotEmpty(elements)) {
                                    Iterator<PsiElement> iterator = elements.iterator();
                                    while (iterator.hasNext()) {
                                        PsiElement element = iterator.next();
                                        VirtualFile virtualFile = PsiUtil.getVirtualFile(element);
                                        if (virtualFile != null) {
                                            PsiFile newFile = psiManager.findFile(virtualFile);
                                            if (file.equals(newFile)) {
                                                iterator.remove();
                                                length--;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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
     * 得到rqlx关键要素
     *
     * @param file 文件
     * @return boolean
     */
    private ArrayList<PsiElement> getRqlxKeyElement(PsiFile file) {
        ArrayList<PsiElement> elements = new ArrayList<>();
        // 所有的方法名
        for (String rqlxMethodName : RqlxUtils.SQL_METHOD_NAME) {
            List<Integer> indexes = StringUtils.indexOfAll(file.getText(), rqlxMethodName);
            if (CollectionUtils.isNotEmpty(indexes)) {
                // 是否包含方法名
                for (Integer index : indexes) {
                    // 得到该位置的元素
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
                                        elements.add(rqlxElement);
                                    }
                                }
                            }
                        }
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
     * @param rqlxKeyElement rqlx关键要素
     */
    private void storeElement(HashMap<String, List<PsiElement>> elementHashMap, PsiFile file,
        ArrayList<PsiElement> rqlxKeyElement) {
        for (PsiElement rqlxElement : rqlxKeyElement) {
            String text = rqlxElement.getText();
            if (text.contains(".") && text.startsWith("\"") && text.endsWith("\"")) {
                // 存放资源
                String rqlxKey = text.substring(1, text.length() - 1);
                List<PsiElement> psiElements = elementHashMap.get(rqlxKey);
                if (psiElements == null) {
                    psiElements = new ArrayList<>();
                    elementHashMap.put(rqlxKey, psiElements);
                }
                psiElements.add(rqlxElement);

                Set<String> keys = fileMap.get(file);
                if (keys == null) {
                    keys = new HashSet<>();
                    fileMap.put(file, keys);
                }
                keys.add(rqlxKey);
                length++;
            }
        }
    }

    public void destroy() {
        store.clear();
        fileMap.clear();
        instanceMap.remove(project);
        state = STOPPED;
    }

}