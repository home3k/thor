package com.haoyayi.thor.api;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class MultiConditionPair {

	private List<ConditionPair> conditionPairs = new ArrayList<ConditionPair>();

	public List<ConditionPair> getConditionPairs() {
		return conditionPairs;
	}

	public void setConditionPairs(List<ConditionPair> conditionPairs) {
		this.conditionPairs = conditionPairs;
	}

}
