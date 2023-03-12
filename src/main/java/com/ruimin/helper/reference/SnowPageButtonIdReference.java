package com.ruimin.helper.reference;

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
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.constants.SnowPageConstants;
import com.ruimin.helper.common.util.DtstUtils;
import com.ruimin.helper.common.util.SnowPageUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dom.dtst.model.Command;
import com.ruimin.helper.dom.dtst.model.Commands;
import com.ruimin.helper.dom.dtst.model.Data;
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
public class SnowPageButtonIdReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {



    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public SnowPageButtonIdReference(@NotNull XmlAttributeValue element) {
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
            String buttonId = myElement.getValue();
            XmlTag buttonTag = SnowPageUtils.findTag(myElement);
            String dataSetId = buttonTag.getAttributeValue(SnowPageConstants.BUTTON_ATTR_NAME_DATASET);
            if (StringUtils.isNotBlank(dataSetId) && StringUtils.isNotBlank(buttonId)) {
                DomManager domManager = DomManager.getDomManager(myElement.getProject());
                List<XmlTag> dataSetList = SnowPageUtils.getDataSetTag(
                    ((JspFile) myElement.getContainingFile()));
                ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                for (XmlTag dataSetTag : dataSetList) {
                    XmlAttribute dataSetIdAttr = dataSetTag.getAttribute(SnowPageConstants.DTST_ATTR_NAME_ID);
                    if (dataSetIdAttr != null) {
                        String id = dataSetIdAttr.getValue();
                        if (dataSetId.equals(id)) {
                            String dataSetPath = dataSetTag.getAttributeValue(
                                SnowPageConstants.DTST_ATTR_NAME_PATH);
                            if (StringUtils.isNotBlank(dataSetPath)) {
                                ArrayList<XmlFile> dtstFiles = DtstUtils.findDtstFileByPath(dataSetPath,
                                    module.getModuleScope());
                                for (XmlFile dtstFile : dtstFiles) {
                                    DomFileElement<Data> dtstRootElement = domManager.getFileElement(dtstFile,
                                        Data.class);
                                    if (dtstRootElement != null) {
                                        Data data = dtstRootElement.getRootElement();
                                        List<Commands> commandses = data.getCommandses();
                                        for (Commands commands : commandses) {
                                            for (Command command : commands.getCommands()) {
                                                GenericAttributeValue<String> commandId = command.getId();
                                                if (buttonId.equals(commandId.getValue())) {
                                                    XmlAttributeValue xmlAttribute = commandId.getXmlAttributeValue();
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
                return resolveResults.toArray(new ResolveResult[0]);
            }
        }

        return new ResolveResult[0];
    }


}
