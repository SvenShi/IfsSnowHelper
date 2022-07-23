package com.ruimin.ifinsnowhelper.language;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/22 下午 05:35
 * @description 定义dtst文件类型
 */
public class SnowDtstFileType extends LanguageFileType {
    public static final SnowDtstFileType INSTANCE = new SnowDtstFileType();

    private SnowDtstFileType() {
        super(XMLLanguage.INSTANCE);
    }

    @Override
    public @NonNls
    @NotNull String getName() {
        return "dataset file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Define dataset file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "dtst";
    }

    @Override
    public Icon getIcon() {
        return SnowIcons.LOGO;
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "dtst";
    }
}
