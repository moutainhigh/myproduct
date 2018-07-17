package zhumeng.com.uimei.web.wx;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import zhumeng.com.uimei.common.ConfigUtil;
import zhumeng.com.uimei.model.dbo.wx.TWxApp;
import zhumeng.com.uimei.model.dbo.wx.TWxImageText;
import zhumeng.com.uimei.model.dbo.wx.TWxMenu;
import zhumeng.com.uimei.model.dbo.wx.TWxReply;
import zhumeng.com.uimei.service.wx.TWxAppService;
import zhumeng.com.uimei.service.wx.TWxImageTextService;
import zhumeng.com.uimei.service.wx.TWxMenuService;
import zhumeng.com.uimei.service.wx.TWxReplyService;
import zhumeng.com.uimei.service.wx.impl.WxMenuAndwxReplyAndwxImageTextService;
import zhumeng.com.uimei.web.AppBaseController;
import zhumeng.com.uimei.web.wxserver.entity.menu.Menu;
import zhumeng.com.uimei.web.wxserver.util.AccessToken;
import zhumeng.com.uimei.web.wxserver.util.CommApiUtils;
import zhumeng.com.uimei.web.wxserver.util.NewMenuUtil;
import zhumeng.com.uimei.web.wxserver.util.WeixinUtil;

@Controller
@RequestMapping(value = "${backPath}/weixinMenu")
public class WeixinMenuController extends AppBaseController{
	
	public final static Logger log = LoggerFactory.getLogger(WeixinMenuController.class);
	
	@Autowired
	private TWxMenuService wxMenuService;
	
	@Autowired
	private TWxReplyService wxReplyService;
	
	@Autowired
	private WxMenuAndwxReplyAndwxImageTextService wxMenuAndwxReplyAndwxImageTextService;
	
	@Autowired
	private TWxImageTextService wxImageTextService;
	
	@Autowired
	private TWxAppService wxAppService;
	
	/**
	 * 跳转微信菜单 管理页面(新添加)
	 * @param tWxMenu
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "beforeAddWxMenu")
	public String beforeAddWxMenu(TWxMenu tWxMenu,Long wxAppId, Model model) throws Exception{
		
		TWxMenu tWxMenuParam = new TWxMenu();
		tWxMenuParam.setMenuLevel(1);
		//父 id为0
		tWxMenuParam.setParentId(0L);
		tWxMenuParam.setIsUse("1");
		tWxMenuParam.setAppId(wxAppId);
		//一级菜单
		List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
		model.addAttribute("list", list);
		
//		List<TWxMenu> menus = new ArrayList<TWxMenu>();
		List<TWxMenu> menus = null;
		try {
			tWxMenuParam = new TWxMenu();
			tWxMenuParam.setAppId(wxAppId);
			menus = wxMenuService.findList(tWxMenuParam);

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("menu", menus!=null?menus:new ArrayList<TWxMenu>());
		model.addAttribute("wxAppId", wxAppId);
		return "wx/wx_menu_list";
	}
	
	
	/**
	 * 跳转微信菜单 管理页面(编辑)
	 * @param tWxMenu
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "beforeEditWxMenu")
	public String beforeEditWxMenu(Long id,Long wxAppId, Model model) throws Exception{
		if (id==null) {
			log.info("参数id 为空");
			return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
		}
		//需要编辑的一级菜单
		TWxMenu m = wxMenuService.findByKey(id);
		model.addAttribute("m", m);
		
		//父 id为0的所有 使用中的 一级菜单
		TWxMenu tWxMenuParam = new TWxMenu();
		tWxMenuParam.setMenuLevel(1);
		tWxMenuParam.setParentId(0L);
		tWxMenuParam.setIsUse("1");
		tWxMenuParam.setAppId(wxAppId);
		List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
		model.addAttribute("list", list!=null?list:new ArrayList<TWxMenu>());
		
		//所有菜单
		List<TWxMenu> menus = new ArrayList<TWxMenu>();
		try {
			tWxMenuParam = new TWxMenu();
			tWxMenuParam.setAppId(wxAppId);
			menus.addAll(wxMenuService.findList(tWxMenuParam));

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("menu", menus);
		//所编辑菜单 的 对应回复	
		TWxReply tWxReply = new TWxReply();
		tWxReply.setMsgKey(m.getId().toString());
		tWxReply.setAppId(wxAppId);
		tWxReply.setMsgEvent("1");
		List<TWxReply> res = wxReplyService.findList(tWxReply);
		if(res!=null && res.size()>0){
			model.addAttribute("res", res.get(0));
		}
		model.addAttribute("wxAppId", wxAppId);
		return "wx/wx_menu_list";
	}
	

	/**
	 * 保存菜单
	 * @param tWxMenu
	 * @param model
	 * @param redirectAttributes
	 * @param msgType 菜单类型是 2（事件类型 时）  的 消息回复类型
	 * @param sid 回复语id
	 * @param mid 素材id
	 * @param content 具体回复的内容
	 * @return
	 */
	@RequestMapping(value = "saveWxMenu")
	public String saveWxMenu(TWxMenu tWxMenu, Model model, 
			RedirectAttributes redirectAttributes,String msgType,Long sid,Long mid,String content,Long wxAppId){
	try {
			tWxMenu.setAppId(wxAppId);
			/*判断是否是一级菜单*/
			if (tWxMenu.getMenuLevel() == 1) {
				TWxMenu tWxMenuParam = new TWxMenu();
				tWxMenuParam.setMenuLevel(1);
				tWxMenuParam.setIsUse("1");
				tWxMenuParam.setAppId(wxAppId);
				List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
				int i = 0;
				for (TWxMenu mm : list) {
					if (!mm.getId().equals(tWxMenu.getId()))
						i++;
				}
				if (i >= 3) {
					addMessage(redirectAttributes, "微信规定一级菜单只能为三个，您现在可用的一级菜单已为3个");
					return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
				}
				tWxMenu.setParentId(0L);
			}
			/*判断是否是二级菜单*/
			if (tWxMenu.getMenuLevel() == 2) {
				TWxMenu tWxMenuParam = new TWxMenu();
				/*
				 * 这里需要注意测试 是否获取到父id
				 **/
//				tWxMenuParam.setParent(tWxMenu.getParent());
				tWxMenuParam.setParentId(tWxMenu.getParentId());
				tWxMenuParam.setMenuLevel(2);
				tWxMenuParam.setIsUse("1");
				tWxMenuParam.setAppId(wxAppId);
				List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
				int i = 0;
				for (TWxMenu mm : list) {
					if (!mm.getId().equals(tWxMenu.getId()))
						i++;
				}
				if (i >= 5) {
					addMessage(redirectAttributes, "一个一级菜单下只能有最多5个二级菜单，请先删除或停用再添加");
					return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
				}
			}
			/*假如是一级菜单 启用，全部的子菜单都启用*/
			if (tWxMenu.getIsUse().equals("1") && tWxMenu.getMenuLevel() == 1 && tWxMenu.getId()!=null) {
				TWxMenu tWxMenuParam = new TWxMenu();
//				tWxMenuParam.setParent(tWxMenu);
				tWxMenuParam.setParentId(tWxMenu.getParentId());
				tWxMenuParam.setAppId(wxAppId);
				List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
				for (int i = 0; list != null && i < list.size(); i++) {
					TWxMenu menu = list.get(i);
					if (menu.getIsUse().equals("0")) {
						menu.setIsUse("1");
						wxMenuService.save(menu);
					}
				}
			}
			/*假如是一级菜单 停用，全部的子菜单都停用*/
			if (tWxMenu.getIsUse().equals("0") && tWxMenu.getMenuLevel() == 1 && tWxMenu.getId()!=null) {
				TWxMenu tWxMenuParam = new TWxMenu();
//				tWxMenuParam.setParent(tWxMenu);
				tWxMenuParam.setParentId(tWxMenu.getParentId());
				tWxMenuParam.setAppId(wxAppId);
				List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
				for (int i = 0; list != null && i < list.size(); i++) {
					TWxMenu menu = list.get(i);
					if (menu.getIsUse().equals("1")) {
						menu.setIsUse("0");
						wxMenuService.save(menu);
					}
				}
			}
			//1视图，2点击事件
			if ("1".equals(tWxMenu.getMenuType())) {//视图菜单
				if (tWxMenu.getUrl().indexOf("http://") < 0) {
					tWxMenu.setUrl("http://" + tWxMenu.getUrl());
				}
				wxMenuService.save(tWxMenu);
				addMessage(redirectAttributes, "保存微信菜单成功");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
			}else if ("3".equals(tWxMenu.getMenuType())) {//3扫码
				if (tWxMenu.getUrl().indexOf("http://") < 0) {
					tWxMenu.setUrl("http://" + tWxMenu.getUrl());
				}
				wxMenuService.save(tWxMenu);
				addMessage(redirectAttributes, "保存微信菜单成功");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
			}else if ("4".equals(tWxMenu.getMenuType())) {//4跳转小程序
				wxMenuService.save(tWxMenu);
				addMessage(redirectAttributes, "保存微信菜单成功");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
				
			}else if ("2".equals(tWxMenu.getMenuType()) || "5".equals(tWxMenu.getMenuType())) {//事件菜单 //5扫码接收消息
				tWxMenu.setUrl("");
				TWxReply  wxReply = new TWxReply();
				wxReply.setAppId(wxAppId);
				wxReply.setId(sid);
				wxReply.setMsgType(msgType);
				wxReply.setReplyMsg(content);
				wxReply.setMsgEvent("1");
				if("1".equals(wxReply.getMsgType())){//文本
					wxMenuAndwxReplyAndwxImageTextService.saveWxMenuAndWxReply(tWxMenu,wxReply);
					addMessage(redirectAttributes, "保存微信菜单成功");
					return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
				}
				if ("2".equals(wxReply.getMsgType())) {// 图文消息
					TWxImageText wxImageText = wxImageTextService.findByKey(mid);
					if(wxImageText==null){
						addMessage(redirectAttributes, "找不到素材");
						return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
					}
					wxReply.setReplyMsg(wxImageText.getId().toString());
					wxMenuAndwxReplyAndwxImageTextService.saveWxMenuAndWxReply(tWxMenu,wxReply);
					addMessage(redirectAttributes, "保存微信菜单成功");
					return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "保存微信菜单失败");
		return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
	}
	

	@RequestMapping(value = "deleteWxMenu")
	public String deleteWxMenu(Long id,RedirectAttributes redirectAttributes,Integer wxAppId) throws Exception {

		if (id==null) {
			addMessage(redirectAttributes, "找不到该微信菜单");
			return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
		}
		wxMenuService.delete(id);
		addMessage(redirectAttributes, "删除菜单成功");
		return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
	}
	
	
	/**
	 * 发布微信菜单
	 */
	@RequestMapping(value = "publicWxMenu")
	public String publicWxMenu(RedirectAttributes redirectAttributes,Long wxAppId){
		//1、查询所有可以使用的菜单
		try{
			TWxMenu tWxMenuParam = new TWxMenu();
			tWxMenuParam.setAppId(wxAppId);
			tWxMenuParam.setIsUse("1");
			List<TWxMenu> list = wxMenuService.findList(tWxMenuParam);
			Menu menu = NewMenuUtil.getMenu(list);
			//2、查询所有可以使用的菜单
			TWxApp wxApp =wxAppService.findByKey(wxAppId);
			AccessToken accessToken = CommApiUtils.getAccessToken(wxApp);
			if(accessToken==null){
				addMessage(redirectAttributes, "获取accessToken失败");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
			}
			int result = WeixinUtil.createMenu(menu, accessToken.getToken());
			if(0==result){
				addMessage(redirectAttributes, "发布菜单成功");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "发布菜单失败");
		return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
		
	}
	
	/**
	 *删除微信菜单
	 */
	@RequestMapping(value = "releaseMenu")
	public String releaseMenu(RedirectAttributes redirectAttributes,Long wxAppId){
		try{
			TWxApp wxApp =wxAppService.findByKey(wxAppId);
			AccessToken accessToken = CommApiUtils.getAccessToken(wxApp);
			if(accessToken==null){
				addMessage(redirectAttributes, "获取accessToken失败");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
			}
			int result = WeixinUtil.deleteMenu(accessToken.getToken());
			if(0==result){
				addMessage(redirectAttributes, "删除菜单成功");
				return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "删除菜单失败");
		return "redirect:"+ConfigUtil.get("backPath")+"/weixinMenu/beforeAddWxMenu?wxAppId="+wxAppId;
	}

}
