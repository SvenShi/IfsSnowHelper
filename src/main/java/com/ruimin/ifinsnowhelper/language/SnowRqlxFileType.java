package com.ruimin.ifinsnowhelper.language;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.ui.DefaultIconDeferrer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/22 下午 08:07
 * @description 定义rqlx文件类型
 */
public class SnowRqlxFileType extends LanguageFileType {

    public static final SnowRqlxFileType INSTANCE = new SnowRqlxFileType();

    private SnowRqlxFileType() {
        super(XMLLanguage.INSTANCE);
    }

    @Override
    public @NonNls
    @NotNull String getName() {
        return "sql file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Sql file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "rqlx";
    }

    @Override
    public Icon getIcon() {
        return SnowIcons.LOGO;
    }
}
