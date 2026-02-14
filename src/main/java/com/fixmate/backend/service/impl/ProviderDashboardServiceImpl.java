package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PaymentStatus;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.service.ProviderDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderDashboardServiceImpl implements ProviderDashboardService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final ProviderServiceRepository providerServiceRepository;

    @Override
    public ProviderDashboardSummaryDTO getSummary(Long userId) {

        ServiceProvider provider = serviceProviderRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Service provider profile not found"));

        Long providerId = provider.getServiceProviderId();

        // KPI counts
        long totalBookings = bookingRepository.countByProviderService_ServiceProvider_ServiceProviderId(providerId);

        long activeJobs = bookingRepository.countByProviderAndStatuses(
                providerId,
                List.of(BookingStatus.ACCEPTED, BookingStatus.IN_PROGRESS, BookingStatus.PAYMENT_PENDING)
        );

        long completedJobs = bookingRepository.countByProviderService_ServiceProvider_ServiceProviderIdAndStatus(
                providerId, BookingStatus.COMPLETED
        );

        // Income (CONFIRMED payments only)
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        Instant monthStart = today.withDayOfMonth(1).atStartOfDay(zone).toInstant();
        Instant nextMonthStart = today.withDayOfMonth(1).plusMonths(1).atStartOfDay(zone).toInstant();

        Instant yearStart = LocalDate.of(today.getYear(), 1, 1).atStartOfDay(zone).toInstant();
        Instant nextYearStart = LocalDate.of(today.getYear() + 1, 1, 1).atStartOfDay(zone).toInstant();

        BigDecimal monthIncome = paymentRepository.sumByProviderAndStatusAndPaidAtBetween(
                providerId, PaymentStatus.CONFIRMED, monthStart, nextMonthStart
        );

        BigDecimal yearIncome = paymentRepository.sumByProviderAndStatusAndPaidAtBetween(
                providerId, PaymentStatus.CONFIRMED, yearStart, nextYearStart
        );

        BigDecimal lifetimeIncome = paymentRepository.sumByProviderAndStatus(
                providerId, PaymentStatus.CONFIRMED
        );

        // Earnings chart (last 6 months)
        List<EarningsPointDTO> last6Months = buildLast6MonthsSeries(providerId, zone);

        // Alerts
        long newRequests = bookingRepository.countByProviderService_ServiceProvider_ServiceProviderIdAndStatus(
                providerId, BookingStatus.PENDING
        );

        long paymentPending = bookingRepository.countByProviderService_ServiceProvider_ServiceProviderIdAndStatus(
                providerId, BookingStatus.PAYMENT_PENDING
        );

        Instant dayStart = today.atStartOfDay(zone).toInstant();
        Instant dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant();

        long todayJobs = bookingRepository.findProviderBookingsScheduledBetween(providerId, dayStart, dayEnd).size();

        boolean availabilityOff = !Boolean.TRUE.equals(provider.getIsAvailable());

        boolean verificationPending =
                !Boolean.TRUE.equals(provider.getIsVerified()) ||
                        provider.getVerificationStatus() != VerificationStatus.APPROVED;

        ProviderDashboardAlertsDTO alerts = ProviderDashboardAlertsDTO.builder()
                .newRequests(newRequests)
                .paymentPending(paymentPending)
                .todayJobs(todayJobs)
                .availabilityOff(availabilityOff)
                .verificationPending(verificationPending)
                .build();

        // Today + Upcoming
        List<ProviderDashboardBookingItemDTO> todayBookings = mapBookingsToItems(
                bookingRepository.findProviderBookingsScheduledBetween(providerId, dayStart, dayEnd),
                zone
        );

        Instant upcomingEnd = today.plusDays(7).atStartOfDay(zone).toInstant();
        List<ProviderDashboardBookingItemDTO> upcomingBookings = mapBookingsToItems(
                bookingRepository.findProviderBookingsScheduledBetween(providerId, dayEnd, upcomingEnd),
                zone
        );

        // Profile health
        ProviderDashboardProfileHealthDTO profileHealth = buildProfileHealth(provider);

        return ProviderDashboardSummaryDTO.builder()
                .totalBookings(totalBookings)
                .activeJobs(activeJobs)
                .completedJobs(completedJobs)
                .monthIncome(nz(monthIncome))
                .yearIncome(nz(yearIncome))
                .lifetimeIncome(nz(lifetimeIncome))
                .earningsLast6Months(last6Months)
                .alerts(alerts)
                .todayBookings(todayBookings)
                .upcomingBookings(upcomingBookings)
                .profileHealth(profileHealth)
                .ratingsEnabled(false)
                .build();
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private List<EarningsPointDTO> buildLast6MonthsSeries(Long providerId, ZoneId zone) {

        LocalDate firstDayThisMonth = LocalDate.now(zone).withDayOfMonth(1);
        LocalDate startMonth = firstDayThisMonth.minusMonths(5);

        Instant from = startMonth.atStartOfDay(zone).toInstant();
        Instant to = firstDayThisMonth.plusMonths(1).atStartOfDay(zone).toInstant();

        List<Payment> payments = paymentRepository.findByProviderAndStatusAndPaidAtBetween(
                providerId, PaymentStatus.CONFIRMED, from, to
        );

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, BigDecimal> byMonth = new HashMap<>();
        for (Payment p : payments) {
            if (p.getPaidAt() == null) continue;
            String key = LocalDateTime.ofInstant(p.getPaidAt(), zone).format(fmt);
            byMonth.put(key, nz(byMonth.get(key)).add(nz(p.getAmount())));
        }

        List<EarningsPointDTO> series = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LocalDate m = startMonth.plusMonths(i);
            String key = m.format(fmt);
            series.add(new EarningsPointDTO(key, nz(byMonth.get(key))));
        }
        return series;
    }

    private List<ProviderDashboardBookingItemDTO> mapBookingsToItems(List<Booking> bookings, ZoneId zone) {
        return bookings.stream().map(b -> ProviderDashboardBookingItemDTO.builder()
                .bookingId(b.getBookingId())
                .status(b.getStatus())
                .scheduledAt(b.getScheduledAt())
                .customerName(
                        b.getUser() != null
                                ? (b.getUser().getFirstName() + " " + b.getUser().getLastName())
                                : "—"
                )
                .serviceTitle(
                        b.getProviderService() != null && b.getProviderService().getService() != null
                                ? b.getProviderService().getService().getTitle()
                                : "—"
                )
                .city(b.getContactInfo() != null ? b.getContactInfo().getCity() : null)
                .address(b.getContactInfo() != null ? b.getContactInfo().getAddress() : null)
                .amount(b.getTotalPrice())
                .paymentStatus(
                        b.getPayment() != null && b.getPayment().getStatus() != null
                                ? b.getPayment().getStatus().name()
                                : null
                )
                .build()
        ).collect(Collectors.toList());
    }

    private ProviderDashboardProfileHealthDTO buildProfileHealth(ServiceProvider provider) {

        boolean hasProfilePic = provider.getUser() != null &&
                provider.getUser().getProfilePic() != null &&
                !provider.getUser().getProfilePic().isBlank();

        boolean hasPhone = provider.getUser() != null &&
                provider.getUser().getPhone() != null &&
                !provider.getUser().getPhone().isBlank();

        boolean hasAddress = provider.getUser() != null &&
                addressRepository.existsByUserId(provider.getUser().getId());

        boolean hasSkill = provider.getSkill() != null && !provider.getSkill().isBlank();
        boolean hasExperience = provider.getExperience() != null && !provider.getExperience().isBlank();
        boolean hasDescription = provider.getDescription() != null && !provider.getDescription().isBlank();

        boolean hasIdFront = provider.getIdFrontUrl() != null && !provider.getIdFrontUrl().isBlank();
        boolean hasIdBack = provider.getIdBackUrl() != null && !provider.getIdBackUrl().isBlank();
        boolean hasWorkPdf = provider.getWorkPdfUrl() != null && !provider.getWorkPdfUrl().isBlank();

        int servicesCount = providerServiceRepository
                .findByServiceProvider_ServiceProviderId(provider.getServiceProviderId()).size();

        List<Boolean> checks = List.of(
                hasProfilePic, hasPhone, hasAddress,
                hasSkill, hasExperience, hasDescription,
                hasIdFront, hasIdBack, hasWorkPdf,
                servicesCount > 0
        );
        long ok = checks.stream().filter(Boolean::booleanValue).count();
        int percent = (int) Math.round((ok * 100.0) / checks.size());

        return ProviderDashboardProfileHealthDTO.builder()
                .completionPercent(percent)
                .hasProfilePic(hasProfilePic)
                .hasPhone(hasPhone)
                .hasAddress(hasAddress)
                .hasSkill(hasSkill)
                .hasExperience(hasExperience)
                .hasDescription(hasDescription)
                .hasIdFront(hasIdFront)
                .hasIdBack(hasIdBack)
                .hasWorkPdf(hasWorkPdf)
                .servicesCount(servicesCount)
                .isAvailable(Boolean.TRUE.equals(provider.getIsAvailable()))
                .isVerified(Boolean.TRUE.equals(provider.getIsVerified()))
                .build();
    }
}