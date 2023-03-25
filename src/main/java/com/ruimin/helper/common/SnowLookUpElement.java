package com.ruimin.helper.common;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.ruimin.helper.common.constants.SnowIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/25 上午 01:45
 * @description
 */
public class SnowLookUpElement extends LookupElement {

    private final String text;

    public SnowLookUpElement(String text) {
        this.text = text;
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
        return text;
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
        presentation.setIcon(SnowIcons.LOGO);
    }
}
