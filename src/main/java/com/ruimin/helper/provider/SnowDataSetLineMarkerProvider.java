package com.ruimin.helper.provider;

import com.google.common.collect.ImmutableSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.helper.constants.DtstConstants;
import com.ruimin.helper.constants.SnowPageConstants;
import com.ruimin.helper.dom.model.Command;
import com.ruimin.helper.dom.model.Data;
import com.ruimin.helper.dom.model.Define;
import com.ruimin.helper.language.SnowIcons;
import com.ruimin.helper.util.JavaUtils;
import com.ruimin.helper.util.SnowPageUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/22 下午 09:40
 * @description dataset文件跳转java
 */
public class SnowDataSetLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken, PsiElement> {

    private static final String DATA_CLASS = Data.class.getSimpleName();

    // 目标标签
    private static final ImmutableSet<String> TARGET_TYPES = ImmutableSet.of(Define.class.getSimpleName(),
                                                                             Command.class.getSimpleName());

    // 是否导航到java 反之导航到jsp
    private boolean toJava = true;

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken && isTargetType((XmlToken) element);
    }


    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull XmlToken from) {
        if (toJava) {
            return goToJava(from);
        } else {
            return goToJsp(from);
        }
    }

    @Override
    public String getName() {
        return toJava ? "调用方法标记" : "被引用的dtst";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return toJava ? SnowIcons.GO_BLUE : SnowIcons.GO_YELLOW;
    }

    @NotNull
    @Override
    public String getTooltip(PsiElement array, @NotNull PsiElement target) {
        return toJava ? "前往java方法" : "前往jsp";
    }

    /**
     * 是否时目标标签
     */
    private boolean isTargetType(XmlToken token) {
        Boolean isTargetType = null;
        if (DATA_CLASS.equals(token.getText())) {
            // 判断当前元素是开始节点
            PsiElement nextSibling = token.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                toJava = false;
                isTargetType = true;
            }
        }
        if (isTargetType == null) {
            if (TARGET_TYPES.contains(token.getText())) {
                PsiElement parent = token.getParent();
                // 判断当前节点时标签
                if (parent instanceof XmlTag) {
                    PsiElement nextSibling = token.getNextSibling();
                    if (nextSibling instanceof PsiWhiteSpace) {
                        isTargetType = true;
                        toJava = true;
                    }
                }
            }
        }
        if (isTargetType == null) {
            isTargetType = false;
        }
        return isTargetType;
    }

    private Optional<? extends PsiElement[]> goToJsp(XmlToken xmlToken) {
        String path = SnowPageUtils.getDtstPath(xmlToken.getContainingFile());
        if (path == null) {
            return Optional.empty();
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
            return Optional.empty();
        }

        return Optional.of(psiElements.toArray(new PsiElement[0]));
    }

    private Optional<? extends PsiElement[]> goToJava(XmlToken from) {
        PsiElement parent = from.getParent();
        if (parent instanceof XmlTag) {
            XmlTag tag = (XmlTag) parent;
            String flowId = tag.getAttributeValue(DtstConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME);
            if (StringUtils.isNotBlank(flowId)) {
                String[] split = flowId.split(DtstConstants.FLOWID_SEPARATE);
                if (split.length >= 2) {
                    return JavaUtils.findMethods(from.getProject(), split[0], split[1]);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
