/*
 * SPDX-License-Identifier: Apache-2.0
 *
 *  The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 *
 */

package org.opensearch.sql.expression.span;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.opensearch.sql.data.type.ExprCoreType.DOUBLE;
import static org.opensearch.sql.data.type.ExprCoreType.INTEGER;
import static org.opensearch.sql.data.type.ExprCoreType.TIMESTAMP;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.opensearch.sql.data.model.ExprValueUtils;
import org.opensearch.sql.expression.DSL;
import org.opensearch.sql.expression.ExpressionTestBase;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SpanExpressionTest extends ExpressionTestBase {
  @Test
  void span() {
    SpanExpression span = DSL.span(DSL.ref("integer_value", INTEGER), DSL.literal(1), "");
    assertEquals(INTEGER, span.type());
    assertEquals(ExprValueUtils.integerValue(1), span.valueOf(valueEnv()));

    span = DSL.span(DSL.ref("integer_value", INTEGER), DSL.literal(1.5), "");
    assertEquals(DOUBLE, span.type());
    assertEquals(ExprValueUtils.doubleValue(1.5), span.valueOf(valueEnv()));

    span = DSL.span(DSL.ref("double_value", DOUBLE), DSL.literal(1), "");
    assertEquals(DOUBLE, span.type());
    assertEquals(ExprValueUtils.doubleValue(1.0), span.valueOf(valueEnv()));

    span = DSL.span(DSL.ref("timestamp_value", TIMESTAMP), DSL.literal(1), "d");
    assertEquals(TIMESTAMP, span.type());
    assertEquals(ExprValueUtils.integerValue(1), span.valueOf(valueEnv()));
  }

}
