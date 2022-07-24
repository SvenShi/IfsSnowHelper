package com.ruimin.helper.util;

import com.intellij.jsp.highlighter.NewJspFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.jsp.BaseJspFile;
import com.intellij.psi.jsp.JspUtil;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.constants.DtstConstants;
import com.ruimin.helper.constants.SnowPageConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * 在dataset标签中寻找路径并转成file
     *
     * @param datasetTag
     * @return
     */
    public static List<PsiFile> findFileInDatasetTag(XmlTag datasetTag) {
        ArrayList<PsiFile> psiFiles = new ArrayList<>();
        // 所属项目
        Project project = datasetTag.getProject();
        // 所属模块
        Module module = ModuleUtil.findModuleForPsiElement(datasetTag);
        if (module == null) {
            return psiFiles;
        }
        PsiManager psiManager = PsiManager.getInstance(project);
        // dataset标签的path属性
        String path = datasetTag.getAttributeValue(SnowPageConstants.DTST_ATTR_NAME_PATH);
        if (StringUtils.isNotBlank(path)) {
            int index = StringUtils.lastIndexOf(path, SnowPageConstants.PATH_SEPARATE);
            // 字符串处理为包名和datset名
            String dtstName = StringUtils.substring(path, index + 1);
            String packageName = StringUtils.substring(path, 0, index);
            // 找到所在的包
            Collection<VirtualFile> matchPackages = PackageIndex.getInstance(project)
                                                                .getDirsByPackageName(packageName,
                                                                                      module.getModuleScope(false))
                                                                .findAll();
            for (VirtualFile matchPackage : matchPackages) {
                // 匹配包下面的文件
                VirtualFile child = matchPackage.findChild(dtstName + DtstConstants.DTST_FILE_EXTENSION_DOT);
                if (child != null) {
                    PsiFile file = psiManager.findFile(child);
                    if (file != null) {
                        psiFiles.add(file);
                    }
                }
            }
        }
        return psiFiles;
    }

    /**
     * 查询项目中的所有page的dtst标签
     */
    public static List<XmlTag> findAllDtstTag(@NotNull Project project) {
        long l = System.currentTimeMillis();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        // 获取所有的jsp文件
        Collection<VirtualFile> files = FileTypeIndex.getFiles(NewJspFileType.INSTANCE, scope);
        PsiManager psiManager = PsiManager.getInstance(project);
        ArrayList<XmlTag> xmlTags = new ArrayList<>();
        // 便利查找到所有的snow:dataset标签
        for (VirtualFile item : files) {
            PsiFile file = psiManager.findFile(item);
            if (file != null) {
                BaseJspFile jspFile = JspUtil.getJspFile(file);
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
        return dtstPackName + SnowPageConstants.PATH_SEPARATE + name;
    }


    public static String getDtstPackName(PsiFile file) {
        Module module = ModuleUtil.findModuleForPsiElement(file);
        String fileAbsolutePath = file.getVirtualFile().getPath();
        fileAbsolutePath = fileAbsolutePath.replace("/" + file.getName(), "");
        if (module == null) {
            return null;
        }

        for (VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots(false)) {
            fileAbsolutePath = fileAbsolutePath.replace(sourceRoot.getPath(), "");
        }
        if (fileAbsolutePath.startsWith("/")) {
            fileAbsolutePath = fileAbsolutePath.substring(1);
        }

        return fileAbsolutePath.replace("/", SnowPageConstants.PATH_SEPARATE);
    }
}
