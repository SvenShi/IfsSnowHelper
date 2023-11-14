package com.ruimin.helper.dtst.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.util.DataUtils;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import com.ruimin.helper.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 04:07
 * @description
 */
public class DatasetDataSourceReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {


    private final String datasetPath;

    /**
     * Reference range is obtained from {@link ElementManipulator#getRangeInElement(PsiElement)}.
     *
     * @param indexOf
     * @param element Underlying element.
     */
    public DatasetDataSourceReference(@NotNull XmlAttributeValue element, int indexOf, String datasetPath) {
        super(Objects.requireNonNull(element),
            new TextRange(indexOf + 1, DataUtils.mustPositive(element.getTextLength() - 1, 0)));
        this.datasetPath = datasetPath;
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
        String value = myElement.getValue();
        if (StringUtils.isNotBlank(value)) {
            if (value.contains(":")) {
                String[] split = value.split(":");
                if (split.length == 2) {
                    String datasourceMode = split[0];
                    if (!DataSetConstants.NOT_IN_DATASOURCE_TAG.contains(datasourceMode)) {
                        String datasourcePath = split[1];
                        if (StringUtils.isNotBlank(datasourcePath) && datasourcePath.contains(".")) {
                            Module module = ModuleUtil.findModuleForPsiElement(myElement);
                            if (module == null) {
                                return super.getVariants();
                            }
                            String packageName = StringUtils.substringBeforeLast(datasourcePath, ".");
                            Collection<VirtualFile> matchPackages = PackageIndex.getInstance(myElement.getProject())
                                .getDirsByPackageName(packageName, module.getModuleScope())
                                .findAll();
                            PsiManager psiManager = PsiManager.getInstance(myElement.getProject());
                            ArrayList<LookupElement> result = new ArrayList<>();
                            for (VirtualFile file : matchPackages) {
                                for (VirtualFile child : file.getChildren()) {
                                    PsiFile psiFile = psiManager.findFile(child);
                                    if (psiFile != null) {
                                        String name = child.getName();
                                        if (StringUtils.isNotBlank(name)) {
                                            if (name.contains(".")) {
                                                if (StringUtils.endsWithIgnoreCase(name,
                                                    DataSetConstants.DTST_FILE_EXTENSION_DOT)) {
                                                    result.add(new SnowLookUpElement(
                                                        packageName + "." + FilenameUtils.getBaseName(name), psiFile));
                                                }

                                            } else {
                                                result.add(new SnowLookUpElement(packageName + "." + name, psiFile));
                                            }
                                        }
                                    }
                                }
                            }
                            return result.toArray();
                        }
                    }
                }
            }
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
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
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
            ArrayList<XmlFile> dtst = DataSetUtils.findDtstFileByPath(datasetPath, module.getModuleScope());
            if (CollectionUtils.isNotEmpty(dtst)) {
                ArrayList<ResolveResult> resolveResults = new ArrayList<>();
                for (XmlFile xmlFile : dtst) {
                    resolveResults.add(new PsiElementResolveResult(xmlFile));
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
        XmlAttribute parent = (XmlAttribute) myElement.getParent();
        String dtstPath = myElement.getValue();
        if (StringUtils.isNotBlank(dtstPath)) {
            String s = StringUtils.substringBeforeLast(dtstPath, ".");
            String filename = FilenameUtils.removeExtension(newElementName);
            parent.setValue(s + "." + filename);
        }
        return myElement;
    }
}
