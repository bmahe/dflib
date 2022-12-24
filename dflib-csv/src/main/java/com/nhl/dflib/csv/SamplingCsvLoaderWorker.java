package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.csv.loader.CsvSeriesBuilder;
import com.nhl.dflib.builder.IntAccum;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.Random;

/**
 * Loads a row sample from a potentially large CSV row iterator with the specified sample size.
 */
class SamplingCsvLoaderWorker implements CsvLoaderWorker {

    protected CsvSeriesBuilder<?>[] columnBuilders;
    protected Index columnIndex;

    protected int rowSampleSize;
    protected Random rowsSampleRandom;
    protected IntAccum sampledRows;

    SamplingCsvLoaderWorker(
            Index columnIndex,
            CsvSeriesBuilder<?>[] columnBuilders,
            int rowSampleSize,
            Random rowsSampleRandom) {

        this.columnIndex = columnIndex;
        this.columnBuilders = columnBuilders;

        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
        this.sampledRows = new IntAccum();
    }

    @Override
    public DataFrame load(Iterator<CSVRecord> it) {
        consumeCSV(it);
        return toDataFrame();
    }

    protected DataFrame toDataFrame() {
        return sortSampled(toUnsortedDataFrame());
    }

    protected DataFrame toUnsortedDataFrame() {
        int width = columnIndex.size();
        Series<?>[] columns = new Series[width];
        for (int i = 0; i < width; i++) {
            columns[i] = columnBuilders[i].toSeries();
        }

        return DataFrame.newFrame(columnIndex).columns(columns);
    }

    protected DataFrame sortSampled(DataFrame sampledUnsorted) {
        IntSeries sortIndex = sampledRows.toSeries().sortIndexInt();
        return sampledUnsorted.selectRows(sortIndex);
    }

    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columnIndex.size();
        int i = 0;
        while (it.hasNext()) {
            CSVRecord row = it.next();
            sampleRow(i++, width, row);
        }
    }

    protected void sampleRow(int rowNumber, int width, CSVRecord row) {

        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (rowNumber < rowSampleSize) {
            addRow(width, row);
            sampledRows.pushInt(rowNumber);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(rowNumber + 1);
            if (pos < rowSampleSize) {
                replaceRow(pos, width, row);
                sampledRows.replaceInt(pos, rowNumber);
            }
        }
    }

    protected void addRow(int width, CSVRecord record) {
        for (int i = 0; i < width; i++) {
            columnBuilders[i].extract(record);
        }
    }

    protected void replaceRow(int pos, int width, CSVRecord record) {
        for (int i = 0; i < width; i++) {
            columnBuilders[i].extract(record, pos);
        }
    }
}
