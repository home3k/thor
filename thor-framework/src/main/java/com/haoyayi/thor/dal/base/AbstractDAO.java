/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.dal.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.CollectionUtils;

import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.GroupFunc;
import com.haoyayi.thor.api.MultiConditionPair;
import com.haoyayi.thor.conf.BizConf;

/**
 * Desc:
 * User: home3k
 */
public abstract class AbstractDAO<T extends AbstractBo, C> implements InitializingBean {

    protected static Log logger = LogFactory.getLog(AbstractDAO.class);

    private final List<String> DB_COLUMNS = Collections.unmodifiableList(com.haoyayi.thor.dal.base.FieldQualifierUtils.getDBColumns(Arrays.asList(getAllColumns())));

    protected abstract C[] getAllColumns();

    protected abstract JdbcTemplate getJdbcTemplate();

    protected List<String> getAllDBColumns() {
        return DB_COLUMNS;
    }

    protected abstract String getTablename();

    protected Integer convert(Boolean value) {
        if (value == null) {
            return 0;
        }
        return value ? 1 : 0;
    }

    protected Object filter(Object value) {
        if (value instanceof Boolean) {
            return convert((Boolean) value);
        } else {
            return value;
        }
    }

    /**
     * 当前DAO的rowMapper
     *
     * @return
     */
    protected abstract RowMapper<T> getRowMapper();

    /**
     * 活的当前DAO的rowMapper
     *
     * @return
     */
    protected abstract RowMapper<Map<C, Object>> getRowMapper(Set<C> fields);

    /**
     * 获得主键id
     *
     * @return
     */
    protected abstract C getPk();

    /**
     * 基于条件进行删除
     *
     * @param conditions
     */
    protected void del(Map<C, Object> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(getTablename());
        List<String> params = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, params));
        sb.append(";");
        getJdbcTemplate().update(sb.toString(), params.toArray());
    }

    /**
     * 基于条件修改。
     *
     * @param conditions key 条件列， value 条件值
     * @param items      key 修改列， value 修改值
     */
    protected int mod(Map<C, Object> conditions, Map<C, Object> items) {
        if (CollectionUtils.isEmpty(conditions) || CollectionUtils.isEmpty(items)) {
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(getTablename());
        sb.append(" set ");
        List<String> setKeys = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();
        List<String> pairExps = new ArrayList<String>();
        for (C key : items.keySet()) {
            Object item = items.get(key);
            if (item instanceof ConditionPair) {
                ConditionPair pair = (ConditionPair) item;
                pairExps.add(pair.getFunc().getPairString(key, pair.getValue()));
                continue;
            }
            setKeys.add(com.haoyayi.thor.dal.base.FieldQualifierUtils.getDBColumnName(key));
            params.add(items.get(key));
        }
        if (setKeys.size() > 0) {
            sb.append(StringUtils.join(setKeys, " =?, "));
            sb.append(" =? ");
        }
        if (pairExps.size() > 0) {
            if (setKeys.size() > 0)
                sb.append(",");
            sb.append(StringUtils.join(pairExps, ","));
        }
        List<String> conditionParams = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, conditionParams));
        sb.append(";");
        params.addAll(conditionParams);
        return getJdbcTemplate().update(sb.toString(), params.toArray());
    }

    /**
     * @param bos
     * @param autoKey
     * @return
     */
    protected List<Long> add(List<T> bos, boolean autoKey) {
        List<Long> bolist = new ArrayList<Long>();
        if (CollectionUtils.isEmpty(bos)) {
            return bolist;
        }
        if (autoKey) {
            for (T bo : bos) {
                bolist.add(addSingleBoWithAutoKey(bo));
            }
            return bolist;
        } else {
            return addBatchWithoutAutoKey(bos);
        }
    }

    protected List<Long> add(List<T> bos) {
        return this.add(bos, false);
    }

    /**
     * @param bos
     */
    private List<Long> addBatchWithoutAutoKey(List<T> bos) {
        List<Long> result = new ArrayList<Long>();
        if (CollectionUtils.isEmpty(bos)) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(getTablename());
        sb.append("(" + StringUtils.join(getAllDBColumns(), ",") + ")");
        sb.append(" values ");
        String singelValue = "(" + StringUtils.join(Collections.nCopies(getAllColumns().length, "?").iterator(), ",")
                + ")";
        String values = StringUtils.join(Collections.nCopies(bos.size(), singelValue).iterator(), ",");
        sb.append(values);
        sb.append(";");
        List<Object> params = new ArrayList<Object>();
        for (T bo : bos) {
            for (C column : getAllColumns()) {
                try {
                    Object value = PropertyUtils.getProperty(bo, column.toString());
                    if (value instanceof Boolean) {
                        params.add(convert((Boolean) value));
                    } else {
                        params.add(value);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("fill add param error", e);
                }
            }
        }
        getJdbcTemplate().update(sb.toString(), params.toArray());
        for (T bo : bos) {
            result.add(bo.getPkid());
        }
        return result;
    }

    public List<Long> addBatchWithAutoKey(List<T> bos) {
        List<Long> result = new ArrayList<Long>();
        if (CollectionUtils.isEmpty(bos)) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(getTablename());

        List<C> addColumns = new ArrayList<C>();
        List<String> addDbColumns = new ArrayList<String>();
        for (C column : getAllColumns()) {
            if (!column.equals(getPk())) {
                addColumns.add(column);
                addDbColumns.add(FieldQualifierUtils.getDBColumnName(column));
            }
        }

        sb.append("(" + StringUtils.join(addDbColumns, ",") + ")");
        sb.append(" values ");
        String singelValue = "(" + StringUtils.join(Collections.nCopies(addColumns.size(), "?").iterator(), ",")
                + ")";
        String values = StringUtils.join(Collections.nCopies(bos.size(), singelValue).iterator(), ",");
        sb.append(values);
        sb.append(";");
        List<Object> params = new ArrayList<Object>();
        for (T bo : bos) {
            for (C column : addColumns) {
                try {
                    Object value = PropertyUtils.getProperty(bo, column.toString());
                    if (value instanceof Boolean) {
                        params.add(convert((Boolean) value));
                    } else {
                        params.add(value);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("fill add param error", e);
                }
            }
        }

        logger.info("add batch with autokey size: " + bos.size());
        // logger.info("Its sql: " + sb.toString());
        getJdbcTemplate().update(sb.toString(), params.toArray());
        for (T bo : bos) {
            result.add(bo.getPkid());
        }
        return result;
    }

    private Long addSingleBoWithAutoKey(T bo) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(getTablename());
        List<C> addColumns = new ArrayList<C>();
        List<String> addDbColumns = new ArrayList<String>();
        for (C column : getAllColumns()) {
            if (!column.equals(getPk())) {
                addColumns.add(column);
                addDbColumns.add(FieldQualifierUtils.getDBColumnName(column));
            }
        }
        sb.append("(" + StringUtils.join(addDbColumns, ",") + ")");
        sb.append(" values ");
        String singelValue = "(" + StringUtils.join(Collections.nCopies(addColumns.size(), "?").iterator(), ",")
                + ")";
        sb.append(singelValue);
        sb.append(";");
        final List<Object> params = new ArrayList<Object>();
        for (C column : addColumns) {
            try {
                Object value = PropertyUtils.getProperty(bo, column.toString());
                if (value instanceof Boolean) {
                    params.add(convert((Boolean) value));
                } else {
                    params.add(value);
                }
            } catch (Exception e) {
                throw new IllegalStateException("fill add param error", e);
            }
        }
        final String sql = sb.toString();
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < params.size(); i++)
                    StatementCreatorUtils.setParameterValue(ps, i + 1, SqlTypeValue.TYPE_UNKNOWN, params.get(i));
                return ps;
            }
        }, generatedKeyHolder);
        bo.setPkid(generatedKeyHolder.getKey().longValue());
        return generatedKeyHolder.getKey().longValue();
    }

    protected Map<Long, T> getBoById(Long userid, Set<Long> ids) {
        Map<Long, T> result = new HashMap<Long, T>();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        List<Long> idsList = new ArrayList<Long>(ids);
        for (int i = 0; i < idsList.size(); i += BizConf.DPL_SHARDING_THRESHOLD) {
            int endIndex = i + BizConf.DPL_SHARDING_THRESHOLD;
            if (endIndex > idsList.size()) {//
                endIndex = idsList.size();
            }
            List<Long> subIds = idsList.subList(i, endIndex);
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(StringUtils.join(getAllDBColumns(), ","));
            sb.append(" from ");
            sb.append(getTablename());
            sb.append(" where ");
            sb.append(getPk());
            sb.append(" in ");
            sb.append("(" + StringUtils.join(subIds, ",") + ")");
            sb.append(" and userid=?;");
            List<T> data = getJdbcTemplate().query(sb.toString(), getRowMapper(), new Object[]{userid});
            result.putAll(list2map(data));
        }
        return result;
    }

    /**
     * @param conditions
     * @return
     */
    protected Map<Long, T> getBoByBasicCondition(Map<C, Object> conditions) {
        return this.getBoByBasicCondition(conditions, null, null, null);
    }


    protected Map<Long, T> getBoByBasicCondition(Map<C, Object> conditions, Integer m, Integer n) {
        return this.getBoByBasicCondition(conditions, null, m, n);
    }

    protected Map<Long, T> getBoByBasicCondition(Map<C, Object> conditions, Map<C, Boolean> orderby, Integer m,
                                                 Integer n) {
        StringBuilder sb = new StringBuilder();
        List<String> param = new ArrayList<String>();
        sb.append(buildBasicSql());
        if (conditions != null && !conditions.isEmpty()) {
            sb.append(buildWhereSql(conditions, param));
        }
        if (orderby != null && !orderby.isEmpty()) {
            sb.append(" order by ");
            int index = 0;
            for (C field : orderby.keySet()) {
                index++;
                sb.append(field).append(orderby.get(field) ? " desc" : " asc");
                if (index == orderby.size()) {
                    sb.append(",");
                }
            }
            sb.append(" ");
        }
        if (m != null) {
            sb.append(" limit ");
            sb.append(m);
            if (n != null) { // limit m
                sb.append(",");
                sb.append(n);
            }
        }
        sb.append(";");
        List<T> data = getJdbcTemplate().query(sb.toString(), getRowMapper(), param.toArray());
        return list2map(data);
    }

    protected String buildBasicSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(getAllDBColumns(), ","));
        sb.append(" from ");
        sb.append(getTablename());
        return sb.toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected String buildCondition(String field, Object o, List<String> params) {
        if (o == null) {
            return buildNullCondition(field);
        } else if (o.getClass().isArray()) {
            return buildInCondition(field, (Object[]) o, params);
        } else if (o instanceof Collection) {
            return buildInCondition(field, (Collection) o, params);
        } else if (o instanceof ConditionPair) {
            return buildPairCondition(field, (ConditionPair) o);
        } else if (o instanceof MultiConditionPair) {
        	List<String> conditonStrings = new ArrayList<String>();
        	for (Object obj : ((MultiConditionPair) o).getConditionPairs()) {
        		ConditionPair conditionPair = (ConditionPair) obj;
        		conditonStrings.add(buildCondition(field, conditionPair, params));
        	}
        	return " (" + StringUtils.join(conditonStrings, " and ") + ") ";
        } else {
            return buildEquelsConditon(field, o, params);
        }
    }

    private String buildNullCondition(String field) {
        return field + " is null ";
    }

    private String buildPairCondition(String field, ConditionPair pair) {
        if (pair == null) {
            throw new IllegalArgumentException("condition field:" + field + "'s value is null");
        }
        return pair.getFunc().getPairString(field, pair.getValue());
    }

    private String buildEquelsConditon(String field, Object o, List<String> params) {
        if (o == null) {
            throw new IllegalArgumentException("condition field:" + field + "'s value is null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" =");
        if (o instanceof String) {
            //TODO
            sb.append(" ? ");
            params.add((String) o);
        } else {
            sb.append(filter(o).toString());
        }
        return sb.toString();
    }

    private String buildInCondition(String field, Collection<Object> inset, List<String> params) {
        if (CollectionUtils.isEmpty(inset)) {
            throw new IllegalArgumentException("condition field:" + field + "'s value is null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" in ");
        sb.append("(");
        List<String> inStr = new ArrayList<String>();
        for (Object o : inset) {
            if (o == null) {
                continue;
            }
            if (o instanceof String) {
                inStr.add(" ? ");
                params.add((String) o);
            } else {
                inStr.add(o.toString());
            }
        }
        sb.append(StringUtils.join(inStr, ","));
        sb.append(") ");
        return sb.toString();
    }

    protected String buildInCondition(String field, Object[] inarray, List<String> params) {
        if (inarray == null || inarray.length == 0) {
            throw new IllegalArgumentException("condition field:" + field + "'s value is null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" in ");
        sb.append("(");
        List<String> inStr = new ArrayList<String>();
        for (Object o : inarray) {
            if (o == null) {
                continue;
            }
            if (o instanceof String) {
                inStr.add(" ? ");
                params.add((String) o);
            } else {
                inStr.add(o.toString());
            }
        }
        sb.append(StringUtils.join(inStr, ","));
        sb.append(") ");
        return sb.toString();
    }

    protected Map<Long, T> list2map(List<T> boList) {
        Map<Long, T> id2bo = new HashMap<Long, T>();
        for (T bo : boList) {
            id2bo.put(bo.getPkid(), bo);
        }
        return id2bo;
    }

    protected Map<Long, Map<C, Object>> getItemsById(Set<Long> ids, Set<C> fields) {
        Map<C, Object> conditions = new HashMap<C, Object>();
        conditions.put(getPk(), ids);
        return getItemsByCondition(conditions, fields);
    }

    protected List<Map<String, Object>> getGroupResultByCondition(Map<C, Object> conditions, Set<C> groupByFields, Map<GroupFunc, C> groupFuncMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isEmpty(groupByFields) || CollectionUtils.isEmpty(groupFuncMap) || CollectionUtils.isEmpty(conditions)) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumnsAsColumn(groupByFields), ","));
        sb.append(",");
        List<String> funcSet = new ArrayList<String>();
        for (GroupFunc func : groupFuncMap.keySet()) {
            funcSet.add(func.getQueryString(FieldQualifierUtils.getDBColumnName(groupFuncMap.get(func))));
        }
        sb.append(StringUtils.join(funcSet, ","));
        sb.append(" from ");
        sb.append(getTablename());
        List<String> params = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, params));
        sb.append(" group by ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumns(groupByFields), ","));
        sb.append(";");
        List<Map<String, Object>> listMap = getJdbcTemplate().queryForList(sb.toString(), params.toArray());
        if (CollectionUtils.isEmpty(listMap)) {
            return result;
        } else {
            return listMap;
        }
    }

    protected Map<String, Object> getGroupResultByCondition(Map<C, Object> conditions, Map<GroupFunc, C> groupFuncMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (CollectionUtils.isEmpty(groupFuncMap) || CollectionUtils.isEmpty(conditions)) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        List<String> funcSet = new ArrayList<String>();
        for (GroupFunc func : groupFuncMap.keySet()) {
            funcSet.add(func.getQueryString(FieldQualifierUtils.getDBColumnName(groupFuncMap.get(func))));
        }
        sb.append(StringUtils.join(funcSet, ","));
        sb.append(" from ");
        sb.append(getTablename());
        List<String> params = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, params));
        sb.append(";");
        Map<String, Object> listMap = getJdbcTemplate().queryForMap(sb.toString(), params.toArray());
        if (CollectionUtils.isEmpty(listMap)) {
            return result;
        } else {
            return listMap;
        }
    }

    public Long getCountByCondition(Map<C, Object> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return 0l;
        }
        return this.getCountByCondition(conditions, getPk());
    }

    protected Long getCountByCondition(Map<C, Object> conditions, C valueField) {
        if (CollectionUtils.isEmpty(conditions)) {
            return 0l;
        }
        Map<GroupFunc, C> groupFuncs = new HashMap<GroupFunc, C>();
        groupFuncs.put(GroupFunc.COUNT, valueField);
        Map<String, Object> groupResult = this.getGroupResultByCondition(conditions, groupFuncs);
        if (CollectionUtils.isEmpty(conditions)) {
            return 0l;
        }
        return Long.parseLong(groupResult.get(GroupFunc.COUNT.getColumnString()).toString());
    }

    protected Map<Long, Long> getCountByCondition(Map<C, Object> conditions, C groupByField, C valueField) {
        Map<Long, Long> result = new HashMap<Long, Long>();
        Set<C> groupByFields = new HashSet<C>();
        groupByFields.add(groupByField);
        Map<GroupFunc, C> groupFuncs = new HashMap<GroupFunc, C>();
        groupFuncs.put(GroupFunc.COUNT, valueField);
        List<Map<String, Object>> listMap = this.getGroupResultByCondition(conditions, groupByFields, groupFuncs);
        if (CollectionUtils.isEmpty(listMap)) {
            return result;
        }
        for (Map<String, Object> item : listMap) {
            Long id = Long.parseLong(item.get(groupByField.toString()).toString());
            Long cnt = Long.parseLong(item.get(GroupFunc.COUNT.getColumnString()).toString());
            result.put(id, cnt);
        }
        return result;
    }

    protected Double getSumByCondition(Map<C, Object> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return 0d;
        }
        return this.getSumByCondition(conditions, getPk());
    }

    protected Double getSumByCondition(Map<C, Object> conditions, C valueField) {
        if (CollectionUtils.isEmpty(conditions)) {
            return 0d;
        }
        Map<GroupFunc, C> groupFuncs = new HashMap<GroupFunc, C>();
        groupFuncs.put(GroupFunc.SUM, valueField);
        Map<String, Object> groupResult = this.getGroupResultByCondition(conditions, groupFuncs);
        if (CollectionUtils.isEmpty(conditions)) {
            return 0d;
        }
        Object result = groupResult.get(GroupFunc.SUM.getColumnString());
        if (null == result) {
            return 0D;
        }
        return Double.parseDouble(result.toString());
    }

    protected Map<Long, Double> getSumByCondition(Map<C, Object> conditions, C groupByField, C valueField) {
        Map<Long, Double> result = new HashMap<Long, Double>();
        Set<C> groupByFields = new HashSet<C>();
        groupByFields.add(groupByField);
        Map<GroupFunc, C> groupFuncs = new HashMap<GroupFunc, C>();
        groupFuncs.put(GroupFunc.SUM, valueField);
        List<Map<String, Object>> listMap = this.getGroupResultByCondition(conditions, groupByFields, groupFuncs);
        if (CollectionUtils.isEmpty(listMap)) {
            return result;
        }
        for (Map<String, Object> item : listMap) {
            Long id = Long.parseLong(item.get(groupByField.toString()).toString());
            Object itemResult = item.get(GroupFunc.SUM.getColumnString());
            if (null == itemResult) {
                result.put(id, 0D);
            } else {
                Double sum = Double.parseDouble(itemResult.toString());
                result.put(id, sum);
            }
        }
        return result;
    }

    protected void deleteByWhereSql(String sql, List<Object> params) {

        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(getTablename());
        sb.append(" where ");
        sb.append(sql);
        sb.append(";");
        getJdbcTemplate().update(sb.toString(), params.toArray());

    }

    protected Map<Long, Map<C, Object>> getItemsByWhereSql(String sql, Set<C> fields, List<Object> params) {
        Map<Long, Map<C, Object>> result = new HashMap<Long, Map<C, Object>>();
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumns(fields), ","));
        sb.append(" from ");
        sb.append(getTablename());
        sb.append(" where ");
        sb.append(sql);
        sb.append(";");
        List<Map<C, Object>> data = getJdbcTemplate().query(sb.toString(), getRowMapper(fields), params.toArray());
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (Map<C, Object> row : data) {
            Long key = (Long) row.get(getPk());
            result.put(key, row);
        }
        return result;
    }

    protected Map<Long, Map<C, Object>> getItemsByCondition(Map<C, Object> conditions, Set<C> fields) {
        Map<Long, Map<C, Object>> result = new HashMap<Long, Map<C, Object>>();
        if (CollectionUtils.isEmpty(conditions) || CollectionUtils.isEmpty(fields)) {
            return result;
        }
        Set<C> fields4Query = new HashSet<C>(fields);
        boolean fillPkFiled = false;
        if (!fields4Query.contains(getPk())) {
            fillPkFiled = true;
            fields.add(getPk());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumns(fields), ","));
        sb.append(" from ");
        sb.append(getTablename());
        List<String> param = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, param));
        sb.append(";");
        List<Map<C, Object>> data = getJdbcTemplate().query(sb.toString(), getRowMapper(fields), param.toArray());

        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (Map<C, Object> row : data) {
            Long key = (Long) row.get(getPk());
            if (fillPkFiled) {
                row.remove(getPk());
            }
            result.put(key, row);
        }
        return result;
    }

    //order by, limit m, n
    protected Map<Long, Map<C, Object>> getItemsByCondition(Map<C, Object> conditions, Set<C> fields,
                                                            List<Pair<C, Boolean>> orderbys, Integer num, Integer offset) {
        Map<Long, Map<C, Object>> result = new LinkedHashMap<Long, Map<C, Object>>();
        if (CollectionUtils.isEmpty(conditions) || CollectionUtils.isEmpty(fields)) {
            return result;
        }
        Set<C> fields4Query = new HashSet<C>(fields);
        boolean fillPkFiled = false;
        if (!fields4Query.contains(getPk())) {
            fillPkFiled = true;
            fields.add(getPk());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumns(fields), ","));
        sb.append(" from ");
        sb.append(getTablename());
        List<String> param = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, param));
        if (orderbys != null) {
            //order by:
            sb.append(" order by ");
            boolean first = false;
            for (Pair<C, Boolean> pair : orderbys) {
                if (first) {
                    sb.append(", ");
                } else {
                    first = true;
                }
                sb.append(FieldQualifierUtils.getDBColumnName(pair.getKey()));
                sb.append(" ");
                sb.append(pair.getValue() ? " desc " : " asc ");
            }
        } else {
            sb.append(" order by ");
            sb.append(FieldQualifierUtils.getDBColumnName(getPk()));
            sb.append(" desc ");
        }
        //limit
        if (offset != null) {
            sb.append(" limit ").append(offset);
        }
        if (num!=null) {
            if (offset!=null) {
                sb.append(" , ").append(num);
            } else {
                sb.append(" limit ").append(num);
            }
        }
        sb.append(";");
        List<Map<C, Object>> data = getJdbcTemplate().query(sb.toString(), getRowMapper(fields), param.toArray());

        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (Map<C, Object> row : data) {
            Long key = (Long) row.get(getPk());
            if (fillPkFiled) {
                row.remove(getPk());
            }
            result.put(key, row);
        }
        return result;
    }

    /**
     * sorted, using order by, return type is LinkedHashMap
     */
    protected Map<Long, Map<C, Object>> getItemsByCondition(
            Map<C, Object> conditions, Set<C> fields, Boolean desc) {
        Map<Long, Map<C, Object>> result = new LinkedHashMap<Long, Map<C, Object>>();
        if (CollectionUtils.isEmpty(conditions) || CollectionUtils.isEmpty(fields)) {
            return result;
        }
        Set<C> fields4Query = new HashSet<C>(fields);
        boolean fillPkFiled = false;
        if (!fields4Query.contains(getPk())) {
            fillPkFiled = true;
            fields.add(getPk());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumns(fields), ","));
        sb.append(" from ");
        sb.append(getTablename());
        List<String> param = new ArrayList<String>();
        sb.append(buildWhereSql(conditions, param));
        //order by
        sb.append(" order by ");
        sb.append(FieldQualifierUtils.getDBColumnName(getPk()));
        sb.append(desc ? " DESC " : " ASC ");
        //end of order by
        sb.append(";");
        List<Map<C, Object>> data = getJdbcTemplate().query(sb.toString(), getRowMapper(fields), param.toArray());

        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        // the result is ordered
        for (Map<C, Object> row : data) {
            Long key = (Long) row.get(getPk());
            if (fillPkFiled) {
                row.remove(getPk());
            }
            result.put(key, row);
        }
        return result;
    }

    protected String buildWhereSql(Map<C, Object> conditions, List<String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(" where ");
        List<String> conditonStrings = new ArrayList<String>();
        for (C field : conditions.keySet()) {

            String conditon = buildCondition(FieldQualifierUtils.getDBColumnName(field), conditions.get(field), params);
            conditonStrings.add(conditon);
        }
        sb.append(StringUtils.join(conditonStrings, " and "));
        return sb.toString();
    }

    protected void modByRow(Map<Long, Map<C, Object>> id2items) {
        if (CollectionUtils.isEmpty(id2items)) {
            return;
        }
        for (Long id : id2items.keySet()) {
            Map<C, Object> conditions = new HashMap<C, Object>();
            conditions.put(getPk(), id);
            mod(conditions, id2items.get(id));
        }
    }


    protected void modByColumn(Set<Long> ids, Map<C, Object> items) {
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(items)) {
            return;
        }
        Map<C, Object> conditions = new HashMap<C, Object>();
        conditions.put(getPk(), ids);
        mod(conditions, items);
    }


    protected void mod(Set<Long> ids, C field, Object value) {
        Map<C, Object> items = new HashMap<C, Object>();
        items.put(field, value);
        this.modByColumn(ids, items);
    }

}

