package com.ruimin.helper.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.ruimin.helper.constants.SnowIcons;
import com.ruimin.helper.constants.SnowPageConstants;
import com.ruimin.helper.dom.dtst.model.Command;
import com.ruimin.helper.dom.dtst.model.Commands;
import com.ruimin.helper.dom.dtst.model.Data;
import com.ruimin.helper.util.DtstUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.Icon;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 下午 07:24
 * @description jsp 跳转 jsp
 */
public class SnowJspLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken, PsiElement> {

    private boolean isButton = false;

    private final Map<String, List<XmlFile>> dtstMap = new HashMap<>();

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken && isTargetType((XmlToken) element);
    }

    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull XmlToken from) {
        if (isButton) {
            return processButton(from);
        } else {
            return processDtst(from);
        }
    }

    private Optional<? extends PsiElement[]> processButton(@NotNull XmlToken from) {
        ArrayList<PsiElement> psiElements = new ArrayList<>();
        XmlTag parent = (XmlTag) from.getParent();
        String dataset = parent.getAttributeValue(SnowPageConstants.BUTTON_ATTR_NAME_DATASET);
        String buttonId = parent.getAttributeValue(SnowPageConstants.BUTTON_ATTR_NAME_ID);
        if (StringUtils.isNotBlank(dataset) && StringUtils.isNotBlank(buttonId)) {
            List<XmlFile> xmlFiles = dtstMap.get(dataset);
            if (CollectionUtils.isNotEmpty(xmlFiles)){
                for (XmlFile xmlFile : xmlFiles) {
                    Data data = DtstUtils.getDataTagByDtstFile(xmlFile);
                    if (data != null) {
                        List<Commands> commandses = data.getCommandses();
                        for (Commands commands : commandses) {
                            List<Command> commandList = commands.getCommands();
                            for (Command command : commandList) {
                                if (buttonId.equals(command.getId().getValue())) {
                                    psiElements.add(command.getXmlElement());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(psiElements)) {
            return Optional.of(psiElements.toArray(new PsiElement[0]));
        }

        return Optional.empty();
    }

    private Optional<? extends PsiElement[]> processDtst(@NotNull XmlToken from) {
        XmlTag parent = (XmlTag) from.getParent();
        String id = parent.getAttributeValue(SnowPageConstants.DTST_ATTR_NAME_ID);
        String path = parent.getAttributeValue(SnowPageConstants.DTST_ATTR_NAME_PATH);
        List<XmlFile> dtst = DtstUtils.findDtstFileByPath(
            path, parent.getProject());
        if (dtst.isEmpty()) {
            return Optional.empty();
        }
        dtstMap.put(id, dtst);
        return Optional.of(dtst.toArray(new PsiElement[0]));
    }

    @Override
    public @NotNull Icon getIcon() {
        return SnowIcons.GO_DTST;
    }

    @Override
    public @NotNull String getTooltip(PsiElement array, @NotNull PsiElement target) {
        return "前往dtst";
    }


    /**
     * 是否为目标行
     */
    private boolean isTargetType(XmlToken token) {
        if (SnowPageConstants.SNOW_PAGE_DATASET_TAG_NAME.equals(token.getText())) {
            // 判断是开始标签，不然结束标签也会进入逻辑 影响结果
            PsiElement nextSibling = token.getNextSibling();
            isButton = false;
            return nextSibling instanceof PsiWhiteSpace && "<".equals(token.getPrevSibling().getText());
        }
        if (SnowPageConstants.SNOW_PAGE_BUTTON_TAG_NAME.equals(token.getText())) {
            // 判断是开始标签，不然结束标签也会进入逻辑 影响结果
            PsiElement nextSibling = token.getNextSibling();
            isButton = true;
            return nextSibling instanceof PsiWhiteSpace && "<".equals(token.getPrevSibling().getText());
        }
        return false;
    }

}
