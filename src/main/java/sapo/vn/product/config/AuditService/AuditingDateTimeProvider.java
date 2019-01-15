package sapo.vn.product.config.AuditService;

import org.springframework.data.auditing.DateTimeProvider;

import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

public class AuditingDateTimeProvider implements DateTimeProvider {

    private final DateTimeService dateTimeService;

    public AuditingDateTimeProvider(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.ofNullable(dateTimeService.getCurrentDateAndTime());
    }
}