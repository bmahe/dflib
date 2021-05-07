package com.nhl.dflib;

import com.nhl.dflib.aggregate.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Defines an aggregation operation to produce a single value from a DataFrame. Behind the scenes often this operation
 * works on a single column of data and is a facade to {@link SeriesAggregator}.
 */
public interface Aggregator<T> {

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new Aggregator
     */
    static <T> Aggregator<T> first(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.first(),
                (SeriesExp<T>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     */
    static <T> Aggregator<T> first(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.first(),
                (SeriesExp<T>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * Creates an aggregator to count DataFrame rows.
     *
     * @since 0.6
     */
    static Aggregator<Long> countLong() {
        return new LongCountAggregator("_long_count");
    }

    /**
     * Creates an aggregator to count DataFrame rows.
     *
     * @since 0.6
     */
    static Aggregator<Integer> countInt() {
        return new IntCountAggregator("_int_count");
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> averageDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.averageDouble(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> averageDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.averageDouble(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> medianDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.medianDouble(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> medianDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.medianDouble(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Long> sumLong(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumLong(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Long> sumLong(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumLong(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Integer> sumInt(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumInt(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Integer> sumInt(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumInt(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    static Aggregator<Double> sumDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDouble(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> column
        );
    }

    static Aggregator<Double> sumDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDouble(),
                (SeriesExp<Number>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.11
     */
    static Aggregator<BigDecimal> sumDecimal(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDecimal(),
                (SeriesExp<BigDecimal>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.11
     */
    static Aggregator<BigDecimal> sumDecimal(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDecimal(),
                (SeriesExp<BigDecimal>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.11
     */
    static Aggregator<BigDecimal> sumDecimal(String column, int resultScale, RoundingMode resultRoundingMode) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDecimal(resultScale, resultRoundingMode),
                (SeriesExp<BigDecimal>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.11
     */
    static Aggregator<BigDecimal> sumDecimal(int column, int resultScale, RoundingMode resultRoundingMode) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDecimal(resultScale, resultRoundingMode),
                (SeriesExp<BigDecimal>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Aggregator<T> max(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.max(),
                (SeriesExp<T>) Exp.$col(column),
                index -> column
        );
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Aggregator<T> max(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.max(),
                (SeriesExp<T>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Aggregator<T> min(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.min(),
                (SeriesExp<T>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Aggregator<T> min(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.min(),
                (SeriesExp<T>) Exp.$col(column),
                index -> column
        );
    }

    static Aggregator<String> concat(String column, String delimiter) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter),
                Exp.$col(column),
                index -> column
        );
    }

    static Aggregator<String> concat(int column, String delimiter) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter),
                Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    static Aggregator<String> concat(String column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter, prefix, suffix),
                Exp.$col(column),
                index -> column
        );
    }

    static Aggregator<String> concat(int column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter, prefix, suffix),
                Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    static <S> Aggregator<List<S>> list(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.list(),
                (SeriesExp<S>) Exp.$col(column),
                index -> column
        );
    }

    static <S> Aggregator<List<S>> list(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.list(),
                (SeriesExp<S>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    static <S> Aggregator<Set<S>> set(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.set(),
                (SeriesExp<S>) Exp.$col(column),
                index -> column
        );
    }

    static <S> Aggregator<Set<S>> set(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.set(),
                (SeriesExp<S>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * Starts a builder of an aggregator that will prefilter DataFrame rows before applying an aggregation function.
     *
     * @since 0.7
     */
    static AggregatorBuilder filterRows(RowPredicate rowPredicate) {
        return new AggregatorBuilder().filterRows(rowPredicate);
    }

    /**
     * Starts a builder of an aggregator that will prefilter DataFrame rows before applying an aggregation function.
     *
     * @since 0.7
     */
    static <V> AggregatorBuilder filterRows(String columnLabel, ValuePredicate<V> filter) {
        return new AggregatorBuilder().filterRows(columnLabel, filter);
    }

    /**
     * Starts a builder of an aggregator that will prefilter DataFrame rows before applying an aggregation function.
     *
     * @since 0.7
     */
    static <V> AggregatorBuilder filterRows(int columnPos, ValuePredicate<V> filter) {
        return new AggregatorBuilder().filterRows(columnPos, filter);
    }

    static <S, T> Aggregator<T> of(String column, Collector<S, ?, T> aggregator) {
        return new ColumnAggregator<>(
                SeriesAggregator.of("of", aggregator),
                (SeriesExp<S>) Exp.$col(column),
                index -> column
        );
    }

    static <S, T> Aggregator<T> of(int column, Collector<S, ?, T> aggregator) {
        return new ColumnAggregator<>(
                SeriesAggregator.of("of", aggregator),
                (SeriesExp<S>) Exp.$col(column),
                index -> index.getLabel(column)
        );
    }

    /**
     * Creates an Aggregator based on a custom function that takes the entire DataFrame.
     *
     * @since 0.6
     */
    static <T> Aggregator<T> of(Function<DataFrame, T> aggregator) {
        return new DataFrameAggregator<>(
                aggregator,
                index -> "of"
        );
    }

    T aggregate(DataFrame df);

    String aggregateLabel(Index columnIndex);

    /**
     * Ensures that the aggregated column in a DataFrame will be named using the provided label. Only applicable for
     * aggregating GroupBy.
     */
    Aggregator<T> named(String newAggregateLabel);
}
