package com.jzy.game.plugin.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

/**
 * freemarker工具
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月31日 上午10:27:31
 */
public class FreeMarkerUtil {

	public static Template getTemplate(Class<?> clazz,String name,String ftlPath) {
		try {
			// 通过Freemaker的Configuration读取相应的ftl
			Configuration cfg = new Configuration(new Version(2, 3, 23));
			// 设定去哪里读取相应的ftl模板文件
			cfg.setClassForTemplateLoading(FreeMarkerUtil.class, ftlPath);
			// 在模板文件目录中找到名称为name的文件
			Template temp = cfg.getTemplate(name);
			return temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 控制台输出
	 * 
	 * @param name
	 * @param root
	 */
	public static void print(String name, Map<String, Object> root) {
		try {
			// 通过Template可以将模板文件输出到相应的流
			Template temp = getTemplate(FreeMarkerUtil.class,name,"/ftl");
			temp.process(root, new PrintWriter(System.out));
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出到文件
	 * 
	 * @param name 模板名称
	 * @param root 参数
	 * @param outFile 输出文件
	 * @param ftlPath 模板路径
	 */
	public static void writeToFile(String name, Map<String, Object> root, String outFile,String ftlPath) {
		FileWriter out = null;
		try {
			// 通过一个文件输出流，就可以写到相应的文件中，此处用的是绝对路径
			out = new FileWriter(new File(outFile));
			Template temp = getTemplate(FreeMarkerUtil.class,name,ftlPath);
			temp.process(root, out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
