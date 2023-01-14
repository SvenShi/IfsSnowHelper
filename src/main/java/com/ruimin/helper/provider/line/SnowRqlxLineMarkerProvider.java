package com.ruimin.helper.provider.line;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomUtil;
import com.ruimin.helper.common.constants.SnowIcons;
import com.ruimin.helper.common.util.RqlxUtils;
import com.ruimin.helper.dom.rql.model.Rql;
import java.util.HashSet;
import java.util.List;
import javax.swing.Icon;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/22 下午 09:40
 * @description dataset文件跳转java
 */
public class SnowRqlxLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken> {

    public static final HashSet<String> TARGET_TAG = Sets.newHashSet("insert", "update", "delete", "select");

    @Override
    public boolean isTheElement(@NotNull XmlToken element) {
        return isTargetType(element);
    }


    @Override
    public List<? extends PsiElement> apply(@NotNull XmlToken from) {
        Rql rql = DomUtil.findDomElement(from, Rql.class);
        if (rql != null) {
            Module module = rql.getModule();
            if (module != null) {
                String rqlxKey = RqlxUtils.getRqlxKeyByRqlTag(rql);
                if (StringUtils.isNotBlank(rqlxKey)) {
                    return RqlxUtils.findRqlReference(rqlxKey, module);
                }
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("DialogTitleCapitalization")
    public String getName() {
        return "前往java方法";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SnowIcons.GO_JAVA;
    }

    @NotNull
    @Override
    public String getTooltip() {
        return "前往java方法";
    }

    /**
     * 是否是目标标签
     */
    private boolean isTargetType(XmlToken token) {
        if (TARGET_TAG.contains(token.getText())) {
            PsiElement nextSibling = token.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                return "<".equals(token.getPrevSibling().getText());
            }
        }
        return false;
    }

}

