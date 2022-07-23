package com.ruimin.ifinsnowhelper.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import com.ruimin.ifinsnowhelper.dom.model.Data;
import com.ruimin.ifinsnowhelper.dom.model.FlowIdDomElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    public static List<Data> findDtsts(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<Data>> elements = DomService.getInstance().getFileElements(Data.class, project, scope);
        return elements.stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    /**
     * 获取标签的flowId
     */
    public static String getFlowIdSignature(@NotNull FlowIdDomElement domElement) {
        return domElement.getFlowid().getRawText();
    }
}
