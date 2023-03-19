package com.ruimin.helper.js.constants;

import java.util.Set;

public interface JSConstants {

    Set<String> JS_METHODS = Set.of("setValue", "getValue", "getString", "getViewString",
        "getCurValue", "setCurValue", "getOldValue", "setOldValue", "getJsonValue", "setFieldReadOnly",
        "setFieldInvalid", "setFieldRequired", "setFieldRule", "setFieldHidden");

}
