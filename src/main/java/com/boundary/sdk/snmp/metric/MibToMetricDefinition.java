// Copyright 2014-2015 Boundary, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.boundary.sdk.snmp.metric;

import java.util.List;

import org.snmp4j.smi.OID;

import com.boundary.plugin.sdk.MetricAggregate;
import com.boundary.plugin.sdk.MetricDefinition;
import com.boundary.plugin.sdk.MetricDefinitionBuilder;
import com.boundary.plugin.sdk.MetricDefinitionList;
import com.boundary.plugin.sdk.MetricUnit;
import com.snmp4j.smi.SmiModule;
import com.snmp4j.smi.SmiObject;

public class MibToMetricDefinition extends MibTransformBase {
	
	MetricDefinitionList metricDefinitionList = new MetricDefinitionList();
	
	public MibToMetricDefinition() {
		metricDefinitionList = new MetricDefinitionList();

	}

	@Override
	public void transform(SmiModule module, SmiObject object) {
		MetricDefinitionBuilder builder = new MetricDefinitionBuilder();
		List<MetricDefinition> list = metricDefinitionList.getResult();

		OID oid = object.getOID();
		if (standardFilterCriteria(module,object)) {
			if (oid != null) {
				String objectName = object.getObjectName();
				String moduleName = module.getModuleName();

				String metricId = oidToMetricId(moduleName,objectName);
				String displayName = oidToDisplayName(moduleName,objectName);
				String shortDisplayName = oidToShortDisplayName(moduleName,objectName);
				String description = oidToDescription(object.getDescription());
				
				builder.setName(metricId);
				builder.setDisplayName(displayName);
				builder.setDisplayNameShort(shortDisplayName);
				builder.setDescription(description);
				builder.setDefaultResolutionMS(5000);
				builder.setDefaultAggregate(MetricAggregate.avg);
				builder.setUnit(MetricUnit.number);
				list.add(builder.build());
			} else {
				System.err.printf("oid is null: %s%n",object.getObjectName());
			}
		} 
	}
	
	@Override
	public void beginTransform() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endTransform() {
		convertToJson(metricDefinitionList);
	}


	@Override
	public void beginModule(SmiModule module) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endModule(SmiModule module) {
		// TODO Auto-generated method stub
		
	}
	
}
