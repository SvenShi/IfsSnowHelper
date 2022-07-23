package com.ruimin.ifinsnowhelper.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.ruimin.ifinsnowhelper.dom.model.Commands;
import com.ruimin.ifinsnowhelper.dom.model.Data;
import com.ruimin.ifinsnowhelper.dom.model.Define;
import com.ruimin.ifinsnowhelper.language.SnowDtstFileType;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 12:35
 * @description
 */

public final class DtstUtils {

    private DtstUtils() {
        throw new UnsupportedOperationException();
    }

    public static final ArrayList<XmlTag> ALL_COMMAND_AND_DEFINE_LIST = new ArrayList<>();

    /**
     * 查询项目中的所有dtst的data标签
     */
    public static Collection<XmlTag> findDtsts(@NotNull Project project) {
        // if (ALL_COMMAND_AND_DEFINE_LIST.size()>0){
        //     return ALL_COMMAND_AND_DEFINE_LIST;
        // }
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        Collection<VirtualFile> files = FileTypeIndex.getFiles(SnowDtstFileType.INSTANCE, scope);
        DomManager domManager = DomManager.getDomManager(project);
        List<DomFileElement<Data>> allDomElement = files.stream()
                                                        .map(item -> PsiManager.getInstance(project).findFile(item))
                                                        .filter(item -> item instanceof XmlFile)
                                                        .map(file -> domManager.getFileElement(
                                                                (XmlFile) file, Data.class,"Data"))
                                                        .collect(Collectors.toList());

        ArrayList<XmlTag> xmlTags = new ArrayList<>();
        for (DomFileElement<Data> dataDomFileElement : allDomElement) {
            XmlTag rootTag = dataDomFileElement.getRootTag();
            if (rootTag != null) {
                XmlTag define = rootTag.findFirstSubTag(Define.class.getSimpleName());
                XmlTag commands = rootTag.findFirstSubTag(Commands.class.getSimpleName());
                xmlTags.add(define);
                if (commands != null) {
                    CollectionUtils.addAll(xmlTags,commands.getSubTags());
                }
            }
        }
        return xmlTags;
    }

}
