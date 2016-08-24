/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.replace;

import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.GenType;
import com.haoyayi.thor.bizgen.context.CodegenContextHolder;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ReplaceCodegenerator implements CodeGenerator {

    private static final String MODEL_TYPE_PATH = "../thor-api/src/main/java/com/haoyayi/thor/api/ModelType.java";
    private static final String UCENTER_TYPE_PATH = "../thor-sal/src/main/java/com/haoyayi/thor/sal/ucenter/impl/UcenterConst.java";

    private static final String TOKEN_PLACEHOLDER = "//placeholder";
    private static final String TOKEN_DICT_PLACEHOLDER = "//dict-placeholder";


    private static final String ACTION_REPLACE = "replace";

    protected GenType getGenType() {
        Object object = CodegenContextHolder.getInstance().getCodegenContext("genType");
        if (object == null) {
            return GenType.model;
        } else {
            return (GenType) object;
        }

    }

    @Override
    public void generateCode(ModelContext context, Set<String> actions) throws Exception {

        if (!actions.contains(ACTION_REPLACE)) {
            return;
        }

        if (getGenType()==GenType.model) {
            String model = context.getName();

            String modelType = "    " + model + ",\n";

            replace(MODEL_TYPE_PATH, modelType,TOKEN_PLACEHOLDER);
            String ucenterType = "    public static final String " + model + " = \"" + model + "\";\n";
            replace(UCENTER_TYPE_PATH, ucenterType,TOKEN_PLACEHOLDER);

        } else {
            for (ModelContext sm : context.getDict()) {
                String model = sm.getName();
                String modelType = "    " + model + ",\n";

                replace(MODEL_TYPE_PATH, modelType,TOKEN_DICT_PLACEHOLDER);
            }
        }

    }

    private void replace(String path, String target, String token) {
        try {
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

            StringBuffer strBuffer = new StringBuffer();
            for (String temp = null; (temp = bufReader.readLine()) != null; temp = null) {

                if (temp.contains(token)) {
                    if (!strBuffer.toString().contains(target))
                        temp = temp.replace(token, target + token);
                }

                strBuffer.append(temp);
                strBuffer.append(System.getProperty("line.separator"));
            }
            bufReader.close();
            PrintWriter printWriter = new PrintWriter(path);
            printWriter.write(strBuffer.toString().toCharArray());
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
