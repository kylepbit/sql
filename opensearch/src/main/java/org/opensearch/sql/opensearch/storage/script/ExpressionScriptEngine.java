/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.sql.opensearch.storage.script;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.opensearch.script.AggregationScript;
import org.opensearch.script.FilterScript;
import org.opensearch.script.ScriptContext;
import org.opensearch.script.ScriptEngine;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.opensearch.storage.script.aggregation.ExpressionAggregationScriptFactory;
import org.opensearch.sql.opensearch.storage.script.filter.ExpressionFilterScriptFactory;
import org.opensearch.sql.opensearch.storage.serialization.ExpressionSerializer;

/**
 * Custom expression script engine that supports using core engine expression code in DSL
 * as a new script language just like built-in Painless language.
 */
@RequiredArgsConstructor
public class ExpressionScriptEngine implements ScriptEngine {

  /**
   * Expression script language name.
   */
  public static final String EXPRESSION_LANG_NAME = "opensearch_query_expression";

  /**
   * All supported script contexts and function to create factory from expression.
   */
  private static final Map<ScriptContext<?>, Function<Expression, Object>> CONTEXTS =
      new ImmutableMap.Builder<ScriptContext<?>, Function<Expression, Object>>()
          .put(FilterScript.CONTEXT, ExpressionFilterScriptFactory::new)
          .put(AggregationScript.CONTEXT, ExpressionAggregationScriptFactory::new)
          .build();

  /**
   * Expression serializer that (de-)serializes expression.
   */
  private final ExpressionSerializer serializer;

  @Override
  public String getType() {
    return EXPRESSION_LANG_NAME;
  }

  @Override
  public <T> T compile(String scriptName,
                       String scriptCode,
                       ScriptContext<T> context,
                       Map<String, String> params) {
    /*
     * Note that in fact the expression source is already compiled in query engine.
     * The "code" is actually a serialized expression tree by our serializer.
     * Therefore the compilation here is simply to deserialize the expression tree.
     */
    Expression expression = serializer.deserialize(scriptCode);

    if (CONTEXTS.containsKey(context)) {
      return context.factoryClazz.cast(CONTEXTS.get(context).apply(expression));
    }
    throw new IllegalStateException(String.format("Script context is currently not supported: "
        + "all supported contexts [%s], given context [%s] ", CONTEXTS, context));
  }

  @Override
  public Set<ScriptContext<?>> getSupportedContexts() {
    return CONTEXTS.keySet();
  }

}
