package com.ruimin.ifinsnowhelper.provider;

import com.google.common.collect.ImmutableSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.ifinsnowhelper.constants.SnowConstants;
import com.ruimin.ifinsnowhelper.dom.model.Command;
import com.ruimin.ifinsnowhelper.dom.model.Data;
import com.ruimin.ifinsnowhelper.dom.model.Define;
import com.ruimin.ifinsnowhelper.language.SnowIcons;
import com.ruimin.ifinsnowhelper.util.JavaUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken && isTargetType((XmlToken) element);
    }


    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull XmlToken from) {
        PsiElement parent = from.getParent();
        if (parent instanceof XmlTag) {
            XmlTag tag = (XmlTag) parent;
            String flowId = tag.getAttributeValue(SnowConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME);
            if (StringUtils.isNotBlank(flowId)){
                String[] split = flowId.split(SnowConstants.FLOWID_SEPARATE);
                if (split.length >= 2){
                    return JavaUtils.findMethods(from.getProject(), split[0],split[1]);
                }else {
                    return Optional.empty();
                }
            }else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getName() {
        return "调用方法标记";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SnowIcons.LOGO;
    }

    @NotNull
    @Override
    public String getTooltip(PsiElement array, @NotNull PsiElement target) {
        return "导航到方法";
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
                isTargetType = true;
            }
        }
        if (isTargetType == null){
            if (TARGET_TYPES.contains(token.getText())) {
                PsiElement parent = token.getParent();
                // 判断当前节点时标签
                if (parent instanceof XmlTag) {
                    PsiElement nextSibling = token.getNextSibling();
                    if (nextSibling instanceof PsiWhiteSpace) {
                        isTargetType = true;
                    }
                }
            }
        }
        if (isTargetType == null){
            isTargetType = false;
        }
        return isTargetType;
    }
}
