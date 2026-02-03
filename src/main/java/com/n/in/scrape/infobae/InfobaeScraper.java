package com.n.in.scrape.infobae;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InfobaeScraper {

    public static void main(String[] args) throws Exception {

        String url = "https://www.infobae.com/tendencias/";

        Document home = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get();

        Elements cards = home.select("a.story-card-ctn");

        for (Element card : cards) {

            String link = card.attr("href");
            if (link.startsWith("/")) {
                link = "https://www.infobae.com" + link;
            }

            System.out.println("➡ Entrando a: " + link);

            scrapeArticle(link);

            System.out.println("======================================");
            Thread.sleep(1500); // 🛑 importante: no quemar el sitio
        }
    }

    private static void scrapeArticle(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            String titulo = textOrEmpty(doc.selectFirst("h1"));
            String bajada = textOrEmpty(doc.selectFirst("h2"));
            String fecha = textOrEmpty(doc.selectFirst("time"));

            // Cuerpo del artículo
            Element body = doc.selectFirst("div.article-body");
            StringBuilder contenido = new StringBuilder();

            if (body != null) {
                for (Element p : body.select("p")) {
                    contenido.append(p.text()).append("\n\n");
                }
            }

            System.out.println("📰 Título: " + titulo);
            System.out.println("📝 Bajada: " + bajada);
            System.out.println("📅 Fecha: " + fecha);
            System.out.println("📄 Contenido:");
            System.out.println(contenido.toString());

        } catch (Exception e) {
            System.out.println("❌ Error al scrapear " + url);
        }
    }

    private static String textOrEmpty(Element el) {
        return el != null ? el.text() : "";
    }
}
