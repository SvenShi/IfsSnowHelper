package com.ruimin.helper.common.util;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.BooleanValueHolder;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.common.constants.RqlxConstants;
import com.ruimin.helper.dom.dtst.model.Data;
import com.ruimin.helper.dom.rql.model.Mapper;
import com.ruimin.helper.dom.rql.model.Rql;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 12:35
 * @description
 */

public final class RqlxUtils {

    private RqlxUtils() {
        throw new UnsupportedOperationException();
    }

    public static final String[] SQL_METHOD_NAMES = new String[]{"selectOne", "selectList", "selectListWithLock",
        "selectCount", "selectListIn", "selectCountIn", "executeUpdate"};

    /**
     * 查询项目中的所有dtst的data标签
     */
    public static List<Mapper> findAllRqlx(@NotNull Project project, GlobalSearchScope scope) {
        @NotNull List<DomFileElement<Mapper>> elements = DomService.getInstance()
            .getFileElements(Mapper.class, project, scope);
        return elements.stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    /**
     * 获取rqlx key
     *
     * @param rql rql
     * @return {@link String}
     */
    @Nullable
    public static String getRqlxKeyByRqlTag(@NotNull Rql rql) {
        String id = rql.getId().getValue();
        XmlTag xmlTag = rql.getXmlTag();
        Module module = rql.getModule();
        if (xmlTag != null && module != null) {
            PsiFile file = xmlTag.getContainingFile();
            return getRqlxPathByFile(file) + CommonConstants.DOT_SEPARATE + id;
        }
        return null;
    }

    /**
     * 获取rqlx key
     *
     * @param file rqlx文件
     * @param id id
     * @return {@link String}
     */
    public static @NotNull String getRqlxKey(@NotNull PsiFile file, @NotNull String id) {
        return getRqlxPathByFile(file) + CommonConstants.DOT_SEPARATE + id;
    }

    /**
     * 得到包含rqlx key 的元素
     *
     * @param file java文件
     * @param rqlxKey rqlx key
     * @return {@link ArrayList}<{@link PsiElement}>
     */
    @NotNull
    public static ArrayList<PsiElement> findRqlxKeyElements(@NotNull PsiFile file, @NotNull String rqlxKey) {
        ArrayList<PsiElement> elements = new ArrayList<>();
        String text = file.getText();
        if (StringUtils.isNotBlank(text)) {
            for (Integer index : StringUtils.indexOfAll(text, rqlxKey)) {
                PsiElement elementAtOffset = PsiUtil.getElementAtOffset(file, index);
                elements.add(elementAtOffset);
            }
        }
        return elements;
    }

    /**
     * 找到所有包含rqlx key 的元素
     *
     * @param file java文件
     * @param rqlxPath rqlx path
     * @param rqlxId rqlx id
     * @return {@link ArrayList}<{@link PsiElement}>
     */
    @NotNull
    public static ArrayList<PsiElement> findAllRqlxKeyElements(@NotNull PsiFile file, @NotNull String rqlxPath,
        @NotNull String rqlxId) {
        String rqlxKey = rqlxPath + CommonConstants.DOT_SEPARATE + rqlxId;
        ArrayList<PsiElement> elements = new ArrayList<>();
        String text = file.getText();
        if (StringUtils.isNotBlank(text)) {
            for (Integer index : StringUtils.indexOfAll(text, rqlxId)) {
                PsiElement elementAtOffset = PsiUtil.getElementAtOffset(file, index);
                String offsetRqlxId = elementAtOffset.getText();
                boolean target = isRqlxTarget(rqlxKey, elementAtOffset, removeRqlxKeyQuot(offsetRqlxId));
                if (target) {
                    elements.add(elementAtOffset);
                }
            }
        }
        return elements;
    }

    /**
     * 递归判断是否rqlx key 引用的目标
     *
     * @param rqlxKey rqlx key
     * @param element 元素 需要判断的元素
     * @param rqlxKeySuffix rqlx key 的最后一部分
     * @return boolean
     */
    private static boolean isRqlxTarget(String rqlxKey, PsiElement element, String rqlxKeySuffix) {
        PsiElement psiReferenceExpression = element.getParent().getParent().getPrevSibling();
        if (rqlxKey.equals(removeRqlxKeyQuot(rqlxKeySuffix)) && StringUtils.containsAny(
            psiReferenceExpression.getText(), SQL_METHOD_NAMES)) {
            return true;
        } else {
            if (psiReferenceExpression instanceof PsiReferenceExpression) {
                boolean isRqlMethod = StringUtils.containsAny(psiReferenceExpression.getText(), SQL_METHOD_NAMES);
                if (isRqlMethod) {
                    // 是一个rql的查询方法，能进入这段逻辑证明，其中有特殊字符或者根本就不匹配
                    return false;
                } else {
                    // 需要递归寻找调用的方法拼串后再进行判断
                    PsiReference reference = psiReferenceExpression.getReference();
                    if (reference != null) {
                        PsiElement resolve = reference.resolve();
                        if (resolve instanceof PsiMethod) {
                            PsiMethod method = (PsiMethod) resolve;
                            PsiType returnType = method.getReturnType();
                            if (returnType != null && returnType.equalsToText(String.class.getName())) {
                                BooleanValueHolder booleanValueHolder = new BooleanValueHolder(false);
                                method.accept(new JavaRecursiveElementVisitor() {
                                    @Override
                                    public void visitReturnStatement(@NotNull PsiReturnStatement statement) {
                                        PsiExpression returnValue = statement.getReturnValue();
                                        if (returnValue != null) {
                                            returnValue.accept(new JavaRecursiveElementVisitor() {
                                                @Override
                                                public void visitLiteralExpression(
                                                    @NotNull PsiLiteralExpression expression) {
                                                    String text = expression.getText();
                                                    if (StringUtils.isNotBlank(text) && text.contains(
                                                        CommonConstants.DOT_SEPARATE)) {
                                                        boolean rqlxTarget = isRqlxTarget(rqlxKey,
                                                            psiReferenceExpression,
                                                            removeRqlxKeyQuot(text) + rqlxKeySuffix);
                                                        booleanValueHolder.setValue(rqlxTarget);
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });
                                return booleanValueHolder.getValue();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * 被文件前缀rqlx关键
     *
     * @param file 文件
     * @return {@link String}
     */
    @Nullable
    public static String getRqlxPathByFile(@NotNull PsiFile file) {
        String path = file.getVirtualFile().getPath();
        Module module = ModuleUtil.findModuleForPsiElement(file);
        if (module != null) {
            String packagePath = path;
            for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getSourceRoots()) {
                packagePath = StringUtils.remove(packagePath, virtualFile.getPath());
            }
            if (packagePath.startsWith("/") || packagePath.startsWith("\\")) {
                packagePath = packagePath.substring(1);
            }
            packagePath = packagePath.replace("/", ".");
            packagePath = packagePath.replace("\\", ".");
            return packagePath.replace(RqlxConstants.RQLX_FILE_EXTENSION_DOT, "");
        }
        return null;
    }

    /**
     * 根据rqlKey获取所有相应的标签
     *
     * @param scope 范围
     * @param rqlKeys 需要对应的flowid
     * @return 查找到的xmltag
     */
    public static Collection<XmlAttributeValue> findXmlTagByRqlKey(@NotNull GlobalSearchScope scope,
        String... rqlKeys) {
        ArrayList<XmlAttributeValue> xmlAttributeValues = new ArrayList<>();
        HashSet<String> rqlKeySet = Sets.newHashSet(rqlKeys);
        Project project = scope.getProject();
        if (CollectionUtils.isNotEmpty(rqlKeySet) && project != null) {
            for (String key : rqlKeySet) {
                if (StringUtils.isNotBlank(key)) {
                    if (key.contains("\"")) {
                        key = key.replace("\"", "");
                    }
                    int fileAndMethodIndex = StringUtils.lastIndexOf(key, CommonConstants.DOT_SEPARATE);
                    // 字符串处理为包名和rqlx名
                    String methodName = StringUtils.substring(key, fileAndMethodIndex + 1);
                    String rqlxFilePath = StringUtils.substring(key, 0, fileAndMethodIndex);
                    Collection<XmlFile> xmlFiles = findFileByRqlxFilePath(scope, rqlxFilePath);
                    if (CollectionUtils.isNotEmpty(xmlFiles) && StringUtils.isNotBlank(methodName)) {
                        for (XmlFile xmlFile : xmlFiles) {
                            Mapper mapper = getMapperTagByRqlxFile(xmlFile);
                            if (mapper != null) {
                                for (Rql rql : mapper.getRqls()) {
                                    if (methodName.equals(rql.getId().getValue())) {
                                        xmlAttributeValues.add(rql.getId().getXmlAttributeValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return xmlAttributeValues;
    }

    /**
     * 找到rqlx rqlx文件文件路径
     *
     * @param scope 范围
     * @param rqlxFilePath rqlx文件路径
     * @return {@link Collection}<{@link XmlFile}>
     */
    private static Collection<XmlFile> findFileByRqlxFilePath(GlobalSearchScope scope, String rqlxFilePath) {
        ArrayList<XmlFile> xmlFiles = new ArrayList<>();
        int packageAndFileIndex = StringUtils.lastIndexOf(rqlxFilePath, CommonConstants.DOT_SEPARATE);
        String rqlxName = StringUtils.substring(rqlxFilePath, packageAndFileIndex + 1);
        String packageName = StringUtils.substring(rqlxFilePath, 0, packageAndFileIndex);
        // 找到所在的包
        Project project = scope.getProject();
        if (project != null) {
            Collection<VirtualFile> matchPackages = PackageIndex.getInstance(project)
                .getDirsByPackageName(packageName, false)
                .findAll();
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile matchPackage : matchPackages) {
                // 匹配包下面的文件
                VirtualFile child = matchPackage.findChild(rqlxName + RqlxConstants.RQLX_FILE_EXTENSION_DOT);
                if (child != null) {
                    PsiFile file = psiManager.findFile(child);
                    if (isRqlxFile(file)) {
                        xmlFiles.add((XmlFile) file);
                    }
                }
            }
        }
        return xmlFiles;
    }

    /**
     * 是否是rqlx文件
     *
     * @param file
     */
    public static boolean isRqlxFile(@Nullable PsiFile file) {
        Boolean isRqlx = null;
        if (file == null) {
            isRqlx = false;
        }
        if (isRqlx == null) {
            if (!(file instanceof XmlFile)) {
                isRqlx = false;
            }
        }
        if (isRqlx == null) {
            XmlTag rootTag = ((XmlFile) file).getRootTag();
            if (rootTag == null) {
                isRqlx = false;
            }
            if (isRqlx == null) {
                if (!"mapper".equals(rootTag.getName())) {
                    isRqlx = false;
                }
            }
        }
        if (isRqlx == null) {
            isRqlx = true;
        }
        return isRqlx;
    }

    /**
     * 得到rqlx文件的mapper标签
     *
     * @param file rqlx文件
     * @return {@link Data}
     */
    public static Mapper getMapperTagByRqlxFile(PsiFile file) {
        if (isRqlxFile(file)) {
            DomFileElement<Mapper> fileElement = DomManager.getDomManager(file.getProject())
                .getFileElement((XmlFile) file, Mapper.class);
            if (fileElement != null) {
                return fileElement.getRootElement();
            }
        }
        return null;
    }


    /**
     * 找到rql参考
     *
     * @param rqlxPath rqlx的path
     * @param rqlxId rqlx的id
     * @return {@link List}<{@link PsiElement}>
     */
    public static List<PsiElement> findRqlReference(@NotNull String rqlxPath, String rqlxId, Module module) {
        PsiFile[] filesWithWord = CacheManager.getInstance(module.getProject())
            .getFilesWithWord(rqlxId, UsageSearchContext.IN_STRINGS, module.getModuleScope(), true);
        ArrayList<PsiElement> elements = new ArrayList<>();
        for (PsiFile psiFile : filesWithWord) {
            elements.addAll(findAllRqlxKeyElements(psiFile, rqlxPath, rqlxId));
        }

        return elements;

    }


    public static String removeRqlxKeyQuot(@NotNull String rqlxKey) {
        return StringUtils.remove(rqlxKey, "\"");
    }


}
