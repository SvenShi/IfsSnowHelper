package com.ruimin.helper.provider.line;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.common.constants.DtstConstants;
import com.ruimin.helper.common.constants.SnowIcons;
import com.ruimin.helper.common.constants.SnowPageConstants;
import com.ruimin.helper.common.enums.DtstToWhere;
import com.ruimin.helper.common.util.DtstUtils;
import com.ruimin.helper.common.util.SnowPageUtils;
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
            if (toWhere == DtstToWhere.JSP) {
                return goToJsp(from);
            }
            return goToDtst(from);
        }
        return null;
    }

    @Override
    @SuppressWarnings("DialogTitleCapitalization")
    public String getName() {
        if (toWhere != null) {
            if (toWhere == DtstToWhere.JSP) {
                return "前往jsp标志";
            }
            return "前往dtst标志";
        }
        return "";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        if (toWhere != null) {
            if (toWhere == DtstToWhere.JSP) {
                return SnowIcons.GO_JSP;
            }
            return SnowIcons.GO_DTST;
        }
        return SnowIcons.GO_BLACK;
    }

    @NotNull
    @Override
    public String getTooltip() {
        if (toWhere != null) {
            if (toWhere == DtstToWhere.JSP) {
                return "前往jsp";
            }
            return "前往dtst";
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
        if (StringUtils.isNotBlank(path)) {
            Module module = ModuleUtil.findModuleForPsiElement(xmlToken);
            if (module != null) {
                List<XmlTag> allDtstTag = SnowPageUtils.getAllDtstTag(module);
                if (CollectionUtils.isNotEmpty(allDtstTag)) {
                    ArrayList<PsiElement> psiElements = new ArrayList<>();
                    for (XmlTag dtstTag : allDtstTag) {
                        String attributeValue = dtstTag.getAttributeValue(SnowPageConstants.ATTR_NAME_PATH);
                        if (path.equals(attributeValue)) {
                            psiElements.add(dtstTag);
                        }
                    }
                    return psiElements;
                }
            }
        }
        return null;
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
                        Module module = ModuleUtil.findModuleForPsiElement(tag);
                        if (module != null){
                            ArrayList<XmlFile> dtst = DtstUtils.findDtstFileByPath(dtstPath, module.getModuleScope());
                            if (CollectionUtils.isNotEmpty(dtst)) {
                                return dtst;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}

