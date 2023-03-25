package com.ruimin.helper.jsp.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
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
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Command;
import com.ruimin.helper.dtst.dom.model.Type;
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
public class JspButtonIdReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {

    private String buttonId;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspButtonIdReference(@NotNull XmlAttributeValue element, String buttonId) {
        super(Objects.requireNonNull(element),
            new TextRange(1, DataUtils.mustPositive(element.getTextLength() - 1, 1)));
        this.buttonId = buttonId;
    }

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JspButtonIdReference(@NotNull XmlAttributeValue element, String buttonId, TextRange range) {
        super(Objects.requireNonNull(element), range);
        this.buttonId = buttonId;
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
        Module module = ModuleUtil.findModuleForPsiElement(myElement);
        if (module != null) {
            XmlTag buttonTag = SnowJspUtils.findTag(myElement);
            String dataSetId = buttonTag.getAttributeValue(JspConstants.ATTR_NAME_DATASET);
            if (StringUtils.isNotBlank(dataSetId)) {
                JspFile jspFile = JspPsiUtil.getJspFile(myElement);
                if (jspFile != null) {
                    XmlTag dataSetTag = SnowJspUtils.findDataSetTag(jspFile, dataSetId);
                    if (dataSetTag != null) {
                        XmlFile dataSetFile = SnowJspUtils.findDataSetFile(dataSetTag);
                        List<Command> commands = DataSetUtils.getCommands(dataSetFile);
                        ArrayList<LookupElement> result = new ArrayList<>();
                        for (Command command : commands) {
                            GenericAttributeValue<String> id = command.getId();
                            XmlAttribute xmlAttribute = id.getXmlAttribute();
                            GenericAttributeValue<Type> typeValue = command.getType();
                            Type type = typeValue.getValue();
                            GenericAttributeValue<String> desc = command.getDesc();
                            if (xmlAttribute != null) {
                                result.add(new SnowLookUpElement(xmlAttribute.getValue(), xmlAttribute, desc.getValue(),
                                    type != null ? type.getValue() : Type.NONE.getValue()));
                            }
                        }
                        return result.toArray();
                    }
                }
            }
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
        Module module = ModuleUtil.findModuleForPsiElement(myElement);
        if (module != null) {
            XmlTag buttonTag = SnowJspUtils.findTag(myElement);
            String dataSetId = buttonTag.getAttributeValue(JspConstants.ATTR_NAME_DATASET);
            if (StringUtils.isNotBlank(dataSetId) && StringUtils.isNotBlank(buttonId)) {
                JspFile jspFile = JspPsiUtil.getJspFile(myElement);
                if (jspFile != null) {
                    XmlTag dataSetTag = SnowJspUtils.findDataSetTag(jspFile, dataSetId);
                    if (dataSetTag != null) {
                        XmlFile dataSetFile = SnowJspUtils.findDataSetFile(dataSetTag);
                        List<Command> commands = DataSetUtils.getCommands(dataSetFile);
                        ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                        for (Command command : commands) {
                            GenericAttributeValue<String> commandId = command.getId();
                            if (buttonId.equals(commandId.getValue())) {
                                XmlAttributeValue xmlAttribute = commandId.getXmlAttributeValue();
                                if (xmlAttribute != null) {
                                    resolveResults.add(new PsiElementResolveResult(xmlAttribute));
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

    /**
     * @param newElementName the new name of the target element.
     * @return
     * @throws IncorrectOperationException
     */
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String value = myElement.getValue();
        PsiElement psiElement = super.handleElementRename(value.replace(buttonId, newElementName));
        this.buttonId = newElementName;
        return psiElement;
    }
}
