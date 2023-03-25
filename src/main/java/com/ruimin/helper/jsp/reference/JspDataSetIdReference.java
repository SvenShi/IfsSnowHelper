package com.ruimin.helper.jsp.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.JspPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JspDataSetIdReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspDataSetIdReference(@NotNull XmlAttributeValue element) {
        super(Objects.requireNonNull(element),
            new TextRange(1, DataUtils.mustPositive(element.getTextLength() - 1, 1)));
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
        JspFile jspFile = JspPsiUtil.getJspFile(myElement);
        if (jspFile != null) {
            List<XmlTag> dataSetList = SnowJspUtils.findAllTagInFile(jspFile, JspConstants.DATASET_TAG_NAME);
            ArrayList<LookupElement> result = new ArrayList<>();
            for (XmlTag datasetTag : dataSetList) {
                XmlAttribute attribute = datasetTag.getAttribute(JspConstants.ATTR_NAME_ID);
                if (attribute != null) {
                    String id = attribute.getValue();
                    XmlAttribute path = datasetTag.getAttribute(JspConstants.ATTR_NAME_PATH);
                    result.add(new SnowLookUpElement(id, datasetTag, path != null ? path.getValue() : null, "DataSet"));
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
        String dataSetId = myElement.getValue();
        JspFile jspFile = JspPsiUtil.getJspFile(myElement);
        if (StringUtils.isNotBlank(dataSetId) && jspFile != null) {
            ArrayList<ResolveResult> resolveResults = new ArrayList<>();
            XmlTag dataSetTag = SnowJspUtils.findDataSetTag(jspFile, dataSetId);
            if (dataSetTag != null){
                XmlAttribute attribute = dataSetTag.getAttribute(JspConstants.ATTR_NAME_ID);
                if (attribute != null) {
                    String id = attribute.getValue();
                    XmlAttributeValue valueElement = attribute.getValueElement();
                    if (dataSetId.equals(id) && valueElement != null) {
                        resolveResults.add(new PsiElementResolveResult(valueElement));
                    }
                }
            }
            return resolveResults.toArray(ResolveResult.EMPTY_ARRAY);
        }
        return ResolveResult.EMPTY_ARRAY;
    }


}
