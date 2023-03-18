package com.ruimin.helper.common.listener;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager.PostStartupActivity;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.rqlx.constans.RqlxConstants;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/15 上午 03:33
 * @description
 */
public class ProjectStartupListener extends PostStartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        associateFileType();
    }


    /**
     * 关联文件类型
     */
    private void associateFileType() {

        ApplicationManager.getApplication().invokeLater(()->{
            WriteAction.run(()->{
                FileTypeManager typeManager = FileTypeManager.getInstance();
                FileType rqlxFileType = typeManager.getFileTypeByExtension(RqlxConstants.RQLX_FILE_EXTENSION);
                FileType dtstFileType = typeManager.getFileTypeByExtension(DataSetConstants.DTST_FILE_EXTENSION);
                FileType dtmdFileType = typeManager.getFileTypeByExtension(DataSetConstants.DTMD_FILE_EXTENSION);
                if (!(rqlxFileType instanceof XmlFileType)) {
                    typeManager.associateExtension(XmlFileType.INSTANCE, RqlxConstants.RQLX_FILE_EXTENSION);
                }
                if (!(dtstFileType instanceof XmlFileType)) {
                    typeManager.associateExtension(XmlFileType.INSTANCE, DataSetConstants.DTST_FILE_EXTENSION);
                }
                if (!(dtmdFileType instanceof XmlFileType)) {
                    typeManager.associateExtension(XmlFileType.INSTANCE, DataSetConstants.DTMD_FILE_EXTENSION);
                }
            });
        });
    }
}
