package com.ruimin.helper.js.provider.reference;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.js.reference.JavaScriptFieldReference;
import com.ruimin.helper.js.utils.SnowJSUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JSLiteralReferenceProvider extends PsiReferenceProvider {


    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        JSReferenceExpression methodCaller = SnowJSUtils.getDataSetMethodCaller(element);
        if (methodCaller != null) {
            XmlFile xmlFile = SnowJSUtils.getDataSetTagByReference(methodCaller);
            if (xmlFile != null) {
                return new PsiReference[]{new JavaScriptFieldReference((JSLiteralExpression) element, xmlFile)};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }

}
