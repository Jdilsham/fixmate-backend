package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.AdminDashboardStats;
import com.fixmate.backend.service.AdminDashboardPdfService;
import com.fixmate.backend.service.AdminService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AdminDashboardPdfServiceImpl implements AdminDashboardPdfService {

    private final AdminService adminService;

    @Override
    public byte[] generateAdminDashboardPdf() {
        AdminDashboardStats stats = adminService.getDashboardStats();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font smallBoldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

            Paragraph title = new Paragraph("Admin Dashboard Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Generated from FixMate Admin Dashboard", normalFont));
            document.add(new Paragraph(" "));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
                    .withZone(ZoneId.of("Asia/Colombo"));

            document.add(new Paragraph("Generated At: " + dtf.format(Instant.now()), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Platform Summary", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(10f);

            addKeyValueCell(table, "Total Users", String.valueOf(stats.totalUsers()), smallBoldFont, normalFont);
            addKeyValueCell(table, "Total Providers", String.valueOf(stats.totalProviders()), smallBoldFont, normalFont);
            addKeyValueCell(table, "Pending Approvals", String.valueOf(stats.pendingApprovals()), smallBoldFont, normalFont);
            addKeyValueCell(table, "Total Bookings", String.valueOf(stats.totalBookings()), smallBoldFont, normalFont);
            addKeyValueCell(table, "Total Earnings", "Rs. " + formatMoney(stats.totalEarnings()), smallBoldFont, normalFont);

            document.add(table);

            document.add(new Paragraph("Insights", sectionFont));
            document.add(new Paragraph(" "));

            double providerRatio = stats.totalUsers() > 0
                    ? (stats.totalProviders() * 100.0) / stats.totalUsers()
                    : 0.0;

            double pendingApprovalRate = stats.totalProviders() > 0
                    ? (stats.pendingApprovals() * 100.0) / stats.totalProviders()
                    : 0.0;

            BigDecimal avgEarningsPerBooking = stats.totalBookings() > 0
                    ? nz(stats.totalEarnings()).divide(BigDecimal.valueOf(stats.totalBookings()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            double bookingDensity = stats.totalUsers() > 0
                    ? ((double) stats.totalBookings()) / stats.totalUsers()
                    : 0.0;

            PdfPTable insightTable = new PdfPTable(2);
            insightTable.setWidthPercentage(100);

            addKeyValueCell(insightTable, "Provider Ratio", formatPercent(providerRatio), smallBoldFont, normalFont);
            addKeyValueCell(insightTable, "Pending Approval Rate", formatPercent(pendingApprovalRate), smallBoldFont, normalFont);
            addKeyValueCell(insightTable, "Avg. Earnings / Booking", "Rs. " + formatMoney(avgEarningsPerBooking), smallBoldFont, normalFont);
            addKeyValueCell(insightTable, "Booking Density", String.format("%.2f", bookingDensity), smallBoldFont, normalFont);

            document.add(insightTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate admin dashboard PDF", e);
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

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String formatMoney(BigDecimal value) {
        return nz(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatPercent(double value) {
        return String.format("%.1f%%", value);
    }
}
