package com.ruimin.helper.util;

import com.google.common.collect.Lists;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Java utils.
 *
 * @author yanglin
 */
public final class JavaUtils {

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Is model clazz boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    public static boolean isModelClazz(@Nullable PsiClass clazz) {
        return null != clazz && !clazz.isAnnotationType() && !clazz.isInterface() && !clazz.isEnum() && clazz.isValid();
    }

    /**
     * Find settable psi field optional.
     *
     * @param clazz the clazz
     * @param propertyName the property name
     * @return the optional
     */
    public static Optional<PsiField> findSettablePsiField(@NotNull PsiClass clazz, @Nullable String propertyName) {
        return Optional.ofNullable(PropertyUtil.findPropertyField(clazz, propertyName, false));
    }

    /**
     * Find settable psi fields psi field [ ].
     *
     * @param clazz the clazz
     * @return the psi field [ ]
     */
    @NotNull
    public static PsiField[] findSettablePsiFields(@NotNull PsiClass clazz) {
        PsiMethod[] methods = clazz.getAllMethods();
        List<PsiField> fields = Lists.newArrayList();
        for (PsiMethod method : methods) {
            if (PropertyUtil.isSimplePropertySetter(method)) {
                Optional<PsiField> psiField = findSettablePsiField(clazz, PropertyUtil.getPropertyName(method));
                psiField.ifPresent(fields::add);
            }
        }
        return fields.toArray(new PsiField[0]);
    }

    /**
     * Is element within interface boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(type).isPresent() && type.isInterface();
    }

    /**
     * Find clazz optional.
     *
     * @param project the project
     * @param clazzName the clazz name
     * @return the optional
     */
    public static Optional<PsiClass> findClazz(@NotNull Project project, @NotNull String clazzName) {
        String classNameNeedFind = clazzName;
        if (classNameNeedFind.contains("$")) {
            classNameNeedFind = classNameNeedFind.replace("$", ".");
        }
        final JavaPsiFacade instance = JavaPsiFacade.getInstance(project);
        return Optional.ofNullable(instance.findClass(classNameNeedFind, GlobalSearchScope.allScope(project)));
    }

    /**
     * Find clazz optional.
     *
     * @param project the project
     * @param clazzName the clazz name
     * @return the optional
     */
    public static Optional<PsiClass[]> findClasses(@NotNull Project project, @NotNull String clazzName) {
        return Optional.of(
            JavaPsiFacade.getInstance(project).findClasses(clazzName, GlobalSearchScope.allScope(project)));
    }

    /**
     * Find clazz optional.
     *
     * @param module the project
     * @param clazzName the clazz name
     * @return the optional
     */
    public static Optional<PsiClass[]> findClasses(@NotNull Module module, @NotNull String clazzName) {
        return Optional.of(
            JavaPsiFacade.getInstance(module.getProject()).findClasses(clazzName, module.getModuleScope()));
    }

    /**
     * Find method optional.
     *
     * @param project the project
     * @param clazzName the clazz name
     * @param methodName the method name
     * @return the optional
     */
    public static List<PsiMethod> findMethods(@NotNull Project project, @NotNull String clazzName,
        @Nullable String methodName) {
        Optional<PsiClass[]> classes = findClasses(project, clazzName);
        return classes.map(psiClasses -> Arrays.stream(psiClasses)
            .map(psiClass -> psiClass.findMethodsByName(methodName, true))
            .flatMap(Arrays::stream)
            .collect(Collectors.toList())).orElse(null);
    }


    /**
     * 找到方法
     *
     * @param module 模块
     * @param clazzName clazz名字
     * @param methodName 方法名称
     * @return {@link List}<{@link PsiMethod}>
     */
    public static List<PsiMethod> findMethods(@NotNull Module module, @NotNull String clazzName,
        @Nullable String methodName) {
        Optional<PsiClass[]> classes = findClasses(module, clazzName);
        return classes.map(psiClasses -> Arrays.stream(psiClasses)
            .map(psiClass -> psiClass.findMethodsByName(methodName, true))
            .flatMap(Arrays::stream)
            .collect(Collectors.toList())).orElse(null);
    }

    /**
     * 找到所有java文件
     *
     * @param scope 模块范围
     * @return {@link Collection}<{@link VirtualFile}>
     */
    public static Collection<VirtualFile> findAllJavaFile(@NotNull GlobalSearchScope scope) {
        return FileTypeIndex.getFiles(JavaFileType.INSTANCE, scope);
    }

}
