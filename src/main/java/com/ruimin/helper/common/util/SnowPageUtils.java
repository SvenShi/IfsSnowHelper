package com.ruimin.helper.common.util;


import com.intellij.jsp.highlighter.NewJspFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.jsp.BaseJspFile;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.common.constants.DtstConstants;
import com.ruimin.helper.common.constants.SnowPageConstants;
import java.util.ArrayList;
import java.util.Collection;
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
public class SnowPageUtils {

    private SnowPageUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 查询项目中的所有page的dtst标签
     */
    public static List<XmlTag> findAllDtstTag(@NotNull Module module) {
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
                    for (XmlTag subTag : jspFile.getRootTag().findSubTags(SnowPageConstants.SNOW_PAGE_ROOT_TAG_NAME)) {
                        CollectionUtils.addAll(xmlTags,
                            subTag.findSubTags(SnowPageConstants.SNOW_PAGE_DATASET_TAG_NAME));
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
        name = name.replace(DtstConstants.DTST_FILE_EXTENSION_DOT, "");
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
     * 找到所有数据集标签
     *
     * @param jspFile jsp文件
     * @return {@link List}<{@link XmlTag}>
     */
    public static List<XmlTag> getDataSetTag(JspFile jspFile) {
        List<XmlTag> pageTags = getPageTag(jspFile);
        List<XmlTag> datasetTags = new ArrayList<>();
        for (XmlTag pageTag : pageTags) {
            XmlTag[] subTags = pageTag.findSubTags(SnowPageConstants.SNOW_PAGE_DATASET_TAG_NAME);
            datasetTags.addAll(List.of(subTags));
        }
        return datasetTags;
    }

    /**
     * 找到所有页面标签
     *
     * @param jspFile jsp文件
     * @return {@link List}<{@link XmlTag}>
     */
    public static List<XmlTag> getPageTag(JspFile jspFile) {
        XmlTag rootTag = jspFile.getRootTag();
        XmlTag[] subTags = rootTag.findSubTags(SnowPageConstants.SNOW_PAGE_ROOT_TAG_NAME);
        return List.of(subTags);
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
