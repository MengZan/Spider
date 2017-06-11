package cn.mayzan.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class DoubanSpider implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(3000)
			.setUserAgent("Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");;

	// 列表页正则表达式
	private final static String URL_LIST = "https://movie.douban.com/top250\\?start=\\w+&filter=";

	// 详情页正则表达式
	private final static String URL_POST = "https://movie.douban.com/subject/\\w+";

	public void process(Page page) {
		// 列表页
		if (page.getUrl().regex(URL_LIST).match()) {
			page.addTargetRequests(page.getHtml().xpath("//div[@class='hd']").links().regex(URL_POST).all());
			page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
		}
		// 详情页
		else {
			String title = page.getHtml().xpath("//span[@property='v:itemreviewed']/text()").toString();
			String imgUrl = page.getHtml().xpath("//div[@id='mainpic']").css("img", "src").toString();
			String point = page.getHtml().xpath("//strong [@property='v:average']/text()").toString();
			page.putField("title", title);
			page.putField("point", point);
			page.putField("imgUrl", imgUrl);
			try {
				// 根据图片URL 下载图片方法
				// String 图片URL地址, String 图片名称, String 保存路径
				DownloadImage.download(imgUrl, point + title + ".jpg", "C:\\Users\\27538\\Desktop\\11");
			} catch (Exception e) {
				System.out.println("下载失败：" + title);
				e.printStackTrace();
			}
		}
	}

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new DoubanSpider()).addUrl("https://movie.douban.com/top250?start=50&filter=")
				.addPipeline(new JsonFilePipeline("C:\\Users\\27538\\Desktop\\11")).thread(5).run();

		// Pattern pattern = Pattern.compile(URL_LIST);
		// Matcher matcher =
		// pattern.matcher("https://movie.douban.com/top250?start=50&filter=");
		// System.out.println(matcher.matches());
	}
}
