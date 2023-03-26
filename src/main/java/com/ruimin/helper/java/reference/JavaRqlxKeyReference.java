package com.ruimin.helper.java.reference;

import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.java.utils.SnowJavaUtils;
import com.ruimin.helper.rqlx.dom.model.Mapper;
import com.ruimin.helper.rqlx.dom.model.Rql;
import com.ruimin.helper.rqlx.dom.model.Select;
import com.ruimin.helper.rqlx.utils.RqlxUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class JavaRqlxKeyReference extends PsiReferenceBase<PsiLiteralExpression> implements PsiPolyVariantReference {


    /**
     * rqlx key
     */
    private final String rqlxKey;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param element Underlying element.
     */
    public JavaRqlxKeyReference(@NotNull PsiLiteralExpression element, String rqlxKey) {
        super(Objects.requireNonNull(element),
            new TextRange(1, DataUtils.mustPositive(element.getText().length() - 1, 1)));
        this.rqlxKey = rqlxKey;
    }


    /**
     * Returns the array of String, {@link PsiElement} and/or {@link LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array are used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by the IDE.)
     * <p>
     * This method is default since 2018.3.
     *
     * @return the array of available identifiers.
     */
    @Override
    public Object @NotNull [] getVariants() {
        if (StringUtils.isNotBlank(rqlxKey) && rqlxKey.contains(".")) {
            Module module = ModuleUtil.findModuleForPsiElement(myElement);
            if (module == null) {
                return super.getVariants();
            }
            String beforeRqlxKey = StringUtils.substringBeforeLast(rqlxKey, CompletionUtil.DUMMY_IDENTIFIER);
            String prefixKey = StringUtils.substringBeforeLast(beforeRqlxKey, ".");
            Optional<PsiPackage> aPackage = SnowJavaUtils.findPackage(module.getProject(), prefixKey);
            List<PsiFile> rqlxFiles = RqlxUtils.findFilesByPath(prefixKey, module.getModuleScope());
            ArrayList<SnowLookUpElement> appendElement = new ArrayList<>();
            if (aPackage.isPresent()) {
                PsiPackage psiPackage = aPackage.get();
                PsiPackage[] subPackages = psiPackage.getSubPackages(module.getModuleScope());
                PsiFile[] files = psiPackage.getFiles(module.getModuleScope());
                for (PsiPackage subPackage : subPackages) {
                    appendElement.add(new SnowLookUpElement(subPackage.getName(), subPackage));
                }
                for (PsiFile file : files) {
                    if (RqlxUtils.isRqlxFile(file)) {
                        appendElement.add(new SnowLookUpElement(FilenameUtils.getBaseName(file.getName()), file));
                    }
                }

            }

            if (CollectionUtils.isNotEmpty(rqlxFiles)) {
                DomManager domManager = DomManager.getDomManager(module.getProject());
                for (PsiFile file : rqlxFiles) {
                    XmlFile xmlFile = (XmlFile) file;
                    DomFileElement<Mapper> mapperDomFileElement = domManager.getFileElement(xmlFile, Mapper.class);
                    if (mapperDomFileElement != null) {
                        Mapper mapper = mapperDomFileElement.getRootElement();
                        List<Rql> rqls = mapper.getRqls();
                        for (Rql rql : rqls) {
                            XmlAttribute xmlAttribute = rql.getId().getXmlAttribute();
                            GenericAttributeValue<String> paramType = rql.getParamType();
                            String resultType = null;
                            if (rql instanceof Select) {
                                Select select = (Select) rql;
                                resultType = select.getResultType().getValue();
                                if (StringUtils.isNotBlank(resultType)) {
                                    resultType = StringUtils.substringAfterLast(resultType, ".");
                                }
                            }
                            if (xmlAttribute != null) {
                                appendElement.add(
                                    new SnowLookUpElement(xmlAttribute.getValue(), xmlAttribute, paramType.getValue(),
                                        resultType));
                            }
                        }
                    }
                }
            }
            String s = StringUtils.substringBeforeLast(myElement.getText(), CompletionUtil.DUMMY_IDENTIFIER);
            String text = StringUtils.removeQuot(s);
            ArrayList<LookupElement> result = new ArrayList<>();
            if (text.contains(".")) {
                String prefix = StringUtils.substringBeforeLast(text, ".");
                for (SnowLookUpElement snowLookUpElement : appendElement) {
                    snowLookUpElement.setLookUpText(prefix + "." + snowLookUpElement.getLookupString());
                    result.add(snowLookUpElement);
                }
            } else {
                result.addAll(appendElement);
            }
            return result.toArray();
        }
        return super.getVariants();
    }

    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or {@code null} if it was not possible to resolve the reference to a valid target.
     * @see PsiPolyVariantReference#multiResolve(boolean)
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length >= 1 ? resolveResults[0].getElement() : null;
    }


    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     * being resolved is considered incomplete, and the method may return additional
     * invalid results.
     * @return the array of results for resolving the reference.
     */
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Module module = ModuleUtil.findModuleForPsiElement(myElement);
        if (module != null) {
            Collection<XmlAttributeValue> xmlTagByRqlKey = RqlxUtils.findXmlTagByRqlKey(module.getModuleScope(),
                rqlxKey);
            if (CollectionUtils.isNotEmpty(xmlTagByRqlKey)) {
                ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                for (XmlAttributeValue attributeValue : xmlTagByRqlKey) {
                    resolveResults.add(new PsiElementResolveResult(attributeValue));
                }
                return resolveResults.toArray(ResolveResult.EMPTY_ARRAY);
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    /**
     * @param newElementName the new name of the target element.
     * @return
     * @throws IncorrectOperationException
     */
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String text = myElement.getText();
        String removeQuot = StringUtils.removeQuot(text);
        String s = StringUtils.substringBeforeLast(removeQuot, ".");
        return super.handleElementRename(s + "." + newElementName);
    }
}
