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
 *
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.sql.expression.operator.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.opensearch.sql.data.type.ExprCoreType.BOOLEAN;
import static org.opensearch.sql.data.type.ExprCoreType.BYTE;
import static org.opensearch.sql.data.type.ExprCoreType.DATE;
import static org.opensearch.sql.data.type.ExprCoreType.DATETIME;
import static org.opensearch.sql.data.type.ExprCoreType.DOUBLE;
import static org.opensearch.sql.data.type.ExprCoreType.FLOAT;
import static org.opensearch.sql.data.type.ExprCoreType.INTEGER;
import static org.opensearch.sql.data.type.ExprCoreType.LONG;
import static org.opensearch.sql.data.type.ExprCoreType.SHORT;
import static org.opensearch.sql.data.type.ExprCoreType.STRING;
import static org.opensearch.sql.data.type.ExprCoreType.TIME;
import static org.opensearch.sql.data.type.ExprCoreType.TIMESTAMP;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opensearch.sql.data.model.ExprBooleanValue;
import org.opensearch.sql.data.model.ExprByteValue;
import org.opensearch.sql.data.model.ExprDateValue;
import org.opensearch.sql.data.model.ExprDatetimeValue;
import org.opensearch.sql.data.model.ExprDoubleValue;
import org.opensearch.sql.data.model.ExprFloatValue;
import org.opensearch.sql.data.model.ExprIntegerValue;
import org.opensearch.sql.data.model.ExprLongValue;
import org.opensearch.sql.data.model.ExprShortValue;
import org.opensearch.sql.data.model.ExprStringValue;
import org.opensearch.sql.data.model.ExprTimeValue;
import org.opensearch.sql.data.model.ExprTimestampValue;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.expression.DSL;
import org.opensearch.sql.expression.FunctionExpression;
import org.opensearch.sql.expression.config.ExpressionConfig;

class TypeCastOperatorTest {

  private final DSL dsl = new ExpressionConfig().dsl(new ExpressionConfig().functionRepository());

  private static Stream<ExprValue> numberData() {
    return Stream.of(new ExprByteValue(3), new ExprShortValue(3),
        new ExprIntegerValue(3), new ExprLongValue(3L), new ExprFloatValue(3f),
        new ExprDoubleValue(3D));
  }

  private static Stream<ExprValue> stringData() {
    return Stream.of(new ExprStringValue("strV"));
  }

  private static Stream<ExprValue> boolData() {
    return Stream.of(ExprBooleanValue.of(true));
  }

  private static Stream<ExprValue> date() {
    return Stream.of(new ExprDateValue("2020-12-24"));
  }

  private static Stream<ExprValue> time() {
    return Stream.of(new ExprTimeValue("01:01:01"));
  }

  private static Stream<ExprValue> timestamp() {
    return Stream.of(new ExprTimestampValue("2020-12-24 01:01:01"));
  }

  private static Stream<ExprValue> datetime() {
    return Stream.of(new ExprDatetimeValue("2020-12-24 01:01:01"));
  }

  @ParameterizedTest(name = "castString({0})")
  @MethodSource({"numberData", "stringData", "boolData", "date", "time", "timestamp", "datetime"})
  void castToString(ExprValue value) {
    FunctionExpression expression = dsl.castString(DSL.literal(value));
    assertEquals(STRING, expression.type());
    assertEquals(new ExprStringValue(value.value().toString()), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToByte({0})")
  @MethodSource({"numberData"})
  void castToByte(ExprValue value) {
    FunctionExpression expression = dsl.castByte(DSL.literal(value));
    assertEquals(BYTE, expression.type());
    assertEquals(new ExprByteValue(value.byteValue()), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToShort({0})")
  @MethodSource({"numberData"})
  void castToShort(ExprValue value) {
    FunctionExpression expression = dsl.castShort(DSL.literal(value));
    assertEquals(SHORT, expression.type());
    assertEquals(new ExprShortValue(value.shortValue()), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToInt({0})")
  @MethodSource({"numberData"})
  void castToInt(ExprValue value) {
    FunctionExpression expression = dsl.castInt(DSL.literal(value));
    assertEquals(INTEGER, expression.type());
    assertEquals(new ExprIntegerValue(value.integerValue()), expression.valueOf(null));
  }

  @Test
  void castStringToByte() {
    FunctionExpression expression = dsl.castByte(DSL.literal("100"));
    assertEquals(BYTE, expression.type());
    assertEquals(new ExprByteValue(100), expression.valueOf(null));
  }

  @Test
  void castStringToShort() {
    FunctionExpression expression = dsl.castShort(DSL.literal("100"));
    assertEquals(SHORT, expression.type());
    assertEquals(new ExprShortValue(100), expression.valueOf(null));
  }

  @Test
  void castStringToInt() {
    FunctionExpression expression = dsl.castInt(DSL.literal("100"));
    assertEquals(INTEGER, expression.type());
    assertEquals(new ExprIntegerValue(100), expression.valueOf(null));
  }

  @Test
  void castStringToIntException() {
    FunctionExpression expression = dsl.castInt(DSL.literal("invalid"));
    assertThrows(RuntimeException.class, () -> expression.valueOf(null));
  }

  @Test
  void castBooleanToByte() {
    FunctionExpression expression = dsl.castByte(DSL.literal(true));
    assertEquals(BYTE, expression.type());
    assertEquals(new ExprByteValue(1), expression.valueOf(null));

    expression = dsl.castByte(DSL.literal(false));
    assertEquals(BYTE, expression.type());
    assertEquals(new ExprByteValue(0), expression.valueOf(null));
  }

  @Test
  void castBooleanToShort() {
    FunctionExpression expression = dsl.castShort(DSL.literal(true));
    assertEquals(SHORT, expression.type());
    assertEquals(new ExprShortValue(1), expression.valueOf(null));

    expression = dsl.castShort(DSL.literal(false));
    assertEquals(SHORT, expression.type());
    assertEquals(new ExprShortValue(0), expression.valueOf(null));
  }

  @Test
  void castBooleanToInt() {
    FunctionExpression expression = dsl.castInt(DSL.literal(true));
    assertEquals(INTEGER, expression.type());
    assertEquals(new ExprIntegerValue(1), expression.valueOf(null));

    expression = dsl.castInt(DSL.literal(false));
    assertEquals(INTEGER, expression.type());
    assertEquals(new ExprIntegerValue(0), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToLong({0})")
  @MethodSource({"numberData"})
  void castToLong(ExprValue value) {
    FunctionExpression expression = dsl.castLong(DSL.literal(value));
    assertEquals(LONG, expression.type());
    assertEquals(new ExprLongValue(value.longValue()), expression.valueOf(null));
  }

  @Test
  void castStringToLong() {
    FunctionExpression expression = dsl.castLong(DSL.literal("100"));
    assertEquals(LONG, expression.type());
    assertEquals(new ExprLongValue(100), expression.valueOf(null));
  }

  @Test
  void castStringToLongException() {
    FunctionExpression expression = dsl.castLong(DSL.literal("invalid"));
    assertThrows(RuntimeException.class, () -> expression.valueOf(null));
  }

  @Test
  void castBooleanToLong() {
    FunctionExpression expression = dsl.castLong(DSL.literal(true));
    assertEquals(LONG, expression.type());
    assertEquals(new ExprLongValue(1), expression.valueOf(null));

    expression = dsl.castLong(DSL.literal(false));
    assertEquals(LONG, expression.type());
    assertEquals(new ExprLongValue(0), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToFloat({0})")
  @MethodSource({"numberData"})
  void castToFloat(ExprValue value) {
    FunctionExpression expression = dsl.castFloat(DSL.literal(value));
    assertEquals(FLOAT, expression.type());
    assertEquals(new ExprFloatValue(value.floatValue()), expression.valueOf(null));
  }

  @Test
  void castStringToFloat() {
    FunctionExpression expression = dsl.castFloat(DSL.literal("100.0"));
    assertEquals(FLOAT, expression.type());
    assertEquals(new ExprFloatValue(100.0), expression.valueOf(null));
  }

  @Test
  void castStringToFloatException() {
    FunctionExpression expression = dsl.castFloat(DSL.literal("invalid"));
    assertThrows(RuntimeException.class, () -> expression.valueOf(null));
  }

  @Test
  void castBooleanToFloat() {
    FunctionExpression expression = dsl.castFloat(DSL.literal(true));
    assertEquals(FLOAT, expression.type());
    assertEquals(new ExprFloatValue(1), expression.valueOf(null));

    expression = dsl.castFloat(DSL.literal(false));
    assertEquals(FLOAT, expression.type());
    assertEquals(new ExprFloatValue(0), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToDouble({0})")
  @MethodSource({"numberData"})
  void castToDouble(ExprValue value) {
    FunctionExpression expression = dsl.castDouble(DSL.literal(value));
    assertEquals(DOUBLE, expression.type());
    assertEquals(new ExprDoubleValue(value.doubleValue()), expression.valueOf(null));
  }

  @Test
  void castStringToDouble() {
    FunctionExpression expression = dsl.castDouble(DSL.literal("100.0"));
    assertEquals(DOUBLE, expression.type());
    assertEquals(new ExprDoubleValue(100), expression.valueOf(null));
  }

  @Test
  void castStringToDoubleException() {
    FunctionExpression expression = dsl.castDouble(DSL.literal("invalid"));
    assertThrows(RuntimeException.class, () -> expression.valueOf(null));
  }

  @Test
  void castBooleanToDouble() {
    FunctionExpression expression = dsl.castDouble(DSL.literal(true));
    assertEquals(DOUBLE, expression.type());
    assertEquals(new ExprDoubleValue(1), expression.valueOf(null));

    expression = dsl.castDouble(DSL.literal(false));
    assertEquals(DOUBLE, expression.type());
    assertEquals(new ExprDoubleValue(0), expression.valueOf(null));
  }

  @ParameterizedTest(name = "castToBoolean({0})")
  @MethodSource({"numberData"})
  void castToBoolean(ExprValue value) {
    FunctionExpression expression = dsl.castBoolean(DSL.literal(value));
    assertEquals(BOOLEAN, expression.type());
    assertEquals(ExprBooleanValue.of(true), expression.valueOf(null));
  }

  @Test
  void castZeroToBoolean() {
    FunctionExpression expression = dsl.castBoolean(DSL.literal(0));
    assertEquals(BOOLEAN, expression.type());
    assertEquals(ExprBooleanValue.of(false), expression.valueOf(null));
  }

  @Test
  void castStringToBoolean() {
    FunctionExpression expression = dsl.castBoolean(DSL.literal("True"));
    assertEquals(BOOLEAN, expression.type());
    assertEquals(ExprBooleanValue.of(true), expression.valueOf(null));
  }

  @Test
  void castBooleanToBoolean() {
    FunctionExpression expression = dsl.castBoolean(DSL.literal(true));
    assertEquals(BOOLEAN, expression.type());
    assertEquals(ExprBooleanValue.of(true), expression.valueOf(null));
  }

  @Test
  void castToDate() {
    FunctionExpression expression = dsl.castDate(DSL.literal("2012-08-07"));
    assertEquals(DATE, expression.type());
    assertEquals(new ExprDateValue("2012-08-07"), expression.valueOf(null));

    expression = dsl.castDate(DSL.literal(new ExprDatetimeValue("2012-08-07 01:01:01")));
    assertEquals(DATE, expression.type());
    assertEquals(new ExprDateValue("2012-08-07"), expression.valueOf(null));

    expression = dsl.castDate(DSL.literal(new ExprTimestampValue("2012-08-07 01:01:01")));
    assertEquals(DATE, expression.type());
    assertEquals(new ExprDateValue("2012-08-07"), expression.valueOf(null));

    expression = dsl.castDate(DSL.literal(new ExprDateValue("2012-08-07")));
    assertEquals(DATE, expression.type());
    assertEquals(new ExprDateValue("2012-08-07"), expression.valueOf(null));
  }

  @Test
  void castToTime() {
    FunctionExpression expression = dsl.castTime(DSL.literal("01:01:01"));
    assertEquals(TIME, expression.type());
    assertEquals(new ExprTimeValue("01:01:01"), expression.valueOf(null));

    expression = dsl.castTime(DSL.literal(new ExprDatetimeValue("2012-08-07 01:01:01")));
    assertEquals(TIME, expression.type());
    assertEquals(new ExprTimeValue("01:01:01"), expression.valueOf(null));

    expression = dsl.castTime(DSL.literal(new ExprTimestampValue("2012-08-07 01:01:01")));
    assertEquals(TIME, expression.type());
    assertEquals(new ExprTimeValue("01:01:01"), expression.valueOf(null));

    expression = dsl.castTime(DSL.literal(new ExprTimeValue("01:01:01")));
    assertEquals(TIME, expression.type());
    assertEquals(new ExprTimeValue("01:01:01"), expression.valueOf(null));
  }

  @Test
  void castToTimestamp() {
    FunctionExpression expression = dsl.castTimestamp(DSL.literal("2012-08-07 01:01:01"));
    assertEquals(TIMESTAMP, expression.type());
    assertEquals(new ExprTimestampValue("2012-08-07 01:01:01"), expression.valueOf(null));

    expression = dsl.castTimestamp(DSL.literal(new ExprDatetimeValue("2012-08-07 01:01:01")));
    assertEquals(TIMESTAMP, expression.type());
    assertEquals(new ExprTimestampValue("2012-08-07 01:01:01"), expression.valueOf(null));

    expression = dsl.castTimestamp(DSL.literal(new ExprTimestampValue("2012-08-07 01:01:01")));
    assertEquals(TIMESTAMP, expression.type());
    assertEquals(new ExprTimestampValue("2012-08-07 01:01:01"), expression.valueOf(null));
  }

  @Test
  void castToDatetime() {
    FunctionExpression expression = dsl.castDatetime(DSL.literal("2012-08-07 01:01:01"));
    assertEquals(DATETIME, expression.type());
    assertEquals(new ExprDatetimeValue("2012-08-07 01:01:01"), expression.valueOf(null));

    expression = dsl.castDatetime(DSL.literal(new ExprTimestampValue("2012-08-07 01:01:01")));
    assertEquals(DATETIME, expression.type());
    assertEquals(new ExprDatetimeValue("2012-08-07 01:01:01"), expression.valueOf(null));

    expression = dsl.castDatetime(DSL.literal(new ExprDateValue("2012-08-07")));
    assertEquals(DATETIME, expression.type());
    assertEquals(new ExprDatetimeValue("2012-08-07 00:00:00"), expression.valueOf(null));
  }

}
