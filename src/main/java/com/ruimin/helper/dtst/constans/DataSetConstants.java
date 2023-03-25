package com.ruimin.helper.dtst.constans;

import com.google.common.collect.Sets;
import com.ruimin.helper.common.constants.CommonConstants;
import java.util.Set;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/7/23 上午 12:47
 * @description 常量类
 */
public interface DataSetConstants {

    // dtst文件后缀
    String DTST_FILE_EXTENSION = "dtst";
    String DTMD_FILE_EXTENSION = "dtmd";
    String DTST_FILE_EXTENSION_DOT = CommonConstants.DOT_SEPARATE + DTST_FILE_EXTENSION;


    String XML_TAG_FLOWID_ATTRIBUTE_NAME = "flowid";
    String XML_TAG_METHOD_ATTRIBUTE_NAME = "method";

    String XML_TAG_DATASOURCE_ATTRIBUTE_NAME = "datasource";
    Set<String> NOT_IN_DATASOURCE_TAG = Sets.newHashSet("LIST", "DDIC");

}
