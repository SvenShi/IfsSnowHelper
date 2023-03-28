package com.ruimin.sample.service;

import com.ruimin.sample.service.utils.DBDao;
import com.ruimin.sample.service.utils.DBDaos;
import java.util.HashMap;
import java.util.List;

public class TestService {

    public void test(){
        DBDao dao = DBDaos.newInstance();

        List<?> objects = dao.selectList("com.ruimin.sample.service.rqlx.test.selectTest",
            new HashMap<>());
    }

}
