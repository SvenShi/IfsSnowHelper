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
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Data;
import com.ruimin.helper.dtst.dom.model.Field;
import com.ruimin.helper.dtst.dom.model.Fields;
import com.ruimin.helper.dtst.utils.DataSetUtils;
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
public class JspTagFieldIdReference extends PsiReferenceBase<XmlAttributeValue> implements
    PsiPolyVariantReference {

    private String fieldId;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspTagFieldIdReference(@NotNull XmlAttributeValue element, String fieldId, int startIndex,
        int endIndex) {
        super(Objects.requireNonNull(element), new TextRange(Math.max(startIndex, 0), endIndex));
        this.fieldId = fieldId;
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
            XmlTag buttonTag = SnowJspUtils.findTag(myElement);
            String dataSetId = buttonTag.getAttributeValue(JspConstants.ATTR_NAME_DATASET);
            if (StringUtils.isNotBlank(dataSetId) && StringUtils.isNotBlank(fieldId)) {
                DomManager domManager = DomManager.getDomManager(myElement.getProject());
                List<XmlTag> dataSetList = SnowJspUtils.findAllTagInFile(
                    ((JspFile) myElement.getContainingFile()), JspConstants.DATASET_TAG_NAME);
                ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                for (XmlTag dataSetTag : dataSetList) {
                    XmlAttribute dataSetIdAttr = dataSetTag.getAttribute(JspConstants.ATTR_NAME_ID);
                    if (dataSetIdAttr != null) {
                        String id = dataSetIdAttr.getValue();
                        if (dataSetId.equals(id)) {
                            String dataSetPath = dataSetTag.getAttributeValue(
                                JspConstants.ATTR_NAME_PATH);
                            if (StringUtils.isNotBlank(dataSetPath)) {
                                ArrayList<XmlFile> dtstFiles = DataSetUtils.findDtstFileByPath(dataSetPath,
                                    module.getModuleScope());
                                for (XmlFile dtstFile : dtstFiles) {
                                    DomFileElement<Data> dtstRootElement = domManager.getFileElement(dtstFile,
                                        Data.class);
                                    if (dtstRootElement != null) {
                                        Data data = dtstRootElement.getRootElement();
                                        List<Fields> fieldses = data.getFieldses();
                                        for (Fields fields : fieldses) {
                                            for (Field field : fields.getFields()) {
                                                GenericAttributeValue<String> filedId = field.getId();
                                                if (fieldId.equals(filedId.getValue())) {
                                                    XmlAttributeValue xmlAttribute = filedId.getXmlAttributeValue();
                                                    if (xmlAttribute != null) {
                                                        resolveResults.add(
                                                            new PsiElementResolveResult(xmlAttribute));
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return resolveResults.toArray(ResolveResult.EMPTY_ARRAY);
            }
        }

        return ResolveResult.EMPTY_ARRAY;
    }

    /**
     * @param newElementName the new name of the target element.
     * @return
     * @throws IncorrectOperationException
     */
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String value = myElement.getValue();
        XmlAttribute parent = (XmlAttribute) myElement.getParent();
        parent.setValue(value.replace(fieldId, newElementName));
        this.fieldId = newElementName;
        return myElement;
    }
}
