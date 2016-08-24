/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen;

import com.haoyayi.thor.bizgen.context.CodegenContextHolder;
import com.haoyayi.thor.bizgen.dal.DBDesc;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.sql.DataSource;
import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractCodegenerator {

    static final Log LOG = LogFactory.getLog(AbstractCodegenerator.class);

    @Autowired
    @Qualifier("dbthor_write")
    protected DataSource datasourcethor;

    @Autowired
    protected DBDesc dbConf;

    @Autowired
    @Qualifier("codeGenFreeMarkerConfigurer")
    private FreeMarkerConfigurer freeMarkerConfigurer;

    protected String getFtlPathPrefix() {
        if (getGenType()==GenType.dict) {
            return "dict/";
        } else {
            return "";
        }
    }

    protected String getFtlPathPrefix(String model) {
        if (getGenType()==GenType.dict) {
            return "dict/";
        } else {
            return model;
        }
    }

    public void generateCode(ModelContext context, Set<String> actions) throws Exception {
        if (getGenType()==GenType.dict) {
            for (ModelContext dictContext : context.getDict()) {
                String dictModel = context.getDictModel();
                if (dictModel.equalsIgnoreCase("all") || dictContext.getName().equalsIgnoreCase(dictModel)) {
                    _generateCode(dictContext, actions);
                }
            }
        } else {
            _generateCode(context, actions);
        }
    }

    public void _generateCode(ModelContext context, Set<String> actions) throws Exception {

    }

    protected GenType getGenType() {
        Object object = CodegenContextHolder.getInstance().getCodegenContext("genType");
        if (object == null ) {
            return GenType.model;
        } else {
            return (GenType)object;
        }

    }

    protected String getBackupBasePath(String base) {
        return "thor-bizgen/codebak/" + base;
    }


    protected abstract String getCodegenBasePath();


    protected <T> void generateFile(String base, String templateFilename, String targetFilename,
                                    Map<String, T> data, String resultdir) throws IOException, TemplateException {

        Configuration cfg = freeMarkerConfigurer.getConfiguration();

        cfg.clearTemplateCache();

        Template template = cfg.getTemplate(templateFilename, "UTF-8");

        String dir = "../";
        String outputDir = "../";
        if (resultdir == null || resultdir.equals("")) {
            dir = dir + getCodegenBasePath() + "/";
            outputDir = outputDir + getBackupBasePath(base) + "/";
        } else {
            dir = dir + getCodegenBasePath() + "/" + resultdir + "/";
            outputDir = outputDir + getBackupBasePath(base) + "/" + resultdir + "/";
        }

        new File(dir).mkdirs();
        new File(outputDir).mkdirs();

        File targetJavaFile = new File(dir + targetFilename);
        File outJavaFile = new File(outputDir + targetFilename);
        if (targetJavaFile.exists()) {
            System.out.println("==Backup java file from : " + targetJavaFile.getAbsolutePath() + " to : " + outJavaFile.getAbsoluteFile());
            FileCopyUtils.copy(targetJavaFile, outJavaFile);
        }

        System.out.println("==Generate java file: " + targetJavaFile.getAbsolutePath());
        Writer out = new OutputStreamWriter(new FileOutputStream(targetJavaFile), "UTF-8");

        template.process(data, out);
    }

}
