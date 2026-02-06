package com.n.in.scrape.infobae;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfobaeTecnoService {

    private static final String BASE_URL = "https://www.infobae.com";
    private static final String TECNO_URL = BASE_URL + "/tecno/";
    private static final int TIMEOUT = 10_000;

    // 🔥 ÚNICO MÉTODO PÚBLICO
    public List<String> scrapeTecno() throws Exception {

        List<String> articulos = new ArrayList<>();

        Document home = Jsoup.connect(TECNO_URL)
                .userAgent("Mozilla/5.0")
                .timeout(TIMEOUT)
                .get();

        Elements cards = home.select("a.story-card-ctn");

        for (Element card : cards) {

            String link = card.attr("href");
            if (link.startsWith("/")) {
                link = BASE_URL + link;
            }

            String articulo = scrapeArticle(link);
            if (articulo != null && !articulo.isBlank()) {
                articulos.add(articulo);
            }

            Thread.sleep(1500); // 🛑 no quemar Infobae
        }

        return articulos;
    }

    /* =======================
       LÓGICA INTERNA
       ======================= */

    private String scrapeArticle(String url) {

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(TIMEOUT)
                    .get();

            String titulo = textOrEmpty(doc.selectFirst("h1"));
            String bajada = textOrEmpty(doc.selectFirst("h2"));
            String fecha  = textOrEmpty(doc.selectFirst("time"));

            Element body = doc.selectFirst("div.article-body");
            StringBuilder contenido = new StringBuilder();

            if (body != null) {
                for (Element p : body.select("p")) {
                    contenido.append(p.text()).append("\n\n");
                }
            }

            return new StringBuilder()
                    .append("📰 Título: ").append(titulo).append("\n")
                    .append("📝 Bajada: ").append(bajada).append("\n")
                    .append("📅 Fecha: ").append(fecha).append("\n")
                    .append("📄 Contenido:\n")
                    .append(contenido)
                    .toString();

        } catch (Exception e) {
            return null;
        }
    }

    private String textOrEmpty(Element el) {
        return el != null ? el.text() : "";
    }
}
