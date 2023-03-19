package com.ruimin.helper.js.reference;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Field;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JavaScriptFieldReference extends PsiReferenceBase<JSLiteralExpression> implements
    PsiPolyVariantReference {

    private final XmlFile dataSetFile;

    private final String fieldName;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JavaScriptFieldReference(@NotNull JSLiteralExpression element, XmlFile dataSetFile) {
        super(Objects.requireNonNull(element),
            new TextRange(1, DataUtils.mustPositive(element.getTextLength() - 1, 1)));
        this.dataSetFile = dataSetFile;
        this.fieldName = StringUtils.removeQuot(element.getText());
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
        Field field = DataSetUtils.findField(dataSetFile, fieldName);
        if (field != null) {
            XmlElement xmlElement = field.getXmlElement();
            if (xmlElement != null){
                return new ResolveResult[]{new PsiElementResolveResult(xmlElement)};
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }


}
