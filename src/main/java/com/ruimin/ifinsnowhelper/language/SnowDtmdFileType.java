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
 * @date 2022/7/22 下午 08:08
 * @description 定义dtmd文件类型
 */
public class SnowDtmdFileType extends LanguageFileType {

    public static final SnowDtmdFileType INSTANCE = new SnowDtmdFileType();

    private SnowDtmdFileType() {
        super(XMLLanguage.INSTANCE);
    }

    @Override
    public @NonNls
    @NotNull String getName() {
        return "dataset mapper file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Define dataset mapper file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "dtmd";
    }

    @Override
    public Icon getIcon() {
        return SnowIcons.LOGO;
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "dtmd";
    }
}
