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
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.legacy.unittest.rewriter.subquery;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import org.junit.Test;
import org.opensearch.sql.legacy.rewriter.subquery.NestedQueryContext;


public class NestedQueryContextTest {

    @Test
    public void isNested() {
        NestedQueryContext nestedQueryDetector = new NestedQueryContext();
        nestedQueryDetector.add(new SQLExprTableSource(new SQLIdentifierExpr("employee"), "e"));

        assertFalse(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("e"), "e1")));
        assertTrue(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("e.projects"), "p")));

        nestedQueryDetector.add(new SQLExprTableSource(new SQLIdentifierExpr("e.projects"), "p"));
        assertTrue(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("p"))));
    }

    @Test
    public void isNestedJoin() {
        NestedQueryContext nestedQueryDetector = new NestedQueryContext();
        SQLJoinTableSource joinTableSource = new SQLJoinTableSource();
        joinTableSource.setLeft(new SQLExprTableSource(new SQLIdentifierExpr("employee"), "e"));
        joinTableSource.setRight(new SQLExprTableSource(new SQLIdentifierExpr("e.projects"), "p"));
        joinTableSource.setJoinType(JoinType.COMMA);
        nestedQueryDetector.add(joinTableSource);

        assertFalse(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("e"), "e1")));
        assertTrue(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("e.projects"), "p")));
        assertTrue(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("p"))));
    }

    @Test
    public void notNested() {
        NestedQueryContext nestedQueryDetector = new NestedQueryContext();
        nestedQueryDetector.add(new SQLExprTableSource(new SQLIdentifierExpr("employee"), "e"));
        nestedQueryDetector.add(new SQLExprTableSource(new SQLIdentifierExpr("projects"), "p"));

        assertFalse(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("e"), "e1")));
        assertFalse(nestedQueryDetector.isNested(new SQLExprTableSource(new SQLIdentifierExpr("p"))));
    }
}
