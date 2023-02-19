package com.ruimin.helper.reference;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.EmptyResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.common.util.RqlxUtils;
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
public class RqlxToJavaReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public RqlxToJavaReference(@NotNull XmlAttributeValue element) {
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
        String id = myElement.getValue();
        String rqlxPath = RqlxUtils.getRqlxPathByFile(myElement.getContainingFile());
        Module module = ModuleUtil.findModuleForPsiElement(myElement);

        if (StringUtils.isNotBlank(rqlxPath) && module != null) {
            List<PsiElement> rqlReference = RqlxUtils.findRqlReference(rqlxPath, id, module);
            if (CollectionUtils.isNotEmpty(rqlReference)) {
                ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                for (PsiElement element : rqlReference) {
                    PsiElementResolveResult resolveResult = new PsiElementResolveResult(element);
                    resolveResults.add(resolveResult);
                }
                return resolveResults.toArray(new ResolveResult[0]);
            }
        }
        return new ResolveResult[]{EmptyResolveResult.INSTANCE};
    }

}
