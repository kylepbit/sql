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

package org.opensearch.sql.data.model;

import java.util.Objects;
import org.opensearch.sql.data.type.ExprCoreType;
import org.opensearch.sql.data.type.ExprType;

/**
 * Expression Missing Value.
 */
public class ExprMissingValue extends AbstractExprValue {
  private static final ExprMissingValue instance = new ExprMissingValue();

  private ExprMissingValue() {
  }

  public static ExprMissingValue of() {
    return instance;
  }

  @Override
  public Object value() {
    return null;
  }

  @Override
  public ExprType type() {
    return ExprCoreType.UNDEFINED;
  }

  @Override
  public boolean isMissing() {
    return true;
  }

  @Override
  public int compare(ExprValue other) {
    throw new IllegalStateException(String.format("[BUG] Unreachable, Comparing with MISSING is "
        + "undefined"));
  }

  /**
   * Missing value is equal to Missing value.
   * Notes, this function should only used for Java Object Compare.
   */
  @Override
  public boolean equal(ExprValue other) {
    return other.isMissing();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode("MISSING");
  }

  @Override
  public String toString() {
    return "MISSING";
  }
}
