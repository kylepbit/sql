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

package org.opensearch.sql.planner;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.planner.logical.LogicalPlan;
import org.opensearch.sql.planner.logical.LogicalPlanNodeVisitor;
import org.opensearch.sql.planner.logical.LogicalRelation;
import org.opensearch.sql.planner.optimizer.LogicalPlanOptimizer;
import org.opensearch.sql.planner.physical.PhysicalPlan;
import org.opensearch.sql.storage.StorageEngine;
import org.opensearch.sql.storage.Table;

/**
 * Planner that plans and chooses the optimal physical plan.
 */
@RequiredArgsConstructor
public class Planner {

  /**
   * Storage engine.
   */
  private final StorageEngine storageEngine;

  private final LogicalPlanOptimizer logicalOptimizer;

  /**
   * Generate optimal physical plan for logical plan. If no table involved,
   * translate logical plan to physical by default implementor.
   * TODO: for now just delegate entire logical plan to storage engine.
   *
   * @param plan logical plan
   * @return optimal physical plan
   */
  public PhysicalPlan plan(LogicalPlan plan) {
    String tableName = findTableName(plan);
    if (isNullOrEmpty(tableName)) {
      return plan.accept(new DefaultImplementor<>(), null);
    }

    Table table = storageEngine.getTable(tableName);
    return table.implement(
        table.optimize(optimize(plan)));
  }

  private String findTableName(LogicalPlan plan) {
    return plan.accept(new LogicalPlanNodeVisitor<String, Object>() {

      @Override
      public String visitNode(LogicalPlan node, Object context) {
        List<LogicalPlan> children = node.getChild();
        if (children.isEmpty()) {
          return "";
        }
        return children.get(0).accept(this, context);
      }

      @Override
      public String visitRelation(LogicalRelation node, Object context) {
        return node.getRelationName();
      }
    }, null);
  }

  private LogicalPlan optimize(LogicalPlan plan) {
    return logicalOptimizer.optimize(plan);
  }
}
