package com.ruimin.helper.dtst.dom.description;

import com.intellij.util.xml.DomFileDescription;
import com.ruimin.helper.dtst.dom.model.Data;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 01:09
 * @description dtst 文件属性提示
 */
public class DataSetDescription extends DomFileDescription<Data> {

    public static final String [] HTTP_DTST_DTD =
            new String[]{"http://www.rmitec.cn/dtst-mapper","http://rmitec.cn/dtst-mapper"};

    /**
     *  向idea注册一个dtst的description
     */
    public DataSetDescription() {
        super(Data.class,"Data");
    }


    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy("DtstXml",HTTP_DTST_DTD);
    }
}
