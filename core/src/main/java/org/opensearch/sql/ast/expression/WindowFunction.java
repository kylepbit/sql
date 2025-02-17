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

package org.opensearch.sql.ast.expression;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.sql.ast.AbstractNodeVisitor;
import org.opensearch.sql.ast.Node;
import org.opensearch.sql.ast.tree.Sort.SortOption;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@RequiredArgsConstructor
@ToString
public class WindowFunction extends UnresolvedExpression {

  private final UnresolvedExpression function;
  private List<UnresolvedExpression> partitionByList;
  private List<Pair<SortOption, UnresolvedExpression>> sortList;

  @Override
  public List<? extends Node> getChild() {
    ImmutableList.Builder<UnresolvedExpression> children = ImmutableList.builder();
    children.add(function);
    children.addAll(partitionByList);
    sortList.forEach(pair -> children.add(pair.getRight()));
    return children.build();
  }

  @Override
  public <T, C> T accept(AbstractNodeVisitor<T, C> nodeVisitor, C context) {
    return nodeVisitor.visitWindowFunction(this, context);
  }

}
