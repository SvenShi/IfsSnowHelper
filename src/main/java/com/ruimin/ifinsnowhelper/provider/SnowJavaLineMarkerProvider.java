package com.ruimin.ifinsnowhelper.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Query;
import com.ruimin.ifinsnowhelper.constants.SnowConstants;
import com.ruimin.ifinsnowhelper.language.SnowIcons;
import com.ruimin.ifinsnowhelper.util.DtstUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

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
        //判断是不是java方法 不是java方法直接结束方法
        if (!(element instanceof PsiMethod)){
            return;
        }

        final Collection<XmlTag> results = getResults((PsiMethod)element);
        if (CollectionUtils.isNotEmpty(results)) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(SnowIcons.LOGO)
                                               .setAlignment(GutterIconRenderer.Alignment.CENTER)
                                               .setTargets(results)
                                               .setTooltipTitle("导航到dtst文件");
            final PsiElement targetMarkerInfo = Objects.requireNonNull(((PsiNameIdentifierOwner) element).getNameIdentifier());
            result.add(builder.createLineMarkerInfo(targetMarkerInfo));
        }
    }

    private Collection<XmlTag> getResults(PsiMethod psiMethod) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null){
            return  new ArrayList<>();
        }
        Collection<XmlTag> allContainFlowIdTags = DtstUtils.findDtsts(psiMethod.getProject());
        HashSet<String> flowIds = new HashSet<>();
        String flowId = psiClass.getQualifiedName() + SnowConstants.FLOWID_SEPARATE + psiMethod.getName();
        flowIds.add(flowId);
        Query<PsiClass> search = ClassInheritorsSearch.search(psiClass);
        //所有子类
        Collection<PsiClass> allChildren = search.findAll();
        for (PsiClass child : allChildren) {
            String childFlowId = child.getQualifiedName() + SnowConstants.FLOWID_SEPARATE + psiMethod.getName();
            flowIds.add(childFlowId);
        }

        return allContainFlowIdTags.stream().filter(item -> {
            String attributeValue = item.getAttributeValue(SnowConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME);
            return flowIds.contains(attributeValue);
        }).collect(Collectors.toList());
    }


}
