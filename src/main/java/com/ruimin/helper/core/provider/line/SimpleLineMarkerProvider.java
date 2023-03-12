package com.ruimin.helper.core.provider.line;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The type Simple line marker provider.
 *
 * @param <T> the type parameter
 * @author yanglin
 */
@SuppressWarnings("unchecked")
public abstract class SimpleLineMarkerProvider<T extends PsiElement> extends RelatedItemLineMarkerProvider {

    private Class<T> genericClazz;

    {
        // 获取当前类生命的泛型类型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericSuperclass;
            for (Type actualTypeArgument : type.getActualTypeArguments()) {
                if (actualTypeArgument instanceof Class) {
                    genericClazz = ((Class<T>) actualTypeArgument);
                }
            }
        }

    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
        @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (genericClazz.isInstance(element)) {
            T target = (T) element;
            if (!isTheElement(target)) {
                return;
            }

            List<? extends PsiElement> processResult = apply(target);
            if (CollectionUtils.isNotEmpty(processResult)) {
                NavigationGutterIconBuilder<PsiElement> navigationGutterIconBuilder = NavigationGutterIconBuilder.create(
                    getIcon());
                navigationGutterIconBuilder.setTooltipTitle(getTooltip());
                navigationGutterIconBuilder.setTargets(processResult);
                RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = navigationGutterIconBuilder.createLineMarkerInfo(
                    element);
                result.add(lineMarkerInfo);
            }
        }
    }


    /**
     * Is the element boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public abstract boolean isTheElement(@NotNull T element);

    /**
     * Apply optional.
     *
     * @param from the from
     * @return the optional
     */
    public abstract List<? extends PsiElement> apply(@NotNull T from);


    /**
     * Gets icon.
     *
     * @return the icon
     */
    @Override
    @NotNull
    public abstract Icon getIcon();

    @NotNull
    public abstract String getTooltip();
}
