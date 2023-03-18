package com.ruimin.helper.jsp.utils;


import com.intellij.jsp.highlighter.NewJspFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.jsp.BaseJspFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.jsp.constans.JspConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 下午 09:46
 * @description
 */
public class SnowJspUtils {

    private SnowJspUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 查询项目中的所有page的dtst标签
     */
    public static List<XmlTag> getAllDtstTag(@NotNull Module module) {
        long l = System.currentTimeMillis();
        GlobalSearchScope moduleScope = module.getModuleScope();

        // 获取所有的jsp文件
        Collection<VirtualFile> files = FileTypeIndex.getFiles(NewJspFileType.INSTANCE, moduleScope);
        PsiManager psiManager = PsiManager.getInstance(module.getProject());
        ArrayList<XmlTag> xmlTags = new ArrayList<>();
        // 便利查找到所有的snow:dataset标签
        for (VirtualFile item : files) {
            PsiFile file = psiManager.findFile(item);
            if (file != null) {
                BaseJspFile jspFile = getJspFile(file);
                if (jspFile != null) {
                    for (XmlTag subTag : jspFile.getRootTag().findSubTags(JspConstants.PAGE_TAG_NAME)) {
                        CollectionUtils.addAll(xmlTags, subTag.findSubTags(JspConstants.DATASET_TAG_NAME));
                    }
                }
            }
        }
        return xmlTags;
    }

    /**
     * 根据psi文件路径转为dtst的path属性
     *
     * @param file dtst中的属性
     * @return path
     */
    public static String getDtstPath(PsiFile file) {
        String dtstPackName = getDtstPackName(file);
        if (dtstPackName == null) {
            return null;
        }
        String name = file.getVirtualFile().getName();
        name = name.replace(DataSetConstants.DTST_FILE_EXTENSION_DOT, "");
        return dtstPackName + CommonConstants.DOT_SEPARATE + name;
    }


    public static String getDtstPackName(PsiFile file) {
        Module module = ModuleUtil.findModuleForPsiElement(file);
        String fileAbsolutePath = file.getVirtualFile().getPath();
        fileAbsolutePath = fileAbsolutePath.replace("/" + file.getName(), "");
        if (module == null) {
            return null;
        }

        for (VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots()) {
            fileAbsolutePath = fileAbsolutePath.replace(sourceRoot.getPath(), "");
        }
        if (fileAbsolutePath.startsWith("/")) {
            fileAbsolutePath = fileAbsolutePath.substring(1);
        }

        return fileAbsolutePath.replace("/", CommonConstants.DOT_SEPARATE);
    }

    /**
     * 得到jsp文件
     *
     * @param element 元素
     * @return {@link BaseJspFile}
     */
    public static @Nullable BaseJspFile getJspFile(@NotNull PsiElement element) {

        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return null;
        } else {
            FileViewProvider fileViewProvider = containingFile.getViewProvider();
            PsiFile psi = fileViewProvider.getPsi(fileViewProvider.getBaseLanguage());
            return psi instanceof BaseJspFile ? (BaseJspFile) psi : null;
        }
    }

    /**
     * 找到所有页面标签
     *
     * @param jspFile jsp文件
     * @return {@link List}<{@link XmlTag}>
     */
    public static List<XmlTag> findAllTagInFile(@NotNull XmlFile jspFile, @NotNull String tagName) {
        XmlTag rootTag = jspFile.getRootTag();
        if (rootTag != null) {
            ArrayList<XmlTag> pageTags = new ArrayList<>();
            findAllTag(rootTag, pageTags, tagName);
            return pageTags;
        }
        return Collections.emptyList();
    }

    private static void findAllTag(XmlTag rootTag, ArrayList<XmlTag> pageTags, String tagName) {
        if (rootTag == null) {
            return;
        }
        for (XmlTag subTag : rootTag.getSubTags()) {
            findAllTag(subTag, pageTags, tagName);
            if (tagName.equals(subTag.getName())) {
                pageTags.add(subTag);
            }
            if (JspConstants.INCLUDE_TAG_NAME.equals(subTag.getName())) {
                XmlAttribute attribute = subTag.getAttribute(JspConstants.ATTR_NAME_FILE);
                if (attribute != null) {
                    XmlAttributeValue valueElement = attribute.getValueElement();
                    if (valueElement != null) {
                        for (PsiReference reference : valueElement.getReferences()) {
                            PsiElement resolve = reference.resolve();
                            if (resolve instanceof XmlFile) {
                                XmlFile xmlFile = (XmlFile) resolve;
                                findAllTag(xmlFile.getRootTag(), pageTags, tagName);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 得到标签
     *
     * @param attribute 属性
     * @return {@link XmlTag}
     */
    public static XmlTag findTag(XmlElement attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof XmlTag) {
            return (XmlTag) attribute;
        }
        PsiElement parent = attribute.getParent();

        if (parent instanceof XmlElement) {
            return findTag((XmlElement) parent);
        } else {
            return null;
        }
    }
}
