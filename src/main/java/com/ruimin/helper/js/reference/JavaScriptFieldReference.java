package com.ruimin.helper.js.reference;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Data;
import com.ruimin.helper.dtst.dom.model.Field;
import com.ruimin.helper.dtst.dom.model.Fields;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import java.util.ArrayList;
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

    private final XmlTag dataSetTag;

    private final String fieldName;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JavaScriptFieldReference(@NotNull JSLiteralExpression element, XmlTag dataSetTag) {
        super(Objects.requireNonNull(element),
            new TextRange(1, DataUtils.mustPositive(element.getTextLength() - 1, 1)));
        this.dataSetTag = dataSetTag;
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
        XmlAttribute attribute = dataSetTag.getAttribute(JspConstants.ATTR_NAME_PATH);
        if (attribute != null) {
            XmlAttributeValue valueElement = attribute.getValueElement();
            if (valueElement != null) {
                PsiReference reference = valueElement.getReference();
                if (reference != null) {
                    PsiElement resolve = reference.resolve();
                    if (resolve instanceof XmlFile) {
                        Data data = DataSetUtils.getDataTagByDtstFile((PsiFile) resolve);
                        ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                        if (data != null) {
                            for (Fields fields : data.getFieldses()) {
                                for (Field field : fields.getFields()) {
                                    GenericAttributeValue<String> id = field.getId();
                                    if (fieldName.equals(id.getValue())) {
                                        if (id.getXmlAttributeValue() != null) {
                                            resolveResults.add(new PsiElementResolveResult(id.getXmlAttributeValue()));
                                        }
                                    }
                                }
                            }
                        }
                        return resolveResults.toArray(ResolveResult.EMPTY_ARRAY);
                    }
                }
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }


}
