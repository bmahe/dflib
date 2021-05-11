package com.nhl.dflib.exp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.agg.IntExpAggregator;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IntExpFactory extends NumericExpFactory {

    protected static Exp<Integer> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
            return (Exp<Integer>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new IntUnaryExp<>(nExp, UnaryExp.toSeriesOp(Number::intValue));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new IntUnaryExp<>(sExp, UnaryExp.toSeriesOp(Integer::parseInt));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Integer");
    }

    @Override
    public NumericExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("+",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 + n2),
                IntSeries::add);
    }

    @Override
    public NumericExp<?> subtract(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("-",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 - n2),
                IntSeries::subtract);
    }

    @Override
    public NumericExp<?> multiply(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("*",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 * n2),
                IntSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("/",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 / n2),
                IntSeries::divide);
    }

    @Override
    public NumericExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("%",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 % n2),
                IntSeries::mod);
    }

    @Override
    public NumericExp<BigDecimal> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnaryExp.toSeriesOp(i -> BigDecimal.valueOf((long) i).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericExp<Integer> sum(Exp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.sumInt());
    }

    @Override
    public NumericExp<?> min(Exp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.minInt());
    }

    @Override
    public NumericExp<?> max(Exp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.maxInt());
    }

    @Override
    public NumericExp<?> avg(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.averageDouble());
    }

    @Override
    public NumericExp<?> median(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.medianDouble());
    }

    @Override
    public SeriesCondition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinarySeriesCondition("<",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 < n2),
                IntSeries::lt);
    }

    @Override
    public SeriesCondition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinarySeriesCondition("<=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 <= n2),
                IntSeries::le);
    }

    @Override
    public SeriesCondition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinarySeriesCondition(">",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 > n2),
                IntSeries::gt);
    }

    @Override
    public SeriesCondition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinarySeriesCondition(">=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 >= n2),
                IntSeries::ge);
    }
}