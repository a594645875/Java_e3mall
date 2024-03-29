package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.item.controller.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class HtmlGenListener implements MessageListener{

	@Autowired
	private ItemService itemService;
	
	@Value("${HTML_GEN_PATH}")
	private String HTML_GEN_PATH;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Override
	public void onMessage(Message message) {
		try {
			//创建一个模板,参考jsp
			//从消息中取商品ID
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			Long id =new Long(text);
			//等待事务提交
			Thread.sleep(1000);
			//根据商品id查询商品信息，商品基本信息和商品描述。
			TbItem tbItem = itemService.getItemById(id);
			Item item = new Item(tbItem);
			//取商品描述
			E3Result e3Result2 = itemService.selectTbItemDesc(id);
			TbItemDesc tbItemDesc = (TbItemDesc) e3Result2.getData();
			//创建一个数据集，把商品数据封装
			Map dataModel = new HashMap<>();
			dataModel.put("item", item);
			dataModel.put("itemDesc", tbItemDesc);
			//加载模板对象
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			//创建一个输出流，指定输出的目录及文件名。
			Writer out = new FileWriter(new File(HTML_GEN_PATH + id + ".html"));
			//生成静态页面。
			template.process(dataModel, out);
			//关闭流
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
