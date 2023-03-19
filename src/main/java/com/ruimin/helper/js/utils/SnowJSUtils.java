package com.ruimin.helper.js.utils;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFunction;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSParameterList;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSVariable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.dom.model.Field;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import com.ruimin.helper.js.constants.JSConstants;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.enums.JspTagEnum;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import org.jetbrains.annotations.NotNull;

public class SnowJSUtils {


    /**
     * 根据方法中的参数获取方法调用者
     *
     * @param literal 元素
     * @return {@link JSReferenceExpression}
     */
    public static JSReferenceExpression getDataSetMethodCaller(@NotNull PsiElement literal) {
        PsiFile file = literal.getContainingFile();
        if (file instanceof XmlFile) {
            PsiElement jsArgumentList = literal.getParent();
            if (jsArgumentList instanceof JSArgumentList) {
                if (isFirstArgument((JSArgumentList) jsArgumentList, literal)) {
                    PsiElement jsReferenceExpression = jsArgumentList.getPrevSibling();
                    if (jsReferenceExpression instanceof JSReferenceExpression) {
                        PsiElement callMethod = jsReferenceExpression.getLastChild();
                        String text = callMethod.getText();
                        if (JSConstants.JS_METHODS.contains(text)) {
                            PsiElement datasetReference = jsReferenceExpression.getFirstChild();
                            return datasetReference instanceof JSReferenceExpression
                                ? (JSReferenceExpression) datasetReference : null;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据dataset的变量获取调用的dataset的xmlTag
     *
     * @param reference 引用
     * @return {@link XmlTag}
     */
    public static XmlFile getDataSetTagByReference(PsiReference reference) {
        if (reference != null) {
            PsiElement resolve = reference.resolve();
            if (resolve instanceof XmlAttributeValue) {
                XmlTag tag = SnowJspUtils.findTag((XmlElement) resolve);
                if (JspConstants.QUERY_TAG_NAME.equals(tag.getName())) {
                    XmlAttribute attribute = tag.getAttribute(JspConstants.ATTR_NAME_DATASET);
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
                return SnowJspUtils.getDtstFileByTag(tag);
            } else if (resolve instanceof JSParameter) {
                //     事件传参
                JSFunction function = getFunction((JSParameter) resolve);
                if (function != null) {
                    String name = function.getName();
                    if (StringUtils.isNotBlank(name)) {
                        String[] s = name.split("_");
                        if (s.length == 2) {
                            String eventName = s[s.length - 1];
                            if ("beforeCheck".equals(eventName) || "beforeUnCheck".equals(eventName)
                                || "afterCheck".equals(eventName) || "afterUnCheck".equals(eventName)
                                || "beforeExpand".equals(eventName) || "afterExpand".equals(eventName)
                                || "beforeCollapse".equals(eventName) || "afterCollapse".equals(eventName)
                                || "onDblClick".equals(eventName)) {
                                XmlTag tag = JspTagEnum.GRID.findDatasetTagById(resolve, s[0]);
                                return SnowJspUtils.getDtstFileByTag(tag);

                            }
                        } else if (s.length == 3) {
                            String eventName = s[s.length - 1];

                            if ("afterScroll".equals(eventName) || "beforeChange".equals(eventName)
                                || "afterChange".equals(eventName)) {
                                XmlTag tag = SnowJspUtils.findTagById(resolve, s[0], JspTagEnum.DATASET);
                                return SnowJspUtils.getDtstFileByTag(tag);
                            } else if ("onContextmenu".equals(eventName)) {
                                //     tree_xxx(treeId)_onContextmenu
                                XmlTag tag = JspTagEnum.TREE.findDatasetTagById(resolve, s[1]);
                                return SnowJspUtils.getDtstFileByTag(tag);
                            } else if ("onRefresh".equals(eventName)) {
                                //     gridId_fieldId_onRefresh
                                XmlTag tag = JspTagEnum.GRID.findDatasetTagById(resolve, s[0]);
                                return SnowJspUtils.getDtstFileByTag(tag);
                            }
                        } else if (s.length == 4) {
                            String eventName = s[s.length - 1];
                            if ("beforeOpen".equals(eventName) || "onSelect".equals(eventName)) {
                                //     datasetId_dataset_fieldId_beforeOpen
                                XmlTag datasetTagById = JspTagEnum.DATASET.findDatasetTagById(resolve, s[0]);
                                XmlFile xmlFile = SnowJspUtils.getDtstFileByTag(datasetTagById);
                                if (xmlFile != null) {
                                    Field field = DataSetUtils.findField(xmlFile, s[2]);
                                    if (field != null) {
                                        XmlAttributeValue xmlAttributeValue = field.getDatasource()
                                            .getXmlAttributeValue();
                                        if (xmlAttributeValue != null) {
                                            PsiReference reference1 = xmlAttributeValue.getReference();
                                            if (reference1 != null) {
                                                PsiElement resolve1 = reference1.resolve();
                                                if (resolve1 instanceof XmlFile) {
                                                    return (XmlFile) resolve1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (resolve instanceof JSVariable) {
                // firstUnit
                @NotNull PsiElement[] children = resolve.getChildren();
                if (children.length >= 1) {
                    PsiElement child = children[0];
                    PsiElement anMethodCaller = null;
                    if (child instanceof JSCallExpression) {
                        PsiElement firstChild = child.getFirstChild();
                        if (firstChild != null) {
                            anMethodCaller = firstChild.getFirstChild();
                        }
                    } else if (child instanceof JSReferenceExpression) {
                        anMethodCaller = child.getFirstChild();
                    }

                    if (anMethodCaller instanceof JSReferenceExpression && anMethodCaller.getText()
                        .contains(JspConstants.DTST_EXPRESSION_SUFFIX)) {
                        return getDataSetTagByReference(anMethodCaller.getReference());
                    }
                }
            }
        }
        return null;
    }


    public static boolean isFirstArgument(@NotNull JSArgumentList argumentList, PsiElement element) {
        PsiElement[] children = argumentList.getChildren();
        if (children.length >= 2) {
            return children[1].equals(element);
        }
        return false;
    }


    public static JSFunction getFunction(@NotNull JSParameter jsParameter) {
        PsiElement parent = jsParameter.getParent();
        if (parent instanceof JSParameterList) {
            PsiElement parent1 = parent.getParent();
            if (parent1 instanceof JSFunction) {
                return (JSFunction) parent1;
            }
        }
        return null;
    }


}
