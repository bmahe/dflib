package com.nhl.dflib.exp;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.LongStream;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumExp_ConditionTest {

    @Test
    public void testLT_IntDouble() {
        Condition c = $int("b").lt($double("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, -1,
                3., 4,
                3., 3);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testLE_LongInt() {
        Condition c = $long("a").le($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testGT_IntInt() {
        Condition c = $int("a").gt($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testGE_IntInt() {

        Condition c = $int("a").ge($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, true, false);
    }

    @Test
    public void testLT_LongLong_Primitive() {
        Condition c = $long("a").lt($long("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldLongStreamByRow(LongStream.of(2L, 1L, 3L, 4L));
        // sanity check of the test DataFrame
        Series<Long> a = df.getColumn("a");
        assertTrue(a instanceof LongSeries);

        Series<Long> b = df.getColumn("b");
        assertTrue(b instanceof LongSeries);

        // run and verify the calculation
        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true);
    }

    @Test
    public void testGT_DecimalDecimal() {
        Condition c = $decimal("a").gt($decimal("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.1"), new BigDecimal("1.0001"),
                new BigDecimal("3"), new BigDecimal("3"),
                new BigDecimal("1.1"), new BigDecimal("1.2"));

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testEQ_IntIntVal() {

        Condition c = $int("a").eq(3);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testEQ_IntLongVal() {

        Condition c = $int("a").eq(3L);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testEQ_LongIntVal() {

        Condition c = $long("a").eq(3);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testNE_LongIntVal() {

        Condition c = $long("a").ne(3);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testNE_LongIntBigDecimal() {

        Condition c = $long("a").ne(new BigDecimal("3"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }
}