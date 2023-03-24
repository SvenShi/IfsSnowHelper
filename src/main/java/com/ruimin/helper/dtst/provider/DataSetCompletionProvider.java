package com.ruimin.helper.dtst.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.dtst.utils.DataSetUtils;
import com.ruimin.helper.java.utils.SnowJavaUtils;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/24 下午 11:07
 * @description 自动补全flowId和datasource
 */
public class DataSetCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
        @NotNull CompletionResultSet result) {
        XmlAttributeValue attributeValue = (XmlAttributeValue) parameters.getPosition().getParent();
        PsiFile containingFile = attributeValue.getContainingFile();
        String localName = XmlAttributeValuePattern.getLocalName(attributeValue);
        if (StringUtils.isNotBlank(localName)) {
            String name = containingFile.getName();
            if (StringUtils.isNotBlank(name) && StringUtils.endsWithIgnoreCase(name,
                DataSetConstants.DTST_FILE_EXTENSION_DOT)) {
                if (DataSetConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME.equals(localName)) {
                    flowIdCompletion(result, attributeValue);
                } else if (DataSetConstants.XML_TAG_DATASOURCE_ATTRIBUTE_NAME.equals(localName)) {
                    datasourceCompletion(result, attributeValue);
                }
            }

        }

    }


    /**
     * flowId 填充
     *
     * @param result 结果
     * @param attributeValue 属性值
     */
    private static void flowIdCompletion(@NotNull CompletionResultSet result, XmlAttributeValue attributeValue) {
        String value = attributeValue.getValue();
        if (StringUtils.isNotBlank(value)) {
            Module module = ModuleUtil.findModuleForPsiElement(attributeValue);
            if (module == null) {
                return;
            }
            if (value.contains(":")) {
                String[] split = value.split(":");
                if (split.length <= 2) {
                    String className = split[0];
                    List<PsiMethod> methods = SnowJavaUtils.findMethods(module.getModuleScope(), className, null);
                    for (PsiMethod method : methods) {
                        result.addElement(LookupElementBuilder.create(className + ":" + method.getName()));
                    }
                }
            } else if (value.contains(".")) {
                String packageName = StringUtils.substringBeforeLast(value, ".");
                Collection<VirtualFile> matchPackages = PackageIndex.getInstance(attributeValue.getProject())
                    .getDirsByPackageName(packageName, module.getModuleScope())
                    .findAll();
                for (VirtualFile file : matchPackages) {
                    for (VirtualFile child : file.getChildren()) {
                        String name = child.getName();
                        if (StringUtils.isNotBlank(name)) {
                            if (name.contains(".")) {
                                if (StringUtils.endsWithIgnoreCase(name, ".java")) {
                                    result.addElement(LookupElementBuilder.create(
                                        packageName + "." + FilenameUtils.getBaseName(name)));
                                }

                            } else {
                                result.addElement(LookupElementBuilder.create(packageName + "." + name));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * datasource 填充
     *
     * @param result 结果
     * @param attributeValue 属性值
     */
    private void datasourceCompletion(CompletionResultSet result, XmlAttributeValue attributeValue) {
        String value = attributeValue.getValue();
        if (StringUtils.isNotBlank(value)) {
            if (value.contains(":")) {
                String[] split = value.split(":");
                if (split.length == 2) {
                    String datasourceMode = split[0];
                    if (!DataSetConstants.NOT_IN_DATASOURCE_TAG.contains(datasourceMode)) {
                        String datasourcePath = split[1];
                        if (StringUtils.isNotBlank(datasourcePath) && datasourcePath.contains(".")) {
                            Module module = ModuleUtil.findModuleForPsiElement(attributeValue);
                            if (module == null) {
                                return;
                            }
                            String packageName = StringUtils.substringBeforeLast(datasourcePath, ".");
                            Collection<VirtualFile> matchPackages = PackageIndex.getInstance(
                                    attributeValue.getProject())
                                .getDirsByPackageName(packageName, module.getModuleScope())
                                .findAll();
                            for (VirtualFile file : matchPackages) {
                                for (VirtualFile child : file.getChildren()) {
                                    String name = child.getName();
                                    if (StringUtils.isNotBlank(name)) {
                                        if (name.contains(".")) {
                                            if (StringUtils.endsWithIgnoreCase(name,
                                                DataSetConstants.DTST_FILE_EXTENSION_DOT)) {
                                                result.addElement(LookupElementBuilder.create(
                                                    packageName + "." + FilenameUtils.getBaseName(name)));
                                            }

                                        } else {
                                            result.addElement(LookupElementBuilder.create(packageName + "." + name));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }

}
