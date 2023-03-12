package com.ruimin.helper.jsp.reference;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JspDataSetPathReference extends PsiReferenceBase<XmlAttributeValue> implements
    PsiPolyVariantReference {


    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspDataSetPathReference(@NotNull XmlAttributeValue element) {
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
        Module module = ModuleUtil.findModuleForPsiElement(myElement);
        if (module != null) {
            String path = myElement.getValue();
            List<XmlFile> dtst = DataSetUtils.findDtstFileByPath(path, module.getModuleScope());
            ArrayList<ResolveResult> resolveResults = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(dtst)) {
                for (XmlFile xmlFile : dtst) {
                    resolveResults.add(new PsiElementResolveResult(xmlFile));
                }
                return resolveResults.toArray(new ResolveResult[0]);
            }
        }
        return new ResolveResult[0];
    }


}