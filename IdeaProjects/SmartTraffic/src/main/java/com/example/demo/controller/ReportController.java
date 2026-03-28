package com.example.demo.controller;

import com.example.demo.service.ReportService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReportData(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam String reportType,
            @RequestParam String dimension) {
        Map<String, Object> data = reportService.getReportData(startTime, endTime, reportType, dimension);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam String reportType,
            @RequestParam String dimension) {
        List<Map<String, Object>> list = reportService.getReportList(startTime, endTime, reportType, dimension);
        StringBuilder csv = new StringBuilder();
        csv.append("时间,数值,描述\n");
        for (Map<String, Object> item : list) {
            csv.append(escapeCsv(valueOf(item.get("date")))).append(',')
                    .append(escapeCsv(valueOf(item.get("value")))).append(',')
                    .append(escapeCsv(valueOf(item.get("desc"))))
                    .append('\n');
        }
        String filename = buildFilename(reportType, dimension, "csv");
        byte[] bytes = ("\uFEFF" + csv.toString()).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam String reportType,
            @RequestParam String dimension) throws DocumentException {
        List<Map<String, Object>> list = reportService.getReportList(startTime, endTime, reportType, dimension);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font font = createChineseFont();
        String title = buildTitle(reportType, dimension);
        Paragraph titlePara = new Paragraph(title, font);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(createCell("时间", font));
        table.addCell(createCell("数值", font));
        table.addCell(createCell("描述", font));

        for (Map<String, Object> item : list) {
            table.addCell(createCell(valueOf(item.get("date")), font));
            table.addCell(createCell(valueOf(item.get("value")), font));
            table.addCell(createCell(valueOf(item.get("desc")), font));
        }

        document.add(table);
        document.close();

        String filename = buildFilename(reportType, dimension, "pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    private Font createChineseFont() throws DocumentException {
        try {
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            return new Font(bf, 10, Font.NORMAL);
        } catch (Exception e) {
            throw new DocumentException(e.getMessage());
        }
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text == null ? "" : text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private String buildTitle(String reportType, String dimension) {
        String typeTitle = "报表";
        if ("carFlow".equalsIgnoreCase(reportType)) {
            typeTitle = "车辆流量报表";
        } else if ("violation".equalsIgnoreCase(reportType)) {
            typeTitle = "违章统计报表";
        } else if ("speed".equalsIgnoreCase(reportType)) {
            typeTitle = "车速分析报表";
        }
        String dimTitle = "日";
        if ("week".equalsIgnoreCase(dimension)) {
            dimTitle = "周";
        } else if ("month".equalsIgnoreCase(dimension)) {
            dimTitle = "月";
        } else if ("year".equalsIgnoreCase(dimension)) {
            dimTitle = "年";
        }
        return typeTitle + "（" + dimTitle + "维度）";
    }

    private String buildFilename(String reportType, String dimension, String ext) {
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return reportType + "-" + dimension + "-" + time + "." + ext;
    }

    private String valueOf(Object value) {
        return value == null ? "" : value.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuote = value.contains(",") || value.contains("\n") || value.contains("\r") || value.contains("\"");
        String escaped = value.replace("\"", "\"\"");
        return needsQuote ? "\"" + escaped + "\"" : escaped;
    }
}
