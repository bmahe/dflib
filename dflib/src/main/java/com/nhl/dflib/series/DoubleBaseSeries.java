package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoublePredicate;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.series.builder.BooleanAccumulator;
import com.nhl.dflib.series.builder.DoubleAccumulator;
import com.nhl.dflib.series.builder.IntAccumulator;
import com.nhl.dflib.series.builder.ObjectAccumulator;
import com.nhl.dflib.series.builder.UniqueDoubleAccumulator;

import java.util.Objects;

/**
 * @since 0.6
 */
public abstract class DoubleBaseSeries implements DoubleSeries {

    @Override
    public <V> Series<V> map(ValueMapper<Double, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    @Override
    public Series<Double> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedDouble(fromInclusive, toExclusive);
    }

    @Override
    public Series<Double> select(IntSeries positions) {

        int h = positions.size();

        double[] data = new double[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain primitive and have to change to Series<Double>...
            if (index < 0) {
                return selectAsObjectSeries(positions);
            }

            data[i] = getDouble(index);
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public Series<Double> filter(ValuePredicate<Double> p) {
        return filterDouble(d -> p.test(d));
    }

    @Override
    public DoubleSeries filterDouble(DoublePredicate p) {
        DoubleAccumulator filtered = new DoubleAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            double v = getDouble(i);
            if (p.test(v)) {
                filtered.add(v);
            }
        }

        return filtered.toDoubleSeries();
    }

    @Override
    public DoubleSeries filterDouble(BooleanSeries positions) {
        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        DoubleAccumulator data = new DoubleAccumulator();

        for (int i = 0; i < size(); i++) {
            if (positions.getBoolean(i)) {
                data.add(getDouble(i));
            }
        }

        return data.toDoubleSeries();
    }

    @Override
    public Series<Double> filter(BooleanSeries positions) {
        return filterDouble(positions);
    }

    private Series<Double> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Double[] data = new Double[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getDouble(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public DoubleSeries concatDouble(DoubleSeries... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        int size = size();
        int h = size;
        for (DoubleSeries s : other) {
            h += s.size();
        }

        double[] data = new double[h];
        copyToDouble(data, 0, 0, size);

        int offset = size;
        for (DoubleSeries s : other) {
            int len = s.size();
            s.copyToDouble(data, 0, offset, len);
            offset += len;
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public Series<Double> fillNulls(Double value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> fillNullsFromSeries(Series<? extends Double> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> head(int len) {
        return headDouble(len);
    }

    @Override
    public Series<Double> tail(int len) {
        return tailDouble(len);
    }

    @Override
    public Series<Double> concat(Series<? extends Double>... other) {
        // concatenating as Double... to concat as DoubleServies, "concatDouble" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Double>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<Double> materialize() {
        return materializeDouble();
    }

    @Override
    public Double get(int index) {
        return getDouble(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getDouble(i);
        }
    }

    @Override
    public IntSeries indexDouble(DoublePredicate predicate) {
        IntAccumulator filtered = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getDouble(i))) {
                filtered.add(i);
            }
        }

        return filtered.toIntSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Double> predicate) {
        IntAccumulator index = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.add(i);
            }
        }

        return index.toIntSeries();
    }

    @Override
    public Series<Double> replace(BooleanSeries condition, Double with) {
        return with != null
                ? replaceDouble(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Double> replaceNoMatch(BooleanSeries condition, Double with) {
        return with != null
                ? replaceNoMatchDouble(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make double versions of replace public?

    private DoubleSeries replaceDouble(BooleanSeries condition, double with) {
        int s = size();
        int r = Math.min(s, condition.size());
        DoubleAccumulator doubles = new DoubleAccumulator(s);

        for (int i = 0; i < r; i++) {
            doubles.add(condition.getBoolean(i) ? with : getDouble(i));
        }

        for (int i = r; i < s; i++) {
            doubles.add(getDouble(i));
        }

        return doubles.toDoubleSeries();
    }

    private DoubleSeries replaceNoMatchDouble(BooleanSeries condition, double with) {

        int s = size();
        int r = Math.min(s, condition.size());
        DoubleAccumulator doubles = new DoubleAccumulator(s);

        for (int i = 0; i < r; i++) {
            doubles.add(condition.getBoolean(i) ? getDouble(i) : with);
        }

        if (s > r) {
            doubles.fill(r, s, with);
        }

        return doubles.toDoubleSeries();
    }

    private Series<Double> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Double> vals = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? null : getDouble(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(getDouble(i));
        }

        return vals.toSeries();
    }

    private Series<Double> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Double> vals = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? getDouble(i) : null);
        }

        if (s > r) {
            vals.fill(r, s, null);
        }

        return vals.toSeries();
    }

    @Override
    public BooleanSeries eq(Series<Double> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);

        if (another instanceof DoubleSeries) {
            DoubleSeries anotherDouble = (DoubleSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getDouble(i) == anotherDouble.getDouble(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public BooleanSeries ne(Series<Double> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);
        if (another instanceof DoubleSeries) {
            DoubleSeries anotherDouble = (DoubleSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getDouble(i) != anotherDouble.getDouble(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public Series<Double> unique() {
        return uniqueDouble();
    }

    @Override
    public DoubleSeries uniqueDouble() {
        int size = size();
        if (size < 2) {
            return this;
        }

        DoubleAccumulator unique = new UniqueDoubleAccumulator();
        for (int i = 0; i < size; i++) {
            unique.add(get(i));
        }

        return unique.size() < size() ? unique.toDoubleSeries() : this;
    }

    @Override
    public DataFrame valueCounts() {
        return ValueCounts.valueCountsNoNulls(this);
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
