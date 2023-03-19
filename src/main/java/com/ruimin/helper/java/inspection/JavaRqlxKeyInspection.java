package com.ruimin.helper.java.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.rqlx.utils.RqlxUtils;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JavaRqlxKeyInspection extends AbstractBaseJavaLocalInspectionTool {


    @Override
    public ProblemDescriptor @Nullable [] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager,
        boolean isOnTheFly) {
        ArrayList<ProblemDescriptor> problemDescriptors = new ArrayList<>();


        method.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLiteralExpression(@NotNull PsiLiteralExpression expression) {
                PsiMethodCallExpression callExpression = RqlxUtils.getLatestMethodCallExpressionFromParent(expression);
                if (callExpression != null) {
                    PsiElement referenceExpression = callExpression.getFirstChild();
                    String rqlxKey = null;
                    if (RqlxUtils.isRqlxMethodName(referenceExpression.getText())) {
                        // 直接就是rqlx select的方法
                        rqlxKey = StringUtils.removeQuot(expression.getText());
                    } else if (RqlxUtils.isSpliceRqlxKey(referenceExpression)) {
                        //     调用方法拼接的
                        rqlxKey = RqlxUtils.getSplicedRqlxKey(referenceExpression,
                            StringUtils.removeQuot(expression.getText()));

                    }
                    if (StringUtils.isNotBlank(rqlxKey)) {
                        Module module = ModuleUtil.findModuleForPsiElement(expression);
                        if (module != null) {
                            Collection<XmlAttributeValue> xmlTagByRqlKey = RqlxUtils.findXmlTagByRqlKey(
                                module.getModuleScope(),
                                rqlxKey);
                            if (CollectionUtils.isEmpty(xmlTagByRqlKey)) {
                                ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(expression,
                                    "无法根据该rqlx key定位sql!",
                                    (LocalQuickFix) null,
                                    ProblemHighlightType.ERROR, isOnTheFly);
                                problemDescriptors.add(problemDescriptor);
                            }
                        }
                    }
                }
            }
        });

        if (CollectionUtils.isNotEmpty(problemDescriptors)) {
            return problemDescriptors.toArray(ProblemDescriptor.EMPTY_ARRAY);
        }

        return ProblemDescriptor.EMPTY_ARRAY;
    }
}
