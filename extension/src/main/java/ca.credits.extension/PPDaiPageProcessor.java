package ca.credits.extension;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * Created by chenwen on 16/9/12.
 */
@Slf4j
public class PPDaiPageProcessor implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {

        log.info(page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/div/ul/li").regex("[0-9]+").all().toString());

//        log.info(page.getRawText());
        log.info(page.getHtml().links().regex(".*/blacklist/.*").all().toString());

        log.info(page.getHtml().xpath("//input[@onclick]").regex("/blacklistdetail/pdu[0-9]+").all().toString());

        List<String> detailList = page.getHtml().xpath("//input[@onclick]").regex("/blacklistdetail/pdu[0-9]+").all();
        detailList.stream().forEach(detail -> page.addTargetRequest(String.format("http://www.ppdai.com%s",detail)));
        List<String> all = page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/div/ul/li").regex("[0-9]+").all();
        all.stream().forEach(node -> page.addTargetRequest(String.format("http://www.ppdai.com/blacklist/%s_m0",node)));
        page.addTargetRequests(page.getHtml().links().regex(".*/blacklist/.*").all());

//        page.addTargetRequests(page.getHtml().xpath("//@onclick").all());

//        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name") == null) {
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
//
//        // 部分三：从页面发现后续的url地址来抓取
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());


        if (page.getRequest().getUrl().startsWith("http://www.ppdai.com/blacklistdetail")){
            page.putField("name",page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()[2]").get());
            page.putField("mobile",page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()[3]").get());
            page.putField("id_card",page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()[4]").get());
            page.putField("start_time",page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[1]/div[2]/table/tbody/tr[3]/td/div/table/tbody/tr[2]/td[3]").get());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new PPDaiPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.ppdai.com/blacklist")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }
}
