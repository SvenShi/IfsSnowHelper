package com.ruimin.helper.reference;

import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.JavaGlobalMemberLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.util.PsiClassUtil;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.ruimin.helper.constants.CommonConstants;
import com.ruimin.helper.util.JavaUtils;
import com.siyeh.ig.psiutils.MethodUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class DatasetReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public DatasetReference(@NotNull XmlAttributeValue element) {
        super(Objects.requireNonNull(element), new TextRange(1, element.getText().length() - 1));
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
        String flowId = myElement.getValue();
        if (StringUtils.isNotBlank(flowId)) {
            String[] split = flowId.split(CommonConstants.COLON_SEPARATE);
            if (split.length >= 2) {
                Module module = ModuleUtil.findModuleForPsiElement(myElement);
                if (module != null) {
                    List<PsiMethod> methods = JavaUtils.findMethods(module, split[0], split[1]);
                    ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(methods)) {
                        for (PsiMethod method : methods) {
                            resolveResults.add(new PsiElementResolveResult(method));
                        }
                    }
                    return resolveResults.toArray(new ResolveResult[0]);
                }
            }
        }
        return new ResolveResult[0];
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
            String s = StringUtils.substringBefore(flowId, ":");
            parent.setValue(s + ":" + newElementName);
        }
        return myElement;
    }

}
