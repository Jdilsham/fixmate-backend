package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PaymentStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.PaymentRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.CustomerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomerDashboardServiceImpl implements CustomerDashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    private static final ZoneId SRI_LANKA = ZoneId.of("Asia/Colombo");

    @Override
    @Transactional(readOnly = true)
    public CustomerDashboardSummaryDTO getSummary(String email) {

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        Long customerId = customer.getId();

        // Time boundaries (Sri Lanka)
        ZonedDateTime nowSL = ZonedDateTime.now(SRI_LANKA);

        Instant todayStart = nowSL.toLocalDate()
                .atStartOfDay(SRI_LANKA).toInstant();
        Instant tomorrowStart = nowSL.toLocalDate()
                .plusDays(1).atStartOfDay(SRI_LANKA).toInstant();

        LocalDate firstDayOfMonth = nowSL.toLocalDate().withDayOfMonth(1);
        Instant monthStart = firstDayOfMonth
                .atStartOfDay(SRI_LANKA).toInstant();
        Instant nextMonthStart = firstDayOfMonth
                .plusMonths(1).atStartOfDay(SRI_LANKA).toInstant();

        LocalDate firstDayOfYear = nowSL.toLocalDate().withDayOfYear(1);
        Instant yearStart = firstDayOfYear
                .atStartOfDay(SRI_LANKA).toInstant();
        Instant nextYearStart = firstDayOfYear
                .plusYears(1).atStartOfDay(SRI_LANKA).toInstant();

        // KPI counts
        long total = bookingRepository.countByUserId(customerId);

        long pending = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.PENDING);
        long accepted = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.ACCEPTED);
        long inProgress = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.IN_PROGRESS);
        long paymentPending = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.PAYMENT_PENDING);

        long completed = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.COMPLETED);
        long cancelled = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.CANCELLED);
        long rejected = bookingRepository.countByUserIdAndStatus(customerId, BookingStatus.REJECTED);

        long active = pending + accepted + inProgress + paymentPending;

        // Spending
        BigDecimal lifetime = paymentRepository
                .sumByCustomerAndStatus(customerId, PaymentStatus.CONFIRMED);

        BigDecimal month = paymentRepository
                .sumByCustomerAndStatusAndPaidAtBetween(customerId, PaymentStatus.CONFIRMED, monthStart, nextMonthStart);

        BigDecimal year = paymentRepository
                .sumByCustomerAndStatusAndPaidAtBetween(customerId, PaymentStatus.CONFIRMED, yearStart, nextYearStart);

        // Chart
        List<SpendingPointDTO> series = buildLast6MonthsSpending(customerId, nowSL);

        // Today & Upcoming bookings
        List<CustomerDashboardBookingItemDTO> todayBookings =
                bookingRepository.findCustomerBookingsScheduledBetween(customerId, todayStart, tomorrowStart)
                        .stream().map(this::toBookingItem).toList();

        // next 7 days
        Instant upcomingFrom = tomorrowStart;
        Instant upcomingTo = nowSL.toLocalDate()
                .plusDays(8).atStartOfDay(SRI_LANKA).toInstant(); // tomorrow + 7 days window

        List<CustomerDashboardBookingItemDTO> upcomingBookings =
                bookingRepository.findCustomerBookingsScheduledBetween(customerId, upcomingFrom, upcomingTo)
                        .stream().limit(10).map(this::toBookingItem).toList();

        // Alerts
        CustomerDashboardAlertsDTO alerts = CustomerDashboardAlertsDTO.builder()
                .paymentPending(paymentPending)
                .bookingsToday(todayBookings.size())
                .upcomingBookings(upcomingBookings.size())
                .build();

        // Profile health
        boolean hasPhone = customer.getPhone() != null && !customer.getPhone().isBlank();
        boolean hasProfilePic = customer.getProfilePic() != null && !customer.getProfilePic().isBlank();

        int score = 0;
        if (hasPhone) score += 50;
        if (hasProfilePic) score += 50;

        CustomerDashboardProfileHealthDTO profileHealth =
                CustomerDashboardProfileHealthDTO.builder()
                        .hasPhone(hasPhone)
                        .hasProfilePic(hasProfilePic)
                        .score(score)
                        .build();

        return CustomerDashboardSummaryDTO.builder()
                .totalBookings(total)
                .activeBookings(active)
                .completedBookings(completed)
                .cancelledBookings(cancelled)
                .rejectedBookings(rejected)
                .pendingBookings(pending)
                .paymentPendingBookings(paymentPending)

                .monthSpending(nullSafe(month))
                .yearSpending(nullSafe(year))
                .lifetimeSpending(nullSafe(lifetime))

                .last6MonthsSpending(series)
                .alerts(alerts)
                .todayBookings(todayBookings)
                .upcomingBookings(upcomingBookings)
                .profileHealth(profileHealth)
                .build();
    }

    private List<SpendingPointDTO> buildLast6MonthsSpending(Long customerId, ZonedDateTime nowSL) {
        // Range: start of month 5 months ago -> start of next month
        LocalDate startMonth = nowSL.toLocalDate().withDayOfMonth(1).minusMonths(5);
        LocalDate endNextMonth = nowSL.toLocalDate().withDayOfMonth(1).plusMonths(1);

        Instant from = startMonth.atStartOfDay(SRI_LANKA).toInstant();
        Instant to = endNextMonth.atStartOfDay(SRI_LANKA).toInstant();

        List<Payment> payments = paymentRepository.findByCustomerAndStatusAndPaidAtBetween(
                customerId, PaymentStatus.CONFIRMED, from, to
        );

        // Build month buckets
        return startMonth.datesUntil(endNextMonth, java.time.Period.ofMonths(1))
                .map(monthStart -> {
                    LocalDate next = monthStart.plusMonths(1);
                    Instant mFrom = monthStart.atStartOfDay(SRI_LANKA).toInstant();
                    Instant mTo = next.atStartOfDay(SRI_LANKA).toInstant();

                    BigDecimal total = payments.stream()
                            .filter(p -> p.getPaidAt() != null)
                            .filter(p -> !p.getPaidAt().isBefore(mFrom) && p.getPaidAt().isBefore(mTo))
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    String label = monthStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    return new SpendingPointDTO(label, total);
                })
                .toList();
    }

    private CustomerDashboardBookingItemDTO toBookingItem(Booking b) {
        String providerName = (b.getServiceProvider() != null && b.getServiceProvider().getUser() != null)
                ? b.getServiceProvider().getUser().getFirstName() + " " + b.getServiceProvider().getUser().getLastName()
                : "Not assigned";

        String serviceName = (b.getProviderService() != null && b.getProviderService().getService() != null)
                ? b.getProviderService().getService().getTitle()
                : "Service";

        LocalDateTime scheduled = (b.getScheduledAt() == null)
                ? null
                : LocalDateTime.ofInstant(b.getScheduledAt(), SRI_LANKA);

        return CustomerDashboardBookingItemDTO.builder()
                .bookingId(b.getBookingId())
                .serviceName(serviceName)
                .providerName(providerName)
                .status(b.getStatus())
                .scheduledAt(scheduled)
                .amount(b.getTotalPrice())
                .pricingType(b.getPricingType() != null ? b.getPricingType().name() : null)
                .build();
    }

    private BigDecimal nullSafe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}