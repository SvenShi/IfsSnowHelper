package com.ruimin.helper.common.reference;

import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.java.utils.SnowJavaUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class FlowIdReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {


    private final String flowId;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public FlowIdReference(@NotNull XmlAttributeValue element, @NotNull String flowId, TextRange range) {
        super(element, range);
        this.flowId = flowId;
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
        if (StringUtils.isNotBlank(flowId)) {
            Module module = ModuleUtil.findModuleForPsiElement(myElement);
            if (module == null) {
                return super.getVariants();
            }
            String beforeFlowId = StringUtils.substringBeforeLast(flowId, CompletionUtil.DUMMY_IDENTIFIER);
            ArrayList<LookupElement> result = new ArrayList<>();
            if (beforeFlowId.contains(":")) {
                String[] split = beforeFlowId.split(":");
                if (split.length <= 2) {
                    String className = split[0];
                    List<PsiMethod> methods = SnowJavaUtils.findMethods(module.getModuleScope(), className, null);
                    for (PsiMethod method : methods) {
                        result.add(new SnowLookUpElement(className + ":" + method.getName(), method));
                    }
                }
            } else if (beforeFlowId.contains(".")) {
                String packageName = StringUtils.substringBeforeLast(beforeFlowId, ".");
                Optional<PsiPackage> aPackage = SnowJavaUtils.findPackage(module.getProject(), packageName);
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
        if (StringUtils.isNotBlank(flowId)) {
            String[] split = flowId.split(CommonConstants.COLON_SEPARATE);
            if (split.length >= 2) {
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
        if (StringUtils.isNotBlank(flowId)) {
            String s = StringUtils.substringBefore(flowId, ":");
            parent.setValue(s + ":" + newElementName);
        }
        return myElement;
    }


}
