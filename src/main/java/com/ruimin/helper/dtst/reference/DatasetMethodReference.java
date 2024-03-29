package com.ruimin.helper.dtst.reference;

import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.java.utils.SnowJavaUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class DatasetMethodReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public DatasetMethodReference(@NotNull XmlAttributeValue element) {
        super(Objects.requireNonNull(element),
            new TextRange(1, DataUtils.mustPositive(element.getTextLength() - 1, 0)));
    }

    /**
     * Returns the array of String, {@link PsiElement} and/or {@link LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array are used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by the IDE.)
     * <p>
     * This method is default since 2018.3.
     *
     * @return the array of available identifiers.
     */
    @Override
    public Object @NotNull [] getVariants() {
        String value = myElement.getValue();
        if (StringUtils.isNotBlank(value)) {
            Module module = ModuleUtil.findModuleForPsiElement(myElement);
            if (module == null) {
                return super.getVariants();
            }
            String beforeValue = StringUtils.substringBeforeLast(value, CompletionUtil.DUMMY_IDENTIFIER);

            ArrayList<LookupElement> result = new ArrayList<>();
            if (beforeValue.contains(".")) {
                String packageName = StringUtils.substringBeforeLast(beforeValue, ".");
                Optional<PsiPackage> aPackage = SnowJavaUtils.findPackage(module.getProject(), packageName);
                List<PsiMethod> methods = SnowJavaUtils.findMethods(module, packageName, null);
                if (aPackage.isPresent()) {
                    PsiPackage psiPackage = aPackage.get();
                    PsiPackage[] subPackages = psiPackage.getSubPackages(module.getModuleScope());
                    PsiClass[] classes = psiPackage.getClasses(module.getModuleScope());
                    for (PsiPackage subPackage : subPackages) {
                        result.add(new SnowLookUpElement(subPackage.getQualifiedName(), subPackage));
                    }
                    for (PsiClass aClass : classes) {
                        result.add(new SnowLookUpElement(aClass.getQualifiedName(), aClass));
                    }
                }
                for (PsiMethod method : methods) {
                    result.add(new SnowLookUpElement(packageName + "." + method.getName(), method));
                }
            }
            return result.toArray();
        }
        return super.getVariants();
    }

    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or {@code null} if it was not possible to resolve the reference to a valid target.
     * @see PsiPolyVariantReference#multiResolve(boolean)
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }


    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     * being resolved is considered incomplete, and the method may return additional
     * invalid results.
     * @return the array of results for resolving the reference.
     */
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        String methodPath = myElement.getValue();
        if (StringUtils.isNotBlank(methodPath)) {
            String[] split = StringUtils.splitLast(methodPath, CommonConstants.DOT_SEPARATE);
            if (split.length == 2) {
                Module module = ModuleUtil.findModuleForPsiElement(myElement);
                if (module != null) {
                    List<PsiMethod> methods = SnowJavaUtils.findMethods(module, split[0], split[1]);
                    ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(methods)) {
                        for (PsiMethod method : methods) {
                            resolveResults.add(new PsiElementResolveResult(method));
                        }
                    }
                    return resolveResults.toArray(ResolveResult.EMPTY_ARRAY);
                }
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    /**
     * Called when the reference target element has been renamed, in order to change the reference
     * text according to the new name.
     *
     * @param newElementName the new name of the target element.
     * @return the new underlying element of the reference.
     * @throws IncorrectOperationException if the renaming cannot be handled for some reason.
     */
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        XmlAttribute parent = (XmlAttribute) myElement.getParent();
        String flowId = myElement.getValue();
        if (StringUtils.isNotBlank(flowId)) {
            String s = StringUtils.substringBefore(flowId, ".");
            parent.setValue(s + "." + newElementName);
        }
        return myElement;
    }


}
