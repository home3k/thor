/*
* Copyright 2014 51haoyayi.com Inc Limited.
* All rights reserved.
*/

package com.haoyayi.thor.service.dict;


import com.haoyayi.thor.api.dict.dto.${Model}TypeField;
import com.haoyayi.thor.repository.dict.${Model}Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ${Model}Service {

    @Autowired
    ${Model}Repository ${model}Repository;

    public Map<Long, Boolean> get${Model}Exist(Set<Long> ids) {

        Map<Long, Boolean> result = new HashMap<Long, Boolean>();

        Set<${Model}TypeField> fields = new HashSet<${Model}TypeField>();
        fields.add(${Model}TypeField.${pk});

        Map<Long, Map<${Model}TypeField, Object>> ideaCols = ${model}Repository
                .getModelField(ids, fields);

        for (Long id : ids) {
            if (ideaCols.containsKey(id)) {
                result.put(id, true);
            } else {
                result.put(id, false);
            }
        }
        return result;
    }
}
