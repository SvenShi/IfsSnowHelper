package com.ruimin.helper.provider.line;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.constants.SnowIcons;
import com.ruimin.helper.util.DtstUtils;
import com.ruimin.helper.util.RqlxUtils;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
    protected void collectNavigationMarkers(@NotNull PsiElement element,
        @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        PsiMethodCallExpression rqlTarget = isRqlTarget(element);
        if (rqlTarget != null) {
            List<XmlAttributeValue> results = getRqlResults(rqlTarget);
            if (CollectionUtils.isNotEmpty(results)) {
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(SnowIcons.GO_RQLX)
                    .setTargets(results)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTooltipTitle("导航到rqlx文件");
                result.add(builder.createLineMarkerInfo(element));
            }
        }

        boolean dtstTarget = isDtstTarget(element);

        if (dtstTarget) {
            List<XmlTag> results = getDtstResults((PsiMethod) element);
            if (CollectionUtils.isNotEmpty(results)) {
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(SnowIcons.GO_DTST)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTargets(results)
                    .setTooltipTitle("导航到dtst文件");
                final PsiElement targetMarkerInfo = Objects.requireNonNull(
                    ((PsiNameIdentifierOwner) element).getNameIdentifier());
                result.add(builder.createLineMarkerInfo(targetMarkerInfo));
            }
        }

    }

    /**
     * 得到rql结果
     *
     * @param expression 表达式
     * @return {@link List}<{@link XmlTag}>
     */
    private List<XmlAttributeValue> getRqlResults(PsiMethodCallExpression expression) {
        ArrayList<XmlAttributeValue> attributeValues = new ArrayList<>();
        PsiExpressionList argumentList = expression.getArgumentList();
        PsiExpression[] expressions = argumentList.getExpressions();
        if (!ArrayUtils.isEmpty(expressions)) {
            if (expressions.length > 0) {
                String rqlxKey = expressions[0].getText();
                if (StringUtils.isNotBlank(rqlxKey)) {
                    rqlxKey = rqlxKey.replaceAll("\"", "");
                    attributeValues = new ArrayList<>(
                        RqlxUtils.findXmlTagByRqlKey(expression.getResolveScope(), rqlxKey));
                }
            }
        }
        return attributeValues;
    }


    /**
     * 得到dtst结果
     *
     * @param psiMethod psi方法
     * @return {@link List}<{@link XmlTag}>
     */
    private List<XmlTag> getDtstResults(PsiMethod psiMethod) {
        Collection<XmlTag> tag = DtstUtils.findTagsByMethod(psiMethod);
        return new ArrayList<>(tag);
    }

    /**
     * 是否rql目标
     *
     * @param element 元素
     * @return boolean
     */
    private PsiMethodCallExpression isRqlTarget(PsiElement element) {
        if (element instanceof PsiIdentifier) {
            if (RqlxUtils.SQL_METHOD_NAME.contains(element.getText())) {
                PsiElement reference = element.getContext();
                if (reference instanceof PsiReferenceExpression) {
                    PsiElement callMethod = reference.getContext();
                    if (callMethod instanceof PsiMethodCallExpression) {
                        return ((PsiMethodCallExpression) callMethod);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 是否dtst目标
     *
     * @param element 元素
     * @return boolean
     */
    private boolean isDtstTarget(PsiElement element) {
        // 判断是不是java方法 不是java方法直接结束方法
        return element instanceof PsiMethod;
    }

}
