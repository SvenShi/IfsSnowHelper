package com.ruimin.helper.provider.line;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.helper.common.enums.DtstToWhere;
import com.ruimin.helper.constants.CommonConstants;
import com.ruimin.helper.constants.DtstConstants;
import com.ruimin.helper.constants.SnowIcons;
import com.ruimin.helper.constants.SnowPageConstants;
import com.ruimin.helper.util.DtstUtils;
import com.ruimin.helper.util.JavaUtils;
import com.ruimin.helper.util.SnowPageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/22 下午 09:40
 * @description dataset文件跳转java
 */
public class SnowDataSetLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken> {

    /**
     * 导航到哪里
     */
    private DtstToWhere toWhere = null;

    private static final Set<String> NOT_IN_DATASOURCE_TAG = Sets.newHashSet("LIST", "DDIC");


    @Override
    public boolean isTheElement(@NotNull XmlToken element) {
        return isTargetType(element);
    }


    @Override
    public List<? extends PsiElement> apply(@NotNull XmlToken from) {
        if (toWhere != null) {
            switch (toWhere) {
                case JSP:
                    return goToJsp(from);
                case JAVA:
                    return goToJava(from);
                default:
                    return goToDtst(from);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("DialogTitleCapitalization")
    public String getName() {
        if (toWhere != null) {
            switch (toWhere) {
                case JSP:
                    return "前往jsp标志";
                case JAVA:
                    return "前往Java标志";
                default:
                    return "前往dtst标志";
            }
        }
        return "";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        if (toWhere != null) {
            switch (toWhere) {
                case JSP:
                    return SnowIcons.GO_JSP;
                case JAVA:
                    return SnowIcons.GO_JAVA;
                default:
                    return SnowIcons.GO_DTST;
            }
        }
        return SnowIcons.GO_BLACK;
    }

    @NotNull
    @Override
    public String getTooltip() {
        if (toWhere != null) {
            switch (toWhere) {
                case JSP:
                    return "前往jsp";
                case JAVA:
                    return "前往java方法";
                default:
                    return "前往dtst";
            }
        }
        return "";
    }

    /**
     * 是否是目标标签
     */
    private boolean isTargetType(XmlToken token) {
        DtstToWhere where = DtstToWhere.getToWhere(token.getText());
        if (where != null) {
            PsiElement nextSibling = token.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                boolean equals = "<".equals(token.getPrevSibling().getText());
                if (equals) {
                    toWhere = where;
                }
                return equals;
            }
        }
        return false;
    }

    private List<? extends PsiElement> goToJsp(XmlToken xmlToken) {
        String path = SnowPageUtils.getDtstPath(xmlToken.getContainingFile());
        if (path == null) {
            return null;
        }
        List<XmlTag> allDtstTag = SnowPageUtils.findAllDtstTag(xmlToken.getProject());
        ArrayList<PsiElement> psiElements = new ArrayList<>();

        for (XmlTag dtstTag : allDtstTag) {
            String attributeValue = dtstTag.getAttributeValue(SnowPageConstants.DTST_ATTR_NAME_PATH);
            if (path.equals(attributeValue)) {
                psiElements.add(dtstTag);
            }
        }
        if (psiElements.isEmpty()) {
            return null;
        }

        return psiElements;
    }

    private List<? extends PsiElement> goToJava(XmlToken from) {
        PsiElement parent = from.getParent();
        if (parent instanceof XmlTag) {
            XmlTag tag = (XmlTag) parent;
            String flowId = tag.getAttributeValue(DtstConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME);
            if (StringUtils.isNotBlank(flowId)) {
                String[] split = flowId.split(CommonConstants.COLON_SEPARATE);
                if (split.length >= 2) {
                    return JavaUtils.findMethods(from.getProject(), split[0], split[1]);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<? extends PsiElement> goToDtst(XmlToken from) {
        PsiElement parent = from.getParent();
        if (parent instanceof XmlTag) {
            XmlTag tag = (XmlTag) parent;
            String datasource = tag.getAttributeValue(DtstConstants.XML_TAG_DATASOURCE_ATTRIBUTE_NAME);
            if (StringUtils.isNotBlank(datasource)) {
                String[] split = datasource.split(CommonConstants.COLON_SEPARATE);
                if (split.length >= 2) {
                    if (!NOT_IN_DATASOURCE_TAG.contains(split[0])) {
                        String dtstPath = split[1];
                        ArrayList<XmlFile> dtst = DtstUtils.findDtstFileByPath(dtstPath, tag.getProject());
                        if (CollectionUtils.isNotEmpty(dtst)) {
                            return dtst;
                        } else {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return null;
    }
}

