package com.chj.gr.bdd;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.chj.gr.bdd.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, 
			value = "pretty, html:target/cucumber/gr-order-product-category-customer-bulk-operations-demo.html"
					    + ", json:target/cucumber/json/gr-order-product-category-customer-bulk-operations-demo.json")
public class CucumberBulkOrderTest {
}