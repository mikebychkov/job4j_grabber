package com.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ParseSqlRu implements Parse {

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                rsl.add(detail(href.attr("href")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post detail(String link) {
        Random rnd = new Random();
        int newId = rnd.nextInt();
        Calendar calendar = Calendar.getInstance();
        String name = "";
        String text = "";
        try {
            Document doc = Jsoup.connect(link).get();
            //
            Element elTable = doc.selectFirst(".msgTable");
            //
            Element elHead = elTable.selectFirst(".messageHeader");
            name = elHead.childNode(1).toString();
            name = normaliseStr(name);
            //
            Elements els = elTable.select(".msgBody");
            for (Element el : els) {
                if (el.attributes().toString().contains("width")) {
                    continue;
                }
                text = el.text();
                break;
            }
            //
            Element elFooter = doc.selectFirst(".msgFooter");
            String created = elFooter.childNode(0).toString();
            created = normaliseStr(created);
            calendar = parseDate(created);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Post(newId, name, text, link, calendar);
    }

    private static String normaliseStr(String str) {
        return str.replace("&nbsp;", "")
                .replace("[", "")
                .replace("]", "")
                .trim();
    }

    private static Calendar parseDate(String str) {
        str = str.replace(",", "");
        String[] strData = str.split(" ");
        int year = 0;
        int month = 0;
        int day = 0;
        int hh = 0;
        int mm = 0;
        int ss = 0;
        Calendar calendar = Calendar.getInstance();
        if (str.contains("сегодня") || str.contains("вчера")) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            if (str.contains("вчера")) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hh = Integer.parseInt(strData[1].substring(0, 2));
            mm = Integer.parseInt(strData[1].substring(3));
        } else {
            day = Integer.parseInt(strData[0]);
            month = monthValue(strData[1]) - 1;
            year = Integer.parseInt(strData[2]) + 2000;
            hh = Integer.parseInt(strData[3].substring(0, 2));
            mm = Integer.parseInt(strData[3].substring(3));
        }
        calendar.set(year, month, day, hh, mm, ss);
        return calendar;
    }

    private static int monthValue(String str) {
        switch (str) {
            case ("янв"):
                return 1;
            case ("фев"):
                return 2;
            case ("мар"):
                return 3;
            case ("апр"):
                return 4;
            case ("май"):
                return 5;
            case ("июн"):
                return 6;
            case ("июл"):
                return 7;
            case ("авг"):
                return 8;
            case ("сен"):
                return 9;
            case ("окт"):
                return 10;
            case ("ноя"):
                return 11;
            case ("дек"):
                return 12;
            default:
                return 0;
        }
    }
}
