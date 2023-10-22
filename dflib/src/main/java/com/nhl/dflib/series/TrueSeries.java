package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;

import java.util.Arrays;

/**
 * @since 0.11
 */
public class TrueSeries extends BooleanBaseSeries {

    private final int size;

    public TrueSeries(int size) {
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean getBool(int index) {
        return true;
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, true);
    }

    @Override
    public BooleanSeries materialize() {
        return this;
    }

    @Override
    public BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new TrueSeries(toExclusive - fromInclusive);
    }

    @Override
    public BooleanSeries head(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? tail(size + len) : new TrueSeries(len);
    }

    @Override
    public BooleanSeries tail(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? head(size + len) : new TrueSeries(len);
    }

    @Override
    public int firstTrue() {
        return size > 0 ? 0 : -1;
    }

    @Override
    public int countTrue() {
        return 0;
    }

    @Override
    public int countFalse() {
        return size;
    }
}
