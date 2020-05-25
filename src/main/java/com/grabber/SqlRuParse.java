package com.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {

    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        Elements row2 = doc.select(".altCol").select("[style]");
        for (int count = 0; count < row.size(); count++) {
            Element td = row.get(count);
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            //
            Element td2 = row2.get(count);
            System.out.println(td2.childNodes().get(0));
        }
    }
}
