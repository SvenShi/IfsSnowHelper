package com.ruimin.helper.common;

import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/25 上午 01:45
 * @description
 */
public class SnowLookUpElement extends LookupElement {

    private String text;
    private final PsiElement element;
    private final String tailText;

    private final String typeText;

    public SnowLookUpElement(String text, PsiElement element) {
        this.text = text;
        this.element = element;
        this.tailText = null;
        this.typeText = null;
    }

    public SnowLookUpElement(String text, PsiElement element, String tailText, String typeText) {
        this.text = text;
        this.element = element;
        this.tailText = tailText;
        this.typeText = typeText;
    }

    /**
     * @return the string which will be inserted into the editor when this lookup element is chosen
     */
    @Override
    public @NotNull String getLookupString() {
        return text;
    }

    /**
     * @return some object that this lookup element represents, often a {@link PsiElement} or another kind of symbol.
     * This is mostly used by extensions analyzing the lookup elements, e.g. for sorting purposes.
     */
    @Override
    public @NotNull Object getObject() {
        return element == null ? text : element;
    }

    /**
     * Fill the given presentation object with details specifying how this lookup element should look when rendered.
     * By default, just sets the item text to the lookup string.<p></p>
     * <p>
     * This method is called before the item can be shown in the suggestion list, so it should be relatively fast to ensure that
     * list is shown as soon as possible. If there are heavy computations involved, consider making them optional and moving into
     * to {@link #getExpensiveRenderer()}.
     *
     * @param presentation
     */
    @Override
    public void renderElement(@NotNull LookupElementPresentation presentation) {
        presentation.setItemText(text);
        if (element != null) {
            presentation.setIcon(element.getIcon(Iconable.ICON_FLAG_VISIBILITY));
        }
        if (tailText != null) {
            presentation.setTailText(" " + tailText);
        }
        if (typeText != null) {
            presentation.setTypeText(typeText);
        }
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            presentation.setItemText(method.getName());
            presentation.setTailText(PsiFormatUtil.formatMethod(method, PsiSubstitutor.EMPTY, 256, 3));
            PsiType returnType = method.getReturnType();
            if (returnType != null) {
                presentation.setTypeText(returnType.getCanonicalText());
            }
        } else if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            presentation.setItemText(psiClass.getName());
            String locationString = PsiFormatUtil.getPackageDisplayName(psiClass);
            String tailText = " " + locationString;
            if (psiClass.isInterface() || psiClass.hasModifierProperty("abstract")) {
                tailText = "{...}" + locationString;
            }

            if (psiClass.getTypeParameters().length > 0) {
                String separator = "," + (showSpaceAfterComma(psiClass) ? " " : "");
                String type = StringUtil.join(psiClass.getTypeParameters(), PsiQualifiedNamedElement::getName,
                    separator);
                tailText = "<" + type + ">" + tailText;
            }
            presentation.setTailText(tailText, true);
        } else if (element instanceof XmlAttribute) {
            presentation.setIcon(element.getContainingFile().getIcon(Iconable.ICON_FLAG_VISIBILITY));
        }

    }

    private boolean showSpaceAfterComma(PsiClass psiClass) {
        return CodeStyle.getLanguageSettings(element.getContainingFile(), JavaLanguage.INSTANCE).SPACE_AFTER_COMMA;
    }

    public void setLookUpText(String text) {
        this.text = text;
    }

}
