package zhumeng.com.uimei.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import zhumeng.com.uimei.common.ResponseUtils;
import zhumeng.com.uimei.common.util.RequestUtil;
import zhumeng.com.uimei.common.util.StringUtil;

import com.alibaba.fastjson.JSONObject;
 
/**
 * Controller基类 
 */
public abstract class AppBaseController extends RequestUtil {

	@Autowired
	protected  HttpServletRequest request;
	@Autowired
	protected  HttpServletResponse respone;
	
	public final static Logger log = LoggerFactory.getLogger(AppBaseController.class);
			

	
	
	public static final String STATUS = "status";
	public static final String WARN = "warn";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String FAIL  = "fail";
	public static final String MESSAGE = "message";
	
	public static final String CART_COUNT_COOKIE = "cartCountCookie";
	
	/**保存未登录会员购物车项集合的Cookie最大有效时间（单位：秒）*/
	public static final int CARTITEM_LIST_COOKIE_MAX_AGE = 72000;
	
	/**
	 * 将对象转成json字符串，并组装成jsonp格式
	 * @param obj javaBeand对象
	 * @return    jsonp格式字符串
	 */
	protected String pakageJsonps(Object obj) { 
		String callbackFun = request.getParameter("callbackFun");
		String result = "";
		if(obj instanceof JSONObject ){
			 result = obj.toString();
		}else{
			 result = JSONObject.toJSONString(obj);
		}
		log.info("返回參數:"+result);
		
		if (StringUtils.isBlank(callbackFun)) {
			return result;
		}
		String jsonpCallback = callbackFun+"("+  result  +");";
		return jsonpCallback;
	}
	
	/**
	 */
	protected String pakageJsonp(Object obj) { 
		String result = "";
		if(obj instanceof JSONObject ){
			 result = obj.toString();
		}else{
			 result = JSONObject.toJSONString(obj);
		}
		log.info("返回參數:"+result);
		ResponseUtils.renderJson(respone, result);
		return null;
	}
	
	/**
	 * 获取用户id
	 * @author   : zdh
	 * @date     :
	 * @version  : 1.0  
	 * @return   :
	 */
	public Long getMemberId(){
		Object beanFans = request.getSession().getAttribute("beanFans");
		if(StringUtil.checkObj(beanFans)){
		}
		return null;
	}
	/**
	 * 获取用户openId
	 * @author   : zdh
	 * @date     :
	 * @version  : 1.0  
	 * @return   :
	 */
	public String getOpenId(){
		Object beanFans = request.getSession().getAttribute("beanFans");
		if(StringUtil.checkObj(beanFans)){
		}
		return null;
	}
	
	/**
	 * 添加Cookie
	 * @autor wanghongkai
	 * @param name 名称
	 * @param value 值
	 * @param maxAge 最大有效时间(秒)
	 */
	public void addCookie(String name,String value,Integer maxAge){
		Cookie cookie = new Cookie(name,value);
		cookie.setPath(request.getContextPath() + "/");
		if(maxAge>0){
			cookie.setMaxAge(maxAge);
		}
		respone.addCookie(cookie);
		
	}
	
	// 获取当前请求的全路径 
	public String getRequestURL(){
		StringBuffer sburl = new StringBuffer();
		sburl.append(request.getScheme()+"://");
		sburl.append(request.getHeader("host"));
		sburl.append(request.getRequestURI());
		if(request.getQueryString()!=null) //判断请求参数是否为空  
			sburl.append("?"+request.getQueryString());   // 参数  	 
			return sburl.toString();
	}
	
	protected String getRequestString(){
		BufferedReader br;
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			while ((line = br.readLine()) != null){
				sb.append(line);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 line = sb.toString();
		 return line;
	}
	
	/**
	 * 添加Flash消息
	 * @param message
	 */
	protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		redirectAttributes.addFlashAttribute("message", sb.toString());
	}
}
