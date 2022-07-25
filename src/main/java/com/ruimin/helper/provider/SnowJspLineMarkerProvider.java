package com.ruimin.helper.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.helper.constants.SnowPageConstants;
import com.ruimin.helper.constants.SnowIcons;
import com.ruimin.helper.util.SnowPageUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 下午 07:24
 * @description jsp 跳转 jsp
 */
public class SnowJspLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken, PsiElement> {

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken && isTargetType((XmlToken) element);
    }

    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull XmlToken from) {
        List<PsiFile> xmlTag = SnowPageUtils.findFileInDatasetTag((XmlTag) from.getParent());
        if (xmlTag.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(xmlTag.toArray(new PsiElement[0]));
    }

    @Override
    public @NotNull Icon getIcon() {
        return SnowIcons.GO_GREEN;
    }

    @Override
    public @NotNull String getTooltip(PsiElement array, @NotNull PsiElement target) {
        return "前往dtst";
    }


    /**
     * 是否为目标行
     */
    private boolean isTargetType(XmlToken token) {
        if (SnowPageConstants.SNOW_PAGE_DATASET_TAG_NAME.equals(token.getText())) {
            //判断是开始标签，不然结束标签也会进入逻辑 影响结果
            PsiElement nextSibling = token.getNextSibling();
            return nextSibling instanceof PsiWhiteSpace;
        }
        return false;
    }

}
