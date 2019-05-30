package com.nhl.dflib.jdbc.connector;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

/**
 * @since 0.6
 */
public interface StatementPositionBinderFactory {

    static StatementPositionBinder objectBinder(StatementPosition p) {
        return  v -> {

            Object bv = p.boundableValue(v);
            if (bv == null) {
                p.getStatement().setNull(p.getPosition(), p.getType());
            } else {
                p.getStatement().setObject(p.getPosition(), bv, p.getType());
            }
        };
    }

    static StatementPositionBinder dateBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> o instanceof LocalDate ? Date.valueOf((LocalDate) o) : o);
        return objectBinder(converted);
    }

    static StatementPositionBinder timestampBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> o instanceof LocalDateTime ? Timestamp.valueOf((LocalDateTime) o) : o);
        return objectBinder(converted);
    }

    static StatementPositionBinder timeBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> o instanceof LocalTime ? Time.valueOf((LocalTime) o) : o);
        return objectBinder(converted);
    }

    static StatementPositionBinder intBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> {

            // TODO: inefficient - checking type of every object for the same binding... need to be able to precompile
            //  for the entire column. Should be possible for primitive columns whose type is well-defined.

            // at least check for Number first, that is more likely to match here
            if (o instanceof Number) {
                return o;
            }
            // Month is an enum, must go prior to generic enums to return proper value
            else if (o instanceof Month) {
                return ((Month) o).getValue();
            }
            // DayOfWeek is an enum, must go prior to generic enums to return proper value
            else if (o instanceof DayOfWeek) {
                return ((DayOfWeek) o).getValue();
            } else if (o instanceof Enum) {
                return ((Enum) o).ordinal();
            } else if (o instanceof Year) {
                return ((Year) o).getValue();
            } else {
                return o;
            }
        });

        return objectBinder(converted);
    }

    static StatementPositionBinder stringBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> {

            // TODO: inefficient - checking type of every object for the same binding... need to be able to precompile
            //  for the entire column. Should be possible for primitive columns whose type is well-defined.

            // at least check for String first, that is more likely to match here
            if (o instanceof String) {
                return o;
            } else if (o instanceof Enum) {
                return ((Enum) o).name();
            } else {
                // TODO: call 'toString' ?
                return o;
            }
        });

        return objectBinder(converted);
    }

    StatementPositionBinder binder(StatementPosition p);
}