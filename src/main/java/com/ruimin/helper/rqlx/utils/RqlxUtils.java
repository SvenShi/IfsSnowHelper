package com.ruimin.helper.rqlx.utils;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.ruimin.helper.common.bean.Holder;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Data;
import com.ruimin.helper.rqlx.constans.RqlxConstants;
import com.ruimin.helper.rqlx.dom.model.Mapper;
import com.ruimin.helper.rqlx.dom.model.Rql;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
            "selectCount", "selectListIn", "selectCountIn", "executeUpdate", "executeBatchUpdate"};

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
     * @param id   id
     * @return {@link String}
     */
    public static @NotNull String getRqlxKey(@NotNull PsiFile file, @NotNull String id) {
        return getRqlxPathByFile(file) + CommonConstants.DOT_SEPARATE + id;
    }

    /**
     * 得到包含rqlx key 的元素
     *
     * @param file    java文件
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
     * @param file     java文件
     * @param rqlxPath rqlx path
     * @param rqlxId   rqlx id
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
                boolean target = isRqlxTarget(rqlxKey, elementAtOffset.getParent(), removeRqlxKeyQuot(offsetRqlxId));
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
     * @param rqlxKey       rqlx key
     * @param element       元素 需要判断的元素
     * @param rqlxKeySuffix rqlx key 的最后一部分
     * @return boolean
     */
    private static boolean isRqlxTarget(String rqlxKey, PsiElement element, String rqlxKeySuffix) {
        PsiMethodCallExpression callExpression = getLatestMethodCallExpressionFromParent(element);
        if (callExpression != null) {
            PsiElement psiReferenceExpression = callExpression.getFirstChild();
            if (rqlxKey.equals(rqlxKeySuffix) && isRqlxMethodName(psiReferenceExpression.getText())) {
                return true;
            } else {
                if (psiReferenceExpression instanceof PsiReferenceExpression) {
                    boolean isRqlMethod = isRqlxMethodName(psiReferenceExpression.getText());
                    if (isRqlMethod) {
                        // 是一个rql的查询方法，能进入这段逻辑证明，其中有特殊字符或者根本就不匹配
                        return false;
                    } else {
                        // 需要递归寻找调用的方法拼串后再进行判断
                        String splicedRqlxKey = getSplicedRqlxKey(psiReferenceExpression, rqlxKeySuffix);
                        return rqlxKey.equals(splicedRqlxKey);
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
     * @param scope   范围
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
     * @param scope        范围
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
                    .getDirsByPackageName(packageName, scope)
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
     * @param rqlxId   rqlx的id
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


    /**
     * 删除rqlx key 两边的引号
     *
     * @param rqlxKey rqlx关键
     * @return {@link String}
     */
    public static String removeRqlxKeyQuot(@NotNull String rqlxKey) {
        return StringUtils.remove(rqlxKey, "\"");
    }


    /**
     * 判断是否是rqlx的java方法
     *
     * @param methodName 方法名称
     * @return boolean
     */
    public static boolean isRqlxMethodName(String methodName) {
        return StringUtils.endsWithAny(methodName, SQL_METHOD_NAMES);
    }

    /**
     * 是否是拼接的rqlx Key
     *
     * @param referenceExpression 引用表达式
     * @return boolean
     */
    public static boolean isSpliceRqlxKey(PsiElement referenceExpression) {
        if (referenceExpression == null) {
            return false;
        }
        if (isRqlxMethodName(referenceExpression.getText())) {
            return true;
        } else {
            PsiMethodCallExpression callExpression = getLatestMethodCallExpressionFromParent(referenceExpression);
            PsiMethodCallExpression parentCallExpression = getLatestMethodCallExpressionFromParent(callExpression);
            if (parentCallExpression != null) {
                return isSpliceRqlxKey(parentCallExpression.getFirstChild());
            }
        }
        return false;
    }

    /**
     * 得到拼接的rqlx key
     *
     * @param referenceExpression 引用表达式
     * @param rqlxKey             rqlx key
     * @return {@link String}
     */
    @Nullable
    public static String getSplicedRqlxKey(PsiElement referenceExpression, String rqlxKey) {
        if (referenceExpression == null) {
            return null;
        }
        if (isRqlxMethodName(referenceExpression.getText())) {
            return rqlxKey;
        }
        PsiReference reference = referenceExpression.getReference();
        if (reference != null) {
            PsiElement resolve = reference.resolve();
            if (resolve instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) resolve;
                PsiType returnType = method.getReturnType();
                if (returnType != null && returnType.equalsToText(String.class.getName())) {
                    Holder<String> rqlxKeyHolder = new Holder<>("");
                    method.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitReturnStatement(@NotNull PsiReturnStatement statement) {
                            PsiExpression returnValue = statement.getReturnValue();
                            if (returnValue != null) {
                                returnValue.accept(new JavaRecursiveElementVisitor() {
                                    @Override
                                    public void visitLiteralExpression(@NotNull PsiLiteralExpression expression) {
                                        String text = expression.getText();
                                        if (StringUtils.isNotBlank(text) &&
                                                text.contains(CommonConstants.DOT_SEPARATE)) {
                                            // 调用第一遍是传进来的参数的方法本身
                                            PsiMethodCallExpression callExpression = getLatestMethodCallExpressionFromParent(
                                                    referenceExpression);
                                            // 调用第二遍是是本方法的上一层方法
                                            PsiMethodCallExpression parentCallExpression = getLatestMethodCallExpressionFromParent(
                                                    callExpression);
                                            if (parentCallExpression != null) {
                                                String splicedRqlxKey = getSplicedRqlxKey(
                                                        parentCallExpression.getFirstChild(),
                                                        StringUtils.removeQuot(text) + rqlxKey);
                                                rqlxKeyHolder.set(splicedRqlxKey);
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    });
                    return rqlxKeyHolder.get();
                }
            }
        }
        return null;
    }

    /**
     * 得到最新方法调用表达式
     *
     * @param element 元素
     * @return {@link PsiMethodCallExpression}
     */
    @Nullable
    public static PsiMethodCallExpression getLatestMethodCallExpressionFromParent(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement parent = element.getParent();
        if (!(parent instanceof PsiExpression) && !(parent instanceof PsiExpressionList)) {
            return null;
        }
        if (parent instanceof PsiMethodCallExpression) {
            return ((PsiMethodCallExpression) parent);
        }
        return getLatestMethodCallExpressionFromParent(parent);
    }

    /**
     * 根据rqlxPath找到文件
     *
     * @param rqlxPath rqlx路径
     * @param scope    范围
     * @return {@link List}<{@link PsiFile}>
     */
    public static List<PsiFile> findFilesByPath(@NotNull String rqlxPath, @NotNull GlobalSearchScope scope) {
        ArrayList<PsiFile> psiFiles = new ArrayList<>();
        Project project = scope.getProject();
        if (project != null) {
            // 所属项目
            PsiManager psiManager = PsiManager.getInstance(project);
            // dataset标签的path属性
            if (StringUtils.isNotBlank(rqlxPath)) {
                int index = StringUtils.lastIndexOf(rqlxPath, CommonConstants.DOT_SEPARATE);
                // 字符串处理为包名和dataset名
                String fileName = StringUtils.substring(rqlxPath, index + 1);
                String packageName = StringUtils.substring(rqlxPath, 0, index);
                // 找到所在的包
                Collection<VirtualFile> matchPackages = PackageIndex.getInstance(project)
                        .getDirsByPackageName(packageName, scope)
                        .findAll();
                for (VirtualFile matchPackage : matchPackages) {
                    // 匹配包下面的文件
                    VirtualFile child = matchPackage.findChild(fileName + RqlxConstants.RQLX_FILE_EXTENSION_DOT);
                    if (child != null) {
                        boolean contains = scope.contains(child);
                        if (contains) {
                            PsiFile file = psiManager.findFile(child);
                            psiFiles.add(file);
                        }
                    } else {
                        VirtualFile packageChild = matchPackage.findChild(fileName);
                        if (packageChild != null && scope.contains(packageChild)) {
                            PsiFile file = psiManager.findFile(packageChild);
                            psiFiles.add(file);
                        }
                    }
                }
            }
        }
        return psiFiles;
    }

}
