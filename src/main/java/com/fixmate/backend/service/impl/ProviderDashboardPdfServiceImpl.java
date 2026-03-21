package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.EarningsPointDTO;
import com.fixmate.backend.dto.response.ProviderDashboardBookingItemDTO;
import com.fixmate.backend.dto.response.ProviderDashboardSummaryDTO;
import com.fixmate.backend.service.ProviderDashboardPdfService;
import com.fixmate.backend.service.ProviderDashboardService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ProviderDashboardPdfServiceImpl implements ProviderDashboardPdfService {

    private final ProviderDashboardService providerDashboardService;

    @Override
    public byte[] generateProviderDashboardPdf(Long userId) {
        ProviderDashboardSummaryDTO summary = providerDashboardService.getSummary(userId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font smallBoldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

            // Title
            Paragraph title = new Paragraph("Provider Dashboard Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Generated from FixMate Provider Dashboard", normalFont));
            document.add(new Paragraph(" "));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
                    .withZone(ZoneId.of("Asia/Colombo"));
            document.add(new Paragraph("Generated At: " + dtf.format(java.time.Instant.now()), normalFont));
            document.add(new Paragraph(" "));

            // KPI section
            document.add(new Paragraph("Dashboard Summary", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable kpiTable = new PdfPTable(2);
            kpiTable.setWidthPercentage(100);
            kpiTable.setSpacingBefore(5f);
            kpiTable.setSpacingAfter(10f);

            addKeyValueCell(kpiTable, "Total Bookings", String.valueOf(summary.getTotalBookings()), smallBoldFont, normalFont);
            addKeyValueCell(kpiTable, "Active Jobs", String.valueOf(summary.getActiveJobs()), smallBoldFont, normalFont);
            addKeyValueCell(kpiTable, "Completed Jobs", String.valueOf(summary.getCompletedJobs()), smallBoldFont, normalFont);
            addKeyValueCell(kpiTable, "Month Income", "Rs. " + formatMoney(summary.getMonthIncome()), smallBoldFont, normalFont);
            addKeyValueCell(kpiTable, "Year Income", "Rs. " + formatMoney(summary.getYearIncome()), smallBoldFont, normalFont);
            addKeyValueCell(kpiTable, "Lifetime Income", "Rs. " + formatMoney(summary.getLifetimeIncome()), smallBoldFont, normalFont);

            document.add(kpiTable);

            // Alerts
            document.add(new Paragraph("Alerts", sectionFont));
            document.add(new Paragraph(" "));
            PdfPTable alertTable = new PdfPTable(2);
            alertTable.setWidthPercentage(100);

            addKeyValueCell(alertTable, "New Booking Requests", String.valueOf(summary.getAlerts().getNewRequests()), smallBoldFont, normalFont);
            addKeyValueCell(alertTable, "Payment Pending", String.valueOf(summary.getAlerts().getPaymentPending()), smallBoldFont, normalFont);
            addKeyValueCell(alertTable, "Today Jobs", String.valueOf(summary.getAlerts().getTodayJobs()), smallBoldFont, normalFont);
            addKeyValueCell(alertTable, "Availability Off", String.valueOf(summary.getAlerts().isAvailabilityOff()), smallBoldFont, normalFont);
            addKeyValueCell(alertTable, "Verification Pending", String.valueOf(summary.getAlerts().isVerificationPending()), smallBoldFont, normalFont);

            document.add(alertTable);
            document.add(new Paragraph(" "));

            // Earnings last 6 months
            document.add(new Paragraph("Last 6 Months Earnings", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable earningsTable = new PdfPTable(2);
            earningsTable.setWidthPercentage(100);
            addTableHeader(earningsTable, "Month", smallBoldFont);
            addTableHeader(earningsTable, "Amount", smallBoldFont);

            if (summary.getEarningsLast6Months() != null) {
                for (EarningsPointDTO point : summary.getEarningsLast6Months()) {
                    earningsTable.addCell(new Phrase(point.getMonth(), normalFont));
                    earningsTable.addCell(new Phrase("Rs. " + formatMoney(point.getTotal()), normalFont));
                }
            }

            document.add(earningsTable);
            document.add(new Paragraph(" "));

            // Today's bookings
            document.add(new Paragraph("Today's Bookings", sectionFont));
            document.add(new Paragraph(" "));
            document.add(createBookingTable(summary.getTodayBookings(), normalFont, smallBoldFont));
            document.add(new Paragraph(" "));

            // Upcoming bookings
            document.add(new Paragraph("Upcoming Bookings", sectionFont));
            document.add(new Paragraph(" "));
            document.add(createBookingTable(summary.getUpcomingBookings(), normalFont, smallBoldFont));
            document.add(new Paragraph(" "));

            // Profile health
            document.add(new Paragraph("Profile Health", sectionFont));
            document.add(new Paragraph(" "));
            PdfPTable profileTable = new PdfPTable(2);
            profileTable.setWidthPercentage(100);

            addKeyValueCell(profileTable, "Completion", summary.getProfileHealth().getCompletionPercent() + "%", smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Has Profile Picture", String.valueOf(summary.getProfileHealth().isHasProfilePic()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Has Phone", String.valueOf(summary.getProfileHealth().isHasPhone()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Has Address", String.valueOf(summary.getProfileHealth().isHasAddress()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Has Skill", String.valueOf(summary.getProfileHealth().isHasSkill()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Has Experience", String.valueOf(summary.getProfileHealth().isHasExperience()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Has Description", String.valueOf(summary.getProfileHealth().isHasDescription()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Services Count", String.valueOf(summary.getProfileHealth().getServicesCount()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Available", String.valueOf(summary.getProfileHealth().isAvailable()), smallBoldFont, normalFont);
            addKeyValueCell(profileTable, "Verified", String.valueOf(summary.getProfileHealth().isVerified()), smallBoldFont, normalFont);

            document.add(profileTable);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate provider dashboard PDF", e);
        }
    }

    private void addKeyValueCell(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        keyCell.setPadding(8f);
        valueCell.setPadding(8f);
        table.addCell(keyCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setPadding(8f);
        table.addCell(header);
    }

    private PdfPTable createBookingTable(
            java.util.List<ProviderDashboardBookingItemDTO> bookings,
            Font normalFont,
            Font headerFont
    ) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        addTableHeader(table, "Booking ID", headerFont);
        addTableHeader(table, "Customer", headerFont);
        addTableHeader(table, "Service", headerFont);
        addTableHeader(table, "Status", headerFont);
        addTableHeader(table, "Amount", headerFont);

        if (bookings == null || bookings.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("No bookings available", normalFont));
            empty.setColspan(5);
            empty.setPadding(8f);
            table.addCell(empty);
            return table;
        }

        for (ProviderDashboardBookingItemDTO booking : bookings) {
            table.addCell(new Phrase(String.valueOf(booking.getBookingId()), normalFont));
            table.addCell(new Phrase(safe(booking.getCustomerName()), normalFont));
            table.addCell(new Phrase(safe(booking.getServiceTitle()), normalFont));
            table.addCell(new Phrase(booking.getStatus() != null ? booking.getStatus().name() : "-", normalFont));
            table.addCell(new Phrase("Rs. " + formatMoney(booking.getAmount()), normalFont));
        }

        return table;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
