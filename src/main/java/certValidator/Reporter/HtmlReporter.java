package certValidator.Reporter;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.List;

import certValidator.Model.CertModel;

public class HtmlReporter {
    public static void generate(List<CertModel> data, String outputPath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='UTF-8'><style>")
            .append("body { font-family: sans-serif; } table {width:100%; border-collapse: collapse;}")
            .append("th, td {border: 1px solid #ddd; padding: 8px; font-size: 12px;}")
            .append("th {background-color: #333; color: white;}")
            .append(".expired {background-color: #ffcccc;}")
            .append(".warning {background-color: #ffffcc;}")
            .append("</style></head><body>");
        
        html.append("<h2>Relatório de Auditoria SSL</h2>");
        html.append("<table><tr><th>Arquivo</th><th>Alias</th><th>Validade</th><th>Dias</th><th>Emissor</th><th>Checksum</th></tr>");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (CertModel c : data) {
            String clazz = "";
            if (!c.isValid()) clazz = "expired";
            else if (c.getDaysRemaining() < 30) clazz = "warning";

            html.append(String.format("<tr class='%s'>", clazz));
            html.append("<td>").append(c.getFilePath()).append("</td>");
            
            if (c.getError() != null) {
                 html.append("<td colspan='5'><b>ERRO:</b> ").append(c.getError()).append("</td>");
            } else {
                html.append("<td>").append(c.getAlias()).append("</td>");
                html.append("<td>").append(sdf.format(c.getNotAfter())).append("</td>");
                html.append("<td>").append(c.getDaysRemaining()).append("</td>");
                html.append("<td>").append(c.getIssuer()).append("</td>");
                html.append("<td>").append(c.getChecksum().substring(0, 10)).append("...</td>");
            }
            html.append("</tr>");
        }
        html.append("</table></body></html>");
        Files.write(Paths.get(outputPath), html.toString().getBytes());
    }
}