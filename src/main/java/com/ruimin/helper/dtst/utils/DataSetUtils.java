package com.ruimin.helper.dtst.utils;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Query;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.ruimin.helper.common.constants.CommonConstants;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.dtst.dom.model.*;
import com.ruimin.helper.java.utils.SnowJavaUtils;
import org.apache.commons.collections.CollectionUtils;
import com.ruimin.helper.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 12:35
 * @description
 */

public final class DataSetUtils {

    private DataSetUtils() {
        throw new UnsupportedOperationException();
    }


    /**
     * 查询项目中的所有dtst的data标签
     */
    public static List<Data> findAllDtst(@NotNull Project project, GlobalSearchScope scope) {
        List<DomFileElement<Data>> elements = DomService.getInstance().getFileElements(Data.class, project, scope);
        return elements.stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    /**
     * 获取标签的flowId
     */
    public static String getFlowIdSignature(@NotNull FlowIdDomElement domElement) {
        return domElement.getFlowid().getRawText();
    }

    /**
     * 根据方法名获取dataset中的flowid
     *
     * @param psiMethod 方法
     * @return flowId
     */
    public static Collection<String> findFlowIdsByMethod(PsiMethod psiMethod) {
        HashSet<String> flowIds = new HashSet<>();

        // 方法所属的类
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return null;
        }

        String flowId = psiClass.getQualifiedName() + CommonConstants.COLON_SEPARATE + psiMethod.getName();

        flowIds.add(flowId);
        Query<PsiClass> search = ClassInheritorsSearch.search(psiClass);
        // 所有子类
        Collection<PsiClass> allChildren = search.findAll();
        for (PsiClass child : allChildren) {
            String childFlowId = child.getQualifiedName() + CommonConstants.COLON_SEPARATE + psiMethod.getName();
            flowIds.add(childFlowId);
        }
        return flowIds;
    }

    /**
     * 根据flow获取所有相应的标签
     *
     * @param scope   范围
     * @param flowIds 需要对应的flowid
     * @return 查找到的xmltag
     */
    public static Collection<XmlTag> findXmlTagByFlowId(@NotNull GlobalSearchScope scope, String... flowIds) {
        HashSet<String> flowIdSet = Sets.newHashSet(flowIds);
        List<Data> dtsts = DataSetUtils.findAllDtst(Objects.requireNonNull(scope.getProject()), scope);
        ArrayList<XmlTag> xmlTags = new ArrayList<>();
        // 获取所有引用了方法的xmlTag
        for (Data dtst : dtsts) {
            for (Define define : dtst.getDefines()) {
                if (flowIdSet.contains(define.getFlowid().getRawText())) {
                    xmlTags.add(define.getXmlTag());
                }
            }
            for (Commands commands : dtst.getCommandses()) {
                for (Command command : commands.getCommands()) {
                    if (flowIdSet.contains(command.getFlowid().getRawText())) {
                        xmlTags.add(command.getXmlTag());
                    }
                }
            }
        }
        return xmlTags;
    }

    /**
     * 根据方法获取所有对应的xmltags
     */
    public static Collection<XmlTag> findTagsByMethod(PsiMethod psiMethod) {
        HashSet<XmlTag> XmlTags = Sets.newHashSet();
        Module module = ModuleUtil.findModuleForPsiElement(psiMethod);

        if (module != null) {
            Collection<String> flowIds = findFlowIdsByMethod(psiMethod);
            if (CollectionUtils.isNotEmpty(flowIds)) {
                return findXmlTagByFlowId(module.getModuleScope(), flowIds.toArray(new String[0]));
            }
        }

        return XmlTags;
    }

    /**
     * 是否包含在dtst文件中
     *
     * @param element 元素
     * @return 是否
     */
    public static boolean isElementWithinDtstFile(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        return element instanceof XmlElement && DataSetUtils.isDtstFile(psiFile);
    }

    /**
     * 是否是dtst文件
     *
     * @param file
     * @return
     */
    public static boolean isDtstFile(@Nullable PsiFile file) {
        Boolean isDtst = null;
        if (file == null) {
            isDtst = false;
        }
        if (isDtst == null) {
            if (!(file instanceof XmlFile)) {
                isDtst = false;
            }
        }
        if (isDtst == null) {
            XmlTag rootTag = ((XmlFile) file).getRootTag();
            if (rootTag == null) {
                isDtst = false;
            }
            if (isDtst == null) {
                if (!Data.class.getSimpleName().equals(rootTag.getName())) {
                    isDtst = false;
                }
            }
        }
        if (isDtst == null) {
            isDtst = true;
        }
        return isDtst;
    }

    /**
     * 找到dtst根据文件路径
     *
     * @param dtstPath dtst路径
     * @param scope    范围
     * @return {@link ArrayList}<{@link XmlFile}>
     */
    public static ArrayList<XmlFile> findDtstFileByPath(String dtstPath, @NotNull GlobalSearchScope scope) {
        ArrayList<XmlFile> psiFiles = new ArrayList<>();
        Project project = scope.getProject();
        if (project != null) {
            // dataset标签的path属性
            if (StringUtils.isNotBlank(dtstPath)) {
                int index = StringUtils.lastIndexOf(dtstPath, CommonConstants.DOT_SEPARATE);
                // 字符串处理为包名和dataset名
                String dtstName = StringUtils.substring(dtstPath, index + 1);
                String packageName = StringUtils.substring(dtstPath, 0, index);
                // 找到所在的包
                Optional<PsiPackage> aPackage = SnowJavaUtils.findPackage(project, packageName);
                if (aPackage.isPresent()) {
                    PsiPackage psiPackage = aPackage.get();
                    PsiFile[] files = psiPackage.getFiles(scope);
                    String datasetFileName = dtstName + DataSetConstants.DTST_FILE_EXTENSION_DOT;
                    for (PsiFile file : files) {
                        if (datasetFileName.equals(file.getName()) && isDtstFile(file)) {
                            psiFiles.add((XmlFile) file);
                        }
                    }

                }
            }
        }
        return psiFiles;
    }

    /**
     * 得到所有的command
     *
     * @param dtstFile dtst文件
     * @return {@link List}<{@link Command}>
     */
    public static List<Command> getCommands(XmlFile dtstFile) {
        ArrayList<Command> commands = new ArrayList<>();
        if (isDtstFile(dtstFile)) {
            DomFileElement<Data> dtstRootElement = DomManager.getDomManager(dtstFile.getProject())
                    .getFileElement(dtstFile, Data.class);
            if (dtstRootElement != null) {
                Data data = dtstRootElement.getRootElement();
                List<Commands> commandses = data.getCommandses();
                for (Commands commandsTag : commandses) {
                    commands.addAll(commandsTag.getCommands());
                }
            }

        }
        return commands;
    }

    /**
     * 得到所有的command
     *
     * @param dtstFile dtst文件
     * @return {@link List}<{@link Command}>
     */
    public static List<Field> getFields(XmlFile dtstFile) {
        ArrayList<Field> fields = new ArrayList<>();
        if (isDtstFile(dtstFile)) {
            DomFileElement<Data> dtstRootElement = DomManager.getDomManager(dtstFile.getProject())
                    .getFileElement(dtstFile, Data.class);
            if (dtstRootElement != null) {
                Data data = dtstRootElement.getRootElement();
                List<Fields> fieldses = data.getFieldses();
                for (Fields f : fieldses) {
                    fields.addAll(f.getFields());
                }
            }

        }
        return fields;
    }


    /**
     * 得到dtst文件的Data标签
     *
     * @param file dtst文件
     * @return {@link Data}
     */
    public static Data getDataTagByDtstFile(PsiFile file) {
        if (isDtstFile(file)) {
            DomFileElement<Data> fileElement = DomManager.getDomManager(file.getProject())
                    .getFileElement((XmlFile) file, Data.class);
            if (fileElement != null) {
                return fileElement.getRootElement();
            }
        }
        return null;
    }

    public static Field findField(@NotNull XmlFile file, @NotNull String fieldId) {
        Data data = getDataTagByDtstFile(file);
        if (data != null) {
            for (Fields fields : data.getFieldses()) {
                for (Field field : fields.getFields()) {
                    if (fieldId.equals(field.getId().getValue())) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 得到数据集路径
     *
     * @param datasetFile 数据集文件
     * @return {@link String}
     */
    @Nullable
    public static String getDataSetPath(@NotNull PsiFile datasetFile) {
        if (isDtstFile(datasetFile)) {
            String path = datasetFile.getVirtualFile().getPath();
            Module module = ModuleUtil.findModuleForPsiElement(datasetFile);
            if (module != null) {
                String packagePath = path;
                for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getSourceRoots()) {
                    packagePath = StringUtils.remove(packagePath, virtualFile.getPath());
                }
                if (packagePath.startsWith("/") || packagePath.startsWith("\\")) {
                    packagePath = packagePath.substring(1);
                }
                packagePath = packagePath.replace("/", ".");
                packagePath = packagePath.replace("\\", ".");
                return packagePath.replace(DataSetConstants.DTST_FILE_EXTENSION_DOT, "");
            }
            return null;
        }
        return null;
    }
}
