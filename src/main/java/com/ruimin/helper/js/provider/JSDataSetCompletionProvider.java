package com.ruimin.helper.js.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.JspPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/24 下午 11:07
 * @description 自动补全flowId和datasource
 */
public class JSDataSetCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
        @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        if (element.getPrevSibling() != null) {
            return;
        }
        JspFile jspFile = JspPsiUtil.getJspFile(element);
        if (jspFile != null) {
            List<XmlTag> snowTags = SnowJspUtils.findAllSnowTag(jspFile);
            for (XmlTag xmlTag : snowTags) {
                LookupElement lookUpElement = getLookUpElement(xmlTag);
                if (lookUpElement != null) {
                    result.addElement(lookUpElement);
                }
            }
        }
    }

    private LookupElement getLookUpElement(XmlTag xmlTag) {
        String name = xmlTag.getName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        switch (name) {
            case JspConstants.DATASET_TAG_NAME: {
                XmlAttribute id = xmlTag.getAttribute(JspConstants.ATTR_NAME_ID);
                XmlAttribute path = xmlTag.getAttribute(JspConstants.ATTR_NAME_PATH);
                if (id != null) {
                    return new SnowLookUpElement(id.getValue() + JspConstants.DTST_EXPRESSION_SUFFIX, xmlTag,
                        path != null ? path.getValue() : null, "DataSet");
                }

                break;
            }
            case JspConstants.BUTTON_TAG_NAME: {
                XmlAttribute id = xmlTag.getAttribute(JspConstants.ATTR_NAME_ID);
                XmlAttribute datset = xmlTag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                XmlAttribute desc = xmlTag.getAttribute(JspConstants.ATTR_NAME_DESC);
                String tailName = "";
                if (datset != null) {
                    tailName += datset.getValue();
                }
                if (desc != null) {
                    if (StringUtils.isNotBlank(tailName)) {
                        tailName += " ";
                    }
                    tailName += desc.getValue();
                }

                if (id != null) {
                    return new SnowLookUpElement(id.getValue(), xmlTag, tailName, "Button");
                }
                break;
            }
            case JspConstants.QUERY_TAG_NAME: {
                XmlAttribute datset = xmlTag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                if (datset != null) {
                    return new SnowLookUpElement(datset.getValue() + JspConstants.QUERY_EXPRESSION_SUFFIX, xmlTag,
                        datset.getValue(), "QueryDataSet");
                }
                break;
            }
            case JspConstants.GRID_TAG_NAME: {
                XmlAttribute id = xmlTag.getAttribute(JspConstants.ATTR_NAME_ID);
                XmlAttribute datset = xmlTag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                if (id != null) {
                    return new SnowLookUpElement(id.getValue(), xmlTag, datset != null ? datset.getValue() : null,
                        "Grid");
                }
                break;
            }
            case JspConstants.WINDOW_TAG_NAME: {
                XmlAttribute id = xmlTag.getAttribute(JspConstants.ATTR_NAME_ID);
                XmlAttribute datset = xmlTag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                if (id != null) {
                    return new SnowLookUpElement(id.getValue() + JspConstants.WINDOW_EXPRESSION_SUFFIX, xmlTag,
                        datset != null ? datset.getValue() : null, "Window");
                }
                break;
            }
        }
        return null;
    }
}
