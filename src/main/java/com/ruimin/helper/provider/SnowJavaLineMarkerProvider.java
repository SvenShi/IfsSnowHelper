package com.ruimin.helper.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.constants.SnowIcons;
import com.ruimin.helper.util.DtstUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 12:16
 * @description java 跳转到 dataset
 */
public class SnowJavaLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<?
            super RelatedItemLineMarkerInfo<?>> result) {
        // 判断是不是java方法 不是java方法直接结束方法
        if (!(element instanceof PsiMethod)) {
            return;
        }

        final List<XmlTag> results = getResults((PsiMethod) element);
        if (CollectionUtils.isNotEmpty(results)) {
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(SnowIcons.GO_GREEN)
                                                                                         .setAlignment(
                                                                                                 GutterIconRenderer.Alignment.CENTER)
                                                                                         .setTargets(results)
                                                                                         .setTooltipTitle("导航到dtst文件");
            final PsiElement targetMarkerInfo = Objects.requireNonNull(
                    ((PsiNameIdentifierOwner) element).getNameIdentifier());
            result.add(builder.createLineMarkerInfo(targetMarkerInfo));
        }
    }

    private List<XmlTag> getResults(PsiMethod psiMethod) {

        Collection<XmlTag> tag = DtstUtils.findTagsByMethod(psiMethod);

        return new ArrayList<>(tag);
    }


}
