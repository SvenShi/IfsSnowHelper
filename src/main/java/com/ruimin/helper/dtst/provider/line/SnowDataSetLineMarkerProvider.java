package com.ruimin.helper.dtst.provider.line;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.helper.common.constants.SnowIcons;
import com.ruimin.helper.common.provider.line.SimpleLineMarkerProvider;
import com.ruimin.helper.dtst.dom.model.Data;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.ArrayList;
import java.util.List;
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

    public static final String DATA_TAG_NAME = Data.class.getSimpleName();


    @Override
    public boolean isTheElement(@NotNull XmlToken element) {
        return isTargetType(element);
    }


    @Override
    public List<? extends PsiElement> apply(@NotNull XmlToken from) {
        return goToJsp(from);
    }

    @Override
    @SuppressWarnings("DialogTitleCapitalization")
    public String getName() {
        return "前往jsp标志";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SnowIcons.GO_JSP;
    }

    @NotNull
    @Override
    public String getTooltip() {
        return "前往jsp";
    }

    /**
     * 是否是目标标签
     */
    private boolean isTargetType(XmlToken token) {
        if (DATA_TAG_NAME.equals(token.getText())) {
            PsiElement nextSibling = token.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                return "<".equals(token.getPrevSibling().getText());
            }
        }
        return false;
    }

    private List<? extends PsiElement> goToJsp(XmlToken xmlToken) {
        String path = SnowJspUtils.getDtstPath(xmlToken.getContainingFile());
        if (StringUtils.isNotBlank(path)) {
            Module module = ModuleUtil.findModuleForPsiElement(xmlToken);
            if (module != null) {
                List<XmlTag> allDtstTag = SnowJspUtils.getAllDtstTag(module);
                if (CollectionUtils.isNotEmpty(allDtstTag)) {
                    ArrayList<PsiElement> psiElements = new ArrayList<>();
                    for (XmlTag dtstTag : allDtstTag) {
                        String attributeValue = dtstTag.getAttributeValue(JspConstants.ATTR_NAME_PATH);
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
}

