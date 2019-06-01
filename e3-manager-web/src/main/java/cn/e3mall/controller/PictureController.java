package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;
@Controller
public class PictureController {
	
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	//@Value("${Tracker_Server}") 会出现中文乱码
	//private String Tracker_Server;
	
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String fileUpload(MultipartFile uploadFile) {
		try {
			//1、取文件的扩展名 
			//1.1取完整名字
			String originalFileName = uploadFile.getOriginalFilename();
			//取扩展名
			String extName = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
			//2、创建一个FastDFS的客户端
			FastDFSClient fastDFSClient = new FastDFSClient("E:/java课程/workspace/JAVAEE32/e3-manager-web/src/main/resources/conf/clinet.conf");
			//3、执行上传处理
			String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			//4、拼接返回的url和ip地址，拼装成完整的url
			String url = IMAGE_SERVER_URL + path;
			//5、返回map
			Map result = new HashMap<>();
			result.put("error", 0);
			result.put("url", url);
			String json = JsonUtils.objectToJson(result);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			//5、返回map
			Map result = new HashMap<>();
			result.put("error", 1);
			result.put("message", "图片上传失败");
			String json = JsonUtils.objectToJson(result);
			return json;
		}
	}
	
	
}
