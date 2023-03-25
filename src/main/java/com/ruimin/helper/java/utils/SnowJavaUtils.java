package com.ruimin.helper.java.utils;

import com.google.common.collect.Lists;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.ruimin.helper.common.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Java utils.
 *
 * @author yanglin
 */
public final class SnowJavaUtils {

    private SnowJavaUtils() {
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
     * @param clazzName the clazz name
     * @return the optional
     */
    public static Optional<List<PsiClass>> findClasses(@NotNull GlobalSearchScope scope, @NotNull String clazzName) {
        if (scope.getProject() == null) {
            return Optional.empty();
        }
        List<PsiClass> collect = Arrays.stream(
            JavaPsiFacade.getInstance(scope.getProject()).findClasses(clazzName, scope)).filter(item -> {
            PsiFile containingFile = item.getContainingFile();
            return scope.contains(containingFile.getVirtualFile());
        }).collect(Collectors.toList());
        return Optional.of(collect);
    }

    public static Optional<PsiPackage> findPackage(@NotNull Project project, @NotNull String packageName) {
        PsiPackage aPackage = JavaPsiFacade.getInstance(project).findPackage(packageName);
        if (aPackage != null) {
            return Optional.of(aPackage);
        }
        return Optional.empty();
    }

    /**
     * Find method optional.
     *
     * @param clazzName the clazz name
     * @param methodName the method name
     * @return the optional
     */
    public static List<PsiMethod> findMethods(@NotNull GlobalSearchScope scope, @NotNull String clazzName,
        @Nullable String methodName) {
        Optional<List<PsiClass>> classes = findClasses(scope, clazzName);
        ArrayList<PsiMethod> psiMethods = new ArrayList<>();
        if (classes.isPresent()) {
            List<PsiClass> psiClasses = classes.get();
            for (PsiClass psiClass : psiClasses) {
                psiClass.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitMethod(@NotNull PsiMethod method) {
                        if (StringUtils.isNotBlank(methodName)) {
                            if (methodName.equals(method.getName())) {
                                psiMethods.add(method);
                            }
                        } else {
                            psiMethods.add(method);
                        }
                    }
                });
            }
        }
        return psiMethods;
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
        Optional<List<PsiClass>> classes = findClasses(module.getModuleScope(), clazzName);
        return classes.map(psiClasses -> psiClasses.stream()
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
