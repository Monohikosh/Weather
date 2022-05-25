import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Document getPage() throws IOException {
        String url = "http://pogoda.spb.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDateFromString(String stringDate) throws Exception{
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()){
            return matcher.group();
        }
        throw new Exception("Can't extract date from string!");
    }
    private static int printFourValues(Elements values, int index){
        int iterationCount = 4;
        if (index == 0){
            Element valueLn = values.get(3);
            boolean isMorning = valueLn.text().contains("Утро");
            boolean isDay = valueLn.text().contains("День");
            boolean isEvening = valueLn.text().contains("Вечер");
            boolean isNight = valueLn.text().contains("Ночь");
            if (isMorning) iterationCount = 3;
            if (isDay) iterationCount = 2;
            if (isEvening) iterationCount = 1;
            if (isNight) iterationCount = 0;
        }
        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            int j = 0;
            for (Element td : valueLine.select("td")) {
                switch (j) {
                    case 0:
                        System.out.print(td.text());
                        break;
                    case 1:
                        System.out.format("%65s", td.text());
                        break;
                    case 2:
                        System.out.print("\t\t\t\t" + td.text());
                        break;
                    case 3:
                        System.out.format("%12s", "\t\t\t" + td.text());
                        break;
                    case 4:
                        System.out.format("%25s", td.text());
                        break;
                    case 5:
                        System.out.print("\t\t\t" + td.text());
                        break;
                }
                ++j;
            }
            System.out.println();
        }

        return iterationCount;
    }

    public static void main(String[] args) throws Exception {
        Document page = getPage();
        Element tableWth = page.select("table[class=wt]").first();
        Elements names = tableWth.select("tr[class=wth]");
        Elements values = tableWth.select("tr[valign=top]");
        int index = 0;
        for (Element name : names){
            String dateString = name.select("th[id=dt]").text();
            String date = getDateFromString(dateString);
            System.out.println(date + "\t\t\t\t\t\t\t\t\t\tЯвления\t\t\t\t\t\t\t\t\tТемпераутра" +
                    "\t\t\t  Давление\t\t\t\t  Влажность" +
                    "\t\t\tВетер");
            int iterationCount = printFourValues(values, index);
            index += iterationCount;
        }
    }
}
