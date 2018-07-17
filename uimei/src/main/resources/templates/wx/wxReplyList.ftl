<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>自定义回复</title>
<script src="${basePath}/wx/js/jquery-1.9.1.min.js" type="text/javascript"></script>
<link href="${basePath}/wx/css/sub_style.css" rel="stylesheet" type="text/css" />
<link href="${basePath}/wx/css/jquery-ui.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="${basePath}/wx/css/demo.css?i=013" type="text/css"/>
<link rel="stylesheet" href="${basePath}/wx/css/zTreeStyle/zTreeStyle.css" type="text/css"/>
<script src="${basePath}/wx/js/jquery-ui.js" type="text/javascript"></script>
<script src="${basePath}/wx/js/emotion.js" type="text/javascript"></script>
<script type="text/javascript" src="${basePath}/wx/js/jquery.ztree.core-3.5.js"></script>
<script type="text/javascript">
 function delByid(id){
	 	if(confirm("是否要删除数据？\r\n删除后，数据不可恢复！")){
	 		jump("${basePath}/${backPath}/wxReply/delete?id="+id+"&wxAppId="+document.getElementById("wxAppId").value);
	 	}
	 }
function jump(url){
		var e = document.createElement("a");
		e.href = url;
		document.body.appendChild(e);
		e.click();
	}
	function addNews(){
	 	jump("${basePath}/${backPath}/wxReply/beforeAddWxReply?wxAppId="+document.getElementById("wxAppId").value);
	 }
	 function addDefault(){
	 	jump("${basePath}/${backPath}/wxReply/defaultWxReply?wxAppId="+document.getElementById("wxAppId").value);
	 }
	 function addSubscribe(){
	 	jump("${basePath}/${backPath}/wxReply/subscribeWxReply?wxAppId="+document.getElementById("wxAppId").value);
	 }
</script>
</head>
<body>
  <div class="Mix_W600">
  	<input type="hidden" value="${(wxAppId)!}" id="wxAppId"  name="wxAppId"/>
    ${(message)!}
    <div id="submain" style="width:1036px;margin-top: 40px;">
        <h1 class="title co1">
          	  自定义回复管理</h1>
        <div class="tab top">
            <table border="0" cellspacing="0" class="table_1">
                <thead>
                    <tr class="bgco1 co1">
                        <th>
                            关键字
                        </th>
                        <th style="width: 150px;">
                            回复类型
                        </th>
                        <th style="width: 120px;">
                            操作
                        </th>
                    </tr>
                </thead>
                 <tbody class="co1">
                 <#list  p.list as m>
					<tr <#if m_index%2==0 >class="evenRow"</#if><#if m_index%2==1>class="oddRow"</#if>>
						<td>${(m.msgKey)!}</td>
						<td><#if m.msgType=='1'>文本</#if>
						<#if m.msgType=='2'>图文</#if>
						</td>
						<td>
						 [<a href="${basePath}/${backPath}/wxReply/beforeEditWxReply?id=${m.id }&wxAppId=${wxAppId}">编辑</a>] [<a  id="cph_RepeaterList_lbtnDel_2" title="删除" class="Del" href="javascript:delByid('${m.id }')">删除</a>]
						
						</td>
					</tr>
					</#list>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" class="r">
                        	<input type="button" name="ctl00$cph$btnAdd" value="默认回复" onclick="addDefault();" id="cph_btnAdd" class="greenbtn50 mR20" style="width:65px;background:#0088cc;border: 1px solid #0088cc;" />
                            <input type="button" name="ctl00$cph$btnAdd" value="订阅回复" onclick="addSubscribe();" id="cph_btnAdd" class="greenbtn50 mR20" style="width:65px;background:#0088cc;border: 1px solid #0088cc;"/>
                            <input type="button" name="ctl00$cph$btnAdd" value="新增" onclick="addNews();" id="cph_btnAdd" class="greenbtn50 mR20" style="background:#0088cc;border: 1px solid #0088cc;"/>
                        </td>
                    </tr>
                </tfoot>
                
                </table>
                <div class="page top mainbottom20 c">
                <a href="${basePath}/${backPath}/wxReply/list?pageNo=${p.prePage}&wxAppId=${(wxAppId)!}">上一页</a><a  class="selected hover">${p.pageNum }</a><a href="${basePath}/${backPath}/wxReply/list?pageNo=${p.nextPage}&wxAppId=${(wxAppId)!}"}">下一页</a>
            </div>
                </div>
</div></div>
</body>
</html>
