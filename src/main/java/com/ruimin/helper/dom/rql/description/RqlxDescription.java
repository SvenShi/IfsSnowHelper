package com.ruimin.helper.dom.rql.description;

import com.intellij.util.xml.DomFileDescription;
import com.ruimin.helper.dom.dtst.model.Data;
import com.ruimin.helper.dom.rql.model.Mapper;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 01:09
 * @description dtst 文件属性提示
 */
public class RqlxDescription extends DomFileDescription<Mapper> {

    public static final String [] HTTP_DTST_DTD =
            new String[]{"http://sqlmap.rql.org/rql-mapper"};

    /**
     *  向idea注册一个dtst的description
     */
    public RqlxDescription() {
        super(Mapper.class,"mapper");
    }


    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy("RqlxXml",HTTP_DTST_DTD);
    }
}
