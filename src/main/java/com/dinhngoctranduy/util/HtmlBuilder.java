package com.dinhngoctranduy.util;

import com.dinhngoctranduy.model.dto.ImageDTO;
import com.dinhngoctranduy.model.dto.TourResponseDTO;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class HtmlBuilder {
    public static String buildTourHtml(TourResponseDTO tour) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html><head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; line-height: 1.6; padding: 20px; }")
                .append("h1, h2 { color: #2c3e50; }")
                .append(".section { margin-bottom: 30px; }")
                .append(".images img { width: 200px; height: auto; margin-right: 10px; }")
                .append("</style>")
                .append("</head><body>");

        // Title + Code + Destination
        html.append("<div class='section'>")
                .append("<h1>").append(escape(tour.getTitle())).append("</h1>")
                .append("<p><strong>Mã tour:</strong> ").append(escape(tour.getCode())).append("</p>")
                .append("<p><strong>Điểm đến:</strong> ").append(escape(tour.getDestination())).append("</p>")
                .append("</div>");

        // Images
        if (tour.getImages() != null && !tour.getImages().isEmpty()) {
            html.append("<div class='section images'>")
                    .append("<h2>Hình ảnh</h2>");
            tour.getImages().stream().map(ImageDTO::getUrl).collect(Collectors.toSet()).stream().limit(3).forEach(url ->
                    html.append("<img src='").append(escape(safeHtml(url))).append("' />")
            );
            html.append("</div>");
        }

        // Description
        html.append("<div class='section'>")
                .append("<h2>Nội dung tour</h2>")
                .append("<p>").append(escape(tour.getDescription())).append("</p>")
                .append("</div>");

        // Itinerary
        html.append("<div class='section'>")
                .append("<h2>Chương trình tour</h2>");
        if (tour.getItinerary() != null && !tour.getItinerary().isEmpty()) {
            int day = 1;
            for (String item : tour.getItinerary()) {
                html.append("<p><strong>Ngày ").append(day++).append(":</strong> ")
                        .append(escape(item)).append("</p>");
            }
        } else {
            html.append("<p>Không có dữ liệu chương trình.</p>");
        }
        html.append("</div>");

        // Giá
        html.append("<div class='section'>")
                .append("<h2>Giá tour</h2>")
                .append("<p><strong>Người lớn:</strong> ").append(tour.getPriceAdults()).append(" VND</p>")
                .append("<p><strong>Trẻ em:</strong> ").append(tour.getPriceChildren()).append(" VND</p>")
                .append("</div>");

        html.append("</body></html>");

        String s =  StringEscapeUtils.unescapeHtml4(html.toString());
        return s;
    }

    private static String escape(String input) {
        return input == null ? "" : StringEscapeUtils.escapeHtml4(input);
    }

    public static String safeHtml(String rawHtml) {
        return rawHtml.replaceAll("&(?!amp;|lt;|gt;|quot;|#\\d+;)", "&amp;");
    }

}
