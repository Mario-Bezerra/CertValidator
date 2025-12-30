package certValidator.reporter;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.List;

import certValidator.interfaces.IReporter;
import certValidator.model.CertModel;

/**
 * Implementation of IReporter that generates a visual HTML report of
 * certificate scan results.
 */
public class HtmlReporter implements IReporter {

    /** Path where the HTML report will be saved. */
    private final String outputPath;

    /**
     * Constructs an HtmlReporter with the specified output path.
     *
     * @param outputPath The path where the report should be written.
     */
    public HtmlReporter(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Generates the HTML report based on the provided certificate metadata.
     *
     * @param data The list of scanned certificates.
     * @throws IOException If an error occurs during file writing.
     */
    @Override
    public void generate(List<CertModel> data) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Relatório de Certificados</title><style>")
                .append(":root { --primary: #2c3e50; --danger: #e74c3c; --warning: #f1c40f; --success: #2ecc71; --light: #ecf0f1; }")
                .append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }")
                .append(".container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }")
                .append("h2 { color: var(--primary); border-bottom: 2px solid var(--primary); padding-bottom: 10px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("th { background-color: var(--primary); color: white; padding: 12px; text-align: left; }")
                .append("td { padding: 12px; border-bottom: 1px solid #ddd; vertical-align: top; }")
                .append("tr:hover { background-color: #f1f1f1; }")
                .append(".status-badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-weight: bold; font-size: 0.85em; color: white; }")
                .append(".status-expired { background-color: var(--danger); }")
                .append(".status-warning { background-color: var(--warning); color: #333; }")
                .append(".status-ok { background-color: var(--success); }")
                .append(".meta { font-size: 0.85em; color: #666; }")
                .append("</style></head><body>");

        html.append("<div class='container'>");
        html.append("<h2>Relatório de Auditoria de Certificados Digitais</h2>");
        html.append("<p>Gerado em: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()))
                .append("</p>");

        if (data.isEmpty()) {
            html.append("<p>Nenhum certificado encontrado.</p>");
        } else {
            html.append("<table><thead><tr>")
                    .append("<th>Status</th>")
                    .append("<th>Arquivo / Alias</th>")
                    .append("<th>Detalhes do Emissor</th>")
                    .append("<th>Validade</th>")
                    .append("<th>Impressão Digital (SHA-1)</th>")
                    .append("</tr></thead><tbody>");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (CertModel c : data) {
                String statusClass = "status-ok";
                String statusText = "VÁLIDO";

                if (!c.isValid()) {
                    statusClass = "status-expired";
                    statusText = "EXPIRADO";
                } else if (c.getDaysRemaining() < 30) {
                    statusClass = "status-warning";
                    statusText = "EXPIRA EM BREVE";
                }

                html.append("<tr>");

                // Status Column
                html.append("<td style='width: 120px;'><span class='status-badge ").append(statusClass).append("'>")
                        .append(statusText).append("</span><br><small>")
                        .append(c.getDaysRemaining()).append(" dias restantes</small></td>");

                // File/Alias
                html.append("<td><strong>").append(c.getAlias()).append("</strong><br>")
                        .append("<span class='meta'>").append(c.getFilePath()).append("</span></td>");

                if (c.getError() != null) {
                    html.append("<td colspan='3' style='color: var(--danger);'><b>ERRO AO LER CERTIFICADO:</b> ")
                            .append(c.getError()).append("</td>");
                } else {
                    // Issuer
                    html.append("<td>").append(c.getIssuer()).append("</td>");

                    // Validity
                    html.append("<td>De: ").append(sdf.format(c.getNotBefore()))
                            .append("<br>Até: <b>").append(sdf.format(c.getNotAfter())).append("</b></td>");

                    // Checksum
                    String ck = c.getChecksum();
                    if (ck != null && ck.length() > 20) {
                        ck = ck.substring(0, 20) + "...";
                    }
                    html.append("<td style='font-family: monospace;'>").append(ck).append("</td>");
                }
                html.append("</tr>");
            }
            html.append("</tbody></table>");
        }

        html.append("</div></body></html>");

        Files.write(Paths.get(this.outputPath), html.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}