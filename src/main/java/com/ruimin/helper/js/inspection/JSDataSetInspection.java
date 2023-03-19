package com.ruimin.helper.js.inspection;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.inspections.JSInspection;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Data;
import com.ruimin.helper.dtst.dom.model.Field;
import com.ruimin.helper.dtst.dom.model.Fields;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import com.ruimin.helper.js.constants.JSConstants;
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
                PsiFile file = node.getContainingFile();
                String text = node.getText();
                if (file instanceof XmlFile) {
                    if (StringUtils.endsWith(text, JspConstants.QUERY_EXPRESSION_SUFFIX)) {
                        VirtualFile virtualFile = file.getVirtualFile();
                        if (virtualFile != null) {
                            file = PsiManager.getInstance(node.getProject()).findFile(virtualFile);
                        }
                        if (file != null) {
                            List<XmlTag> dataSetTag = SnowJspUtils.findAllTagInFile((XmlFile) file,
                                JspConstants.QUERY_TAG_NAME);
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
                                problemsHolder.registerProblem(node, "未找到对应的dataset！",
                                    ProblemHighlightType.ERROR);
                            }
                        }
                    } else if (StringUtils.endsWith(text, JspConstants.DTST_EXPRESSION_SUFFIX)) {
                        VirtualFile virtualFile = file.getVirtualFile();
                        if (virtualFile != null) {
                            file = PsiManager.getInstance(node.getProject()).findFile(virtualFile);
                        }
                        if (file != null) {
                            List<XmlTag> queryTags = SnowJspUtils.findAllTagInFile((XmlFile) file,
                                JspConstants.DATASET_TAG_NAME);
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
                                problemsHolder.registerProblem(node, "未找到对应的dataset！",
                                    ProblemHighlightType.ERROR);
                            }
                        }
                    }
                }
            }

            /**
             * @param literal
             */
            @Override
            public void visitJSLiteralExpression(JSLiteralExpression literal) {
                PsiFile file = literal.getContainingFile();
                if (file instanceof XmlFile) {
                    String fieldName = StringUtils.removeQuot(literal.getText());
                    PsiElement jsArgumentList = literal.getParent();
                    if (jsArgumentList instanceof JSArgumentList) {
                        PsiElement jsReferenceExpression = jsArgumentList.getPrevSibling();
                        if (jsReferenceExpression instanceof JSReferenceExpression) {
                            PsiElement callMethod = jsReferenceExpression.getLastChild();
                            String text = callMethod.getText();
                            if (JSConstants.JS_METHODS.contains(text)) {
                                PsiElement datasetReference = jsReferenceExpression.getFirstChild();
                                if (datasetReference instanceof JSReferenceExpression) {
                                    PsiReference reference = datasetReference.getReference();
                                    if (reference != null) {
                                        PsiElement resolve = reference.resolve();
                                        if (resolve instanceof XmlAttributeValue) {
                                            XmlTag tag = SnowJspUtils.findTag((XmlElement) resolve);
                                            if (JspConstants.QUERY_TAG_NAME.equals(tag.getName())) {
                                                XmlAttribute attribute = tag.getAttribute(
                                                    JspConstants.ATTR_NAME_DATASET);
                                                if (attribute != null) {
                                                    XmlAttributeValue valueElement = attribute.getValueElement();
                                                    if (valueElement != null) {
                                                        reference = valueElement.getReference();
                                                        if (reference != null) {
                                                            resolve = reference.resolve();
                                                            if (resolve instanceof XmlAttributeValue) {
                                                                tag = SnowJspUtils.findTag((XmlElement) resolve);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (tag != null) {
                                                XmlAttribute attribute = tag.getAttribute(JspConstants.ATTR_NAME_PATH);
                                                boolean flag = true;
                                                if (attribute != null) {
                                                    XmlAttributeValue valueElement = attribute.getValueElement();
                                                    if (valueElement != null) {
                                                        reference = valueElement.getReference();
                                                        if (reference != null) {
                                                            resolve = reference.resolve();
                                                            if (resolve instanceof XmlFile) {
                                                                Data data = DataSetUtils.getDataTagByDtstFile(
                                                                    (PsiFile) resolve);
                                                                if (data != null) {
                                                                    for (Fields fields : data.getFieldses()) {
                                                                        for (Field field : fields.getFields()) {
                                                                            GenericAttributeValue<String> id = field.getId();
                                                                            if (fieldName.equals(id.getValue())) {
                                                                                if (id.getXmlAttributeValue() != null) {
                                                                                    flag = false;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }
                                                }
                                                if (flag) {
                                                    problemsHolder.registerProblem(literal, "未找到对应的 field！",
                                                        ProblemHighlightType.ERROR);
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
        };
    }

    @Override
    public @NotNull String getShortName() {
        return "SnowJSDataSetInspection";
    }
}
