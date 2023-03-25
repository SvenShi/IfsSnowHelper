package com.ruimin.helper.java.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericAttributeValue;
import com.ruimin.helper.common.SnowLookUpElement;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.java.utils.SnowJavaUtils;
import com.ruimin.helper.rqlx.dom.model.Mapper;
import com.ruimin.helper.rqlx.dom.model.Rql;
import com.ruimin.helper.rqlx.dom.model.Select;
import com.ruimin.helper.rqlx.utils.RqlxUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/24 下午 11:07
 * @description 自动补全flowId和datasource
 */
public class JavaRqlxKeyCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
        @NotNull CompletionResultSet result) {
        PsiElement element;
        if ((element = parameters.getPosition().getParent()) instanceof PsiLiteralExpression) {
            PsiLiteralExpression expression = (PsiLiteralExpression) element;
            PsiMethodCallExpression callExpression = RqlxUtils.getLatestMethodCallExpressionFromParent(expression);
            if (callExpression != null) {
                PsiElement referenceExpression = callExpression.getFirstChild();
                String text = StringUtils.remove(expression.getText(), "\"", "IntellijIdeaRulezzz ");
                if (RqlxUtils.isRqlxMethodName(referenceExpression.getText())) {
                    // 直接就是rqlx select的方法
                    String rqlxKey = StringUtils.removeQuot(text);
                    Module module = ModuleUtil.findModuleForPsiElement(expression);
                    if (module != null) {
                        completionRqlxKey(result, text, rqlxKey, module);
                    }
                } else if (RqlxUtils.isSpliceRqlxKey(referenceExpression)) {
                    //     调用方法拼接的
                    String rqlxKey = RqlxUtils.getSplicedRqlxKey(referenceExpression, StringUtils.removeQuot(text));
                    Module module = ModuleUtil.findModuleForPsiElement(expression);
                    if (module != null && StringUtils.isNotBlank(rqlxKey)) {
                        completionRqlxKey(result, text, rqlxKey, module);
                    }
                }
            }
        }
    }

    /**
     * 自动填充rqlxKey
     *
     * @param rqlxKey rqlx关键
     * @param result 结果
     * @param text 文本
     * @param module 模块
     */
    private void completionRqlxKey(@NotNull CompletionResultSet result, @NotNull String text, @NotNull String rqlxKey,
        @NotNull Module module) {

        if (StringUtils.isNotBlank(rqlxKey) && rqlxKey.contains(".")) {
            String prefixKey = StringUtils.substringBeforeLast(rqlxKey, ".");
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

            if (text.contains(".")) {
                String prefix = StringUtils.substringBeforeLast(text, ".");
                for (SnowLookUpElement snowLookUpElement : appendElement) {
                    snowLookUpElement.setLookUpText(prefix + "." + snowLookUpElement.getLookupString());
                    result.addElement(snowLookUpElement);
                }
            } else {
                result.addAllElements(appendElement);
            }


        }
    }

}
