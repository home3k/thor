/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import com.haoyayi.thor.factory.ModelFactory;
import com.haoyayi.thor.query.QueryFacade;
import com.haoyayi.thor.repository.ModelConditionQueryRepository;
import com.haoyayi.thor.repository.ModelRepository;
import com.haoyayi.thor.validate.ConditionValidator;
import com.haoyayi.thor.validate.Validator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ProcessorContext implements ApplicationContextAware {

    protected static final String PROCESSOR_SUFFIX_VALIDATOR = "Validator";

    protected static final String PROCESSOR_SUFFIX_CONDITION_VALIDATOR = "Validator";

    protected static final String PROCESSOR_SUFFIX_FACTORY = "ModelFactory";

    protected static final String PROCESSOR_SUFFIX_REPOSITORY = "Repository";

    protected static final String PROCESSOR_SUFFIX_CONDITION_REPOSITORY = "Repository";

    protected static final String PROCESSOR_SUFFIX_COLUMN = "ConvertBiz";

    protected static final String PROCESSOR_SUFFIX_QUERY = "QueryBiz";

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Validator getValidator(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_VALIDATOR, Validator.class);
    }

    public ModelRepository getModelRepository(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_CONDITION_REPOSITORY, ModelRepository.class);
    }

    public ModelFactory getFactory(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_FACTORY, ModelFactory.class);
    }

    public ColumnProcessor getConverter(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_COLUMN, ColumnProcessor.class);
    }

    public ModelConditionQueryRepository getQueryRepository(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_REPOSITORY, ModelConditionQueryRepository.class);
    }

    public ConditionValidator getConditionValidator(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_CONDITION_VALIDATOR, ConditionValidator.class);
    }

    public QueryFacade getQuery(String model) {
        return applicationContext.getBean(model + PROCESSOR_SUFFIX_QUERY, QueryFacade.class);
    }

}
