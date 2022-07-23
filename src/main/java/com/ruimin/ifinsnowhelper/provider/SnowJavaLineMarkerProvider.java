package com.ruimin.ifinsnowhelper.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Query;
import com.ruimin.ifinsnowhelper.constants.DtstConstants;
import com.ruimin.ifinsnowhelper.dom.model.Command;
import com.ruimin.ifinsnowhelper.dom.model.Commands;
import com.ruimin.ifinsnowhelper.dom.model.Data;
import com.ruimin.ifinsnowhelper.dom.model.Define;
import com.ruimin.ifinsnowhelper.language.SnowIcons;
import com.ruimin.ifinsnowhelper.util.DtstUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
        //获取方法所在类
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return new ArrayList<>();
        }
        //获取所属模块
        Module module = ModuleUtil.findModuleForPsiElement(psiClass);
        if (module == null) {
            return new ArrayList<>();
        }
        //获取所有的dtst文件
        List<Data> dtsts = DtstUtils.findDtsts(psiMethod.getProject(), module.getModuleScope(false));
        HashSet<String> flowIds = new HashSet<>();
        String flowId = psiClass.getQualifiedName() + DtstConstants.FLOWID_SEPARATE + psiMethod.getName();
        flowIds.add(flowId);
        Query<PsiClass> search = ClassInheritorsSearch.search(psiClass);
        // 所有子类
        Collection<PsiClass> allChildren = search.findAll();
        for (PsiClass child : allChildren) {
            String childFlowId = child.getQualifiedName() + DtstConstants.FLOWID_SEPARATE + psiMethod.getName();
            flowIds.add(childFlowId);
        }
        ArrayList<XmlTag> xmlTags = new ArrayList<>();
        // 获取所有引用了方法的xmlTag
        for (Data dtst : dtsts) {
            for (Define define : dtst.getDefines()) {
                if (flowIds.contains(define.getFlowid().getRawText())) {
                    xmlTags.add(define.getXmlTag());
                }
            }
            for (Commands commands : dtst.getCommandses()) {
                for (Command command : commands.getCommands()) {
                    if (flowIds.contains(command.getFlowid().getRawText())) {
                        xmlTags.add(command.getXmlTag());
                    }
                }
            }
        }
        return xmlTags;
    }


}
