package com.ruimin.helper.util;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.ruimin.helper.constants.CommonConstants;
import com.ruimin.helper.constants.RqlxConstants;
import com.ruimin.helper.dom.dtst.model.Data;
import com.ruimin.helper.dom.rql.model.Delete;
import com.ruimin.helper.dom.rql.model.Insert;
import com.ruimin.helper.dom.rql.model.Mapper;
import com.ruimin.helper.dom.rql.model.Rql;
import com.ruimin.helper.dom.rql.model.Select;
import com.ruimin.helper.dom.rql.model.Update;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.math.raw.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 12:35
 * @description
 */

public final class RqlxUtils {

    private RqlxUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 查询项目中的所有dtst的data标签
     */
    public static List<Mapper> findAllRqlx(@NotNull Project project, GlobalSearchScope scope) {
        @NotNull List<DomFileElement<Mapper>> elements = DomService.getInstance()
            .getFileElements(Mapper.class, project, scope);
        return elements.stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    /**
     * 获取rqlx key
     *
     * @param rql rql
     * @return {@link String}
     */
    public static String getRqlxKeyByRqlTag(@NotNull Rql rql) {
        String id = rql.getId().getValue();
        XmlTag xmlTag = rql.getXmlTag();
        Module module = rql.getModule();
        if (xmlTag != null && module != null) {
            PsiFile file = xmlTag.getContainingFile();
            String filePath = file.getVirtualFile().getPath();
            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            VirtualFile[] roots = rootManager.getSourceRoots(false);
            for (VirtualFile root : roots) {
                String path = root.getPath();
                filePath = StringUtils.remove(filePath, path);
            }
            if (filePath.startsWith("/") || filePath.startsWith("\\")) {
                filePath = filePath.substring(1);
            }
            filePath = filePath.replace("/", ".");
            filePath = filePath.replace("\\", ".");
            filePath = StringUtils.remove(filePath, RqlxConstants.RQLX_FILE_EXTENSION_DOT);
            return filePath + CommonConstants.DOT_SEPARATE + id;
        }
        return null;
    }

    /**
     * 根据rqlKey获取所有相应的标签
     *
     * @param scope 范围
     * @param rqlKeys 需要对应的flowid
     * @return 查找到的xmltag
     */
    public static Collection<XmlTag> findXmlTagByRqlKey(@NotNull GlobalSearchScope scope, String... rqlKeys) {
        ArrayList<XmlTag> xmlTags = new ArrayList<>();
        HashSet<String> rqlKeySet = Sets.newHashSet(rqlKeys);
        Project project = scope.getProject();
        if (CollectionUtils.isNotEmpty(rqlKeySet) && project != null) {
            for (String key : rqlKeySet) {
                if (StringUtils.isNotBlank(key)) {
                    int fileAndMethodIndex = StringUtils.lastIndexOf(key, CommonConstants.DOT_SEPARATE);
                    // 字符串处理为包名和rqlx名
                    String methodName = StringUtils.substring(key, fileAndMethodIndex + 1);
                    String rqlxFilePath = StringUtils.substring(key, 0, fileAndMethodIndex);
                    Collection<XmlFile> xmlFiles = findFileByRqlxFilePath(scope, rqlxFilePath);
                    if (CollectionUtils.isNotEmpty(xmlFiles) && StringUtils.isNotBlank(methodName)) {
                        for (XmlFile xmlFile : xmlFiles) {
                            Mapper mapper = getMapperTagByRqlxFile(xmlFile);
                            if (mapper != null) {
                                for (Delete delete : mapper.getDeletes()) {
                                    if (methodName.equals(delete.getId().getValue())) {
                                        xmlTags.add(delete.getXmlTag());
                                    }
                                }
                                for (Insert insert : mapper.getInserts()) {
                                    if (methodName.equals(insert.getId().getValue())) {
                                        xmlTags.add(insert.getXmlTag());
                                    }
                                }
                                for (Select select : mapper.getSelects()) {
                                    if (methodName.equals(select.getId().getValue())) {
                                        xmlTags.add(select.getXmlTag());
                                    }
                                }
                                for (Update update : mapper.getUpdates()) {
                                    if (methodName.equals(update.getId().getValue())) {
                                        xmlTags.add(update.getXmlTag());
                                    }
                                }
                                for (Rql ddl : mapper.getDdls()) {
                                    if (methodName.equals(ddl.getId().getValue())) {
                                        xmlTags.add(ddl.getXmlTag());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return xmlTags;
    }

    /**
     * 找到rqlx rqlx文件文件路径
     *
     * @param scope 范围
     * @param rqlxFilePath rqlx文件路径
     * @return {@link Collection}<{@link XmlFile}>
     */
    private static Collection<XmlFile> findFileByRqlxFilePath(GlobalSearchScope scope, String rqlxFilePath) {
        ArrayList<XmlFile> xmlFiles = new ArrayList<>();
        int packageAndFileIndex = StringUtils.lastIndexOf(rqlxFilePath, CommonConstants.DOT_SEPARATE);
        String rqlxName = StringUtils.substring(rqlxFilePath, packageAndFileIndex + 1);
        String packageName = StringUtils.substring(rqlxFilePath, 0, packageAndFileIndex);
        // 找到所在的包
        Project project = scope.getProject();
        if (project != null) {
            Collection<VirtualFile> matchPackages = PackageIndex.getInstance(project)
                .getDirsByPackageName(packageName, false)
                .findAll();
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile matchPackage : matchPackages) {
                // 匹配包下面的文件
                VirtualFile child = matchPackage.findChild(rqlxName + RqlxConstants.RQLX_FILE_EXTENSION_DOT);
                if (child != null) {
                    PsiFile file = psiManager.findFile(child);
                    if (isRqlxFile(file)) {
                        xmlFiles.add((XmlFile) file);
                    }
                }
            }
        }
        return xmlFiles;
    }

    /**
     * 是否是rqlx文件
     *
     * @param file
     */
    public static boolean isRqlxFile(@Nullable PsiFile file) {
        Boolean isRqlx = null;
        if (file == null) {
            isRqlx = false;
        }
        if (isRqlx == null) {
            if (!(file instanceof XmlFile)) {
                isRqlx = false;
            }
        }
        if (isRqlx == null) {
            XmlTag rootTag = ((XmlFile) file).getRootTag();
            if (rootTag == null) {
                isRqlx = false;
            }
            if (isRqlx == null) {
                if (!"mapper".equals(rootTag.getName())) {
                    isRqlx = false;
                }
            }
        }
        if (isRqlx == null) {
            isRqlx = true;
        }
        return isRqlx;
    }

    /**
     * 得到rqlx文件的mapper标签
     *
     * @param file rqlx文件
     * @return {@link Data}
     */
    public static Mapper getMapperTagByRqlxFile(PsiFile file) {
        if (isRqlxFile(file)) {
            DomFileElement<Mapper> fileElement = DomManager.getDomManager(file.getProject())
                .getFileElement((XmlFile) file, Mapper.class);
            if (fileElement != null) {
                return fileElement.getRootElement();
            }
        }
        return null;
    }
}
