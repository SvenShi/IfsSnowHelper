package com.ruimin.helper.js.inspection;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.inspections.JSInspection;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.JspPsiUtil;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Field;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import com.ruimin.helper.js.utils.SnowJSUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

public class JSDataSetInspection extends JSInspection {


    @Override
    protected @NotNull PsiElementVisitor createVisitor(@NotNull ProblemsHolder problemsHolder,
        @NotNull LocalInspectionToolSession localInspectionToolSession) {
        return new JSElementVisitor() {
            @Override
            public void visitJSReferenceExpression(JSReferenceExpression node) {
                JspFile file = JspPsiUtil.getJspFile(node);
                String text = node.getText();
                if (StringUtils.endsWith(text, JspConstants.QUERY_EXPRESSION_SUFFIX)) {
                    if (file != null) {
                        List<XmlTag> dataSetTag = SnowJspUtils.findAllTagInFile(file, JspConstants.QUERY_TAG_NAME);
                        boolean flag = true;
                        for (XmlTag xmlTag : dataSetTag) {
                            XmlAttribute attribute = xmlTag.getAttribute(JspConstants.ATTR_NAME_DATASET);
                            if (attribute != null) {
                                XmlAttributeValue valueElement = attribute.getValueElement();
                                if (valueElement != null) {
                                    String value = valueElement.getValue();
                                    if (StringUtils.isNotBlank(value)) {
                                        String dataSetExpression = value + JspConstants.QUERY_EXPRESSION_SUFFIX;
                                        if (dataSetExpression.equals(text)) {
                                            flag = false;
                                        }
                                    }
                                }
                            }
                        }
                        if (CollectionUtils.isEmpty(dataSetTag) || flag) {
                            problemsHolder.registerProblem(node, "未找到对应的dataset！", ProblemHighlightType.ERROR);
                        }
                    }
                } else if (StringUtils.endsWith(text, JspConstants.DTST_EXPRESSION_SUFFIX)) {
                    if (file != null) {
                        List<XmlTag> queryTags = SnowJspUtils.findAllTagInFile(file, JspConstants.DATASET_TAG_NAME);
                        boolean flag = true;
                        for (XmlTag xmlTag : queryTags) {
                            XmlAttribute attribute = xmlTag.getAttribute(JspConstants.ATTR_NAME_ID);
                            if (attribute != null) {
                                XmlAttributeValue valueElement = attribute.getValueElement();
                                if (valueElement != null) {
                                    String value = valueElement.getValue();
                                    if (StringUtils.isNotBlank(value)) {
                                        String dataSetExpression = value + JspConstants.DTST_EXPRESSION_SUFFIX;
                                        if (dataSetExpression.equals(text)) {
                                            flag = false;
                                        }
                                    }
                                }
                            }
                        }
                        if (CollectionUtils.isEmpty(queryTags) || flag) {
                            problemsHolder.registerProblem(node, "未找到对应的dataset！", ProblemHighlightType.ERROR);
                        }
                    }
                }
            }

            /**
             * @param literal
             */
            @Override
            public void visitJSLiteralExpression(JSLiteralExpression literal) {
                JSReferenceExpression methodCaller = SnowJSUtils.getDataSetMethodCaller(literal);
                if (methodCaller != null) {
                    XmlFile xmlFile = SnowJSUtils.getDataSetTagByReference(methodCaller.getReference());
                    if (xmlFile != null) {
                        String fieldName = StringUtils.removeQuot(literal.getText());
                        Field field = DataSetUtils.findField(xmlFile, fieldName);
                        if (field == null) {
                            problemsHolder.registerProblem(literal, "未找到对应的 field！", ProblemHighlightType.ERROR);
                        }
                    }

                }
            }
        };
    }

    @Override
    public @NotNull String getShortName() {
        return "SnowJSDataSetInspection";
    }
}
