<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- <script type="text/javascript"src="js/echartsjs/echarts.js"> </script> -->

<link type="text/css" href="/js/slide/style.css" rel="stylesheet" />
<script type="text/javascript">
	$(function() {
		/* alert(window.screen.height); 
		alert(window.screen.width); */
		/* alert(window.screen.availHeight); */
		/* alert(window.screen.availWidth); */
		$('.exportExcel')
				.click(
						function() {
							$('#depbusanyList #pagerForm input[name=export]')
									.val(1);
							$(
									'#depbusanyList #pagerForm input[name=exportType]')
									.val($(this).attr('exportType'));
							$('#depbusanyList #pagerForm').submit();
							$('#depbusanyList #pagerForm input[name=export]')
									.val('');
							$(
									'#depbusanyList #pagerForm input[name=exportType]')
									.val('');
						});
	});
	/* $(function() {
		$('#depbusanyList .search').click(function() {
			divSearch($('#depbusanyList #pagerForm'), 'depbusanyList');
		});
	}); */
	$(function(){
		var obj=document.getElementById("main");
		var objWidth=document.getElementById("chartWidth").value;
		obj.style.height=window.screen.availHeight*0.70+'px';
		if($('#chartType').val()==1){
			obj.style.width=window.screen.availWidth*0.75+'px';
		}else{
			if(objWidth<window.screen.availWidth*0.75){
				obj.style.width=window.screen.availWidth*0.75+'px';
			}else{
				if(objWidth>=32767)
					objWidth = 32767
				obj.style.width=objWidth+'px';
			}
		}
		// if($('#chartType').val()==-1)
		/* alert($('#chartType').val()); */
		$('#depbusanyList .search').click(function(){
			
			var beginDate = $('#beginDate').val();
			var endDate = $('#endDate').val();
			if (beginDate != "" && endDate != "") {
				if (beginDate >= endDate) {
					alertMsg.error('查询开始时间不能大于查询结束时间');
				}
				else{
					divSearch($('#depbusanyList #pagerForm'),'depbusanyList');
						}
			}
			else{
				divSearch($('#depbusanyList #pagerForm'),'depbusanyList');
					}
		});			
	});
</script>
<script type="text/javascript" > 
/* $("document").ready(function(){ 
    $("#odp").click(function(){ 
        if($("#odp").prop("checked")){ 
           // alert('1231');//do some thing
            document.getElementById('showdp').style.display='block';
        }else{ 
           // alert('abc');//do some thing
            document.getElementById('showdp').style.display='none';
        } 
    }) 
}) */

/* function selectAll(){
	var a = document.getElementsByTagName("input");
	if(a[0].checked){
	for(var i = 0;i<a.length;i++){
		if(a[i].type == "checkbox") a[i].checked = false;
		}
	}
	else{
		for(var i = 0;i<a.length;i++){
			if(a[i].type == "checkbox") a[i].checked = true;
			}
		}
	}


$("document").ready(function(){ 
    $("#odp").click(function(){ 
      
            document.getElementById('showdp').style.display='block';
    }) 
    $("#odpook").click(function(){ 
        
        document.getElementById('showdp').style.display='none';
}) 
})  */

</script>
<style type="text/css">
.inputDateButton {
	float: right;
}

.recordType {
	/* 		display: none; */
	
}
</style>
<form action="${base}/list.do" id="pagerForm">
<%-- <div id="showdp" style="float: left; display: none; overflow: auto; width:240px; border: solid 1px #CCC; line-height: 21px; background: #fff">
	<div id="deptNameList" layoutH="60">
		<table>
			<tbody>
			<c:forEach items="${deptList}" var="d" varStatus="s" >
				<tr><td>${d.deptName}<input type="checkbox" value="${d.id}" <c:if test="${dpcheck=='on'}">checked</c:if>   name="dpcheck" />
			</c:forEach>
			<tr height="10px"></tr>
			<tr>
				<td>
				<div class="buttonActive">
					<div class="buttonContent">
						<button type="button" name="select" onclick="selectAll()" >&nbsp;&nbsp;全选&nbsp;&nbsp;</button>
					</div>
				</div>
				<div class="buttonActive" style="margin: 0 10px;">
					<div class="buttonContent">
						<button type="button" id="odpook">&nbsp;&nbsp;确定&nbsp;&nbsp;</button>
					</div>
				</div>
				</td>
			</tr>
			</tbody>
		</table>
	</div>
</div> --%>
<div class="pageHeader" style="border: 1px #B8D0D6 solid">
	
		<div class="searchBar">
			<table border="0">
				<tr align="right">
					<%-- <td width="70">营业部门：</td>
					<td><input type="hidden" name="deptId" value="${deptId}" /> 
					<input type="text" name="deptName" value="${deptName}" size="10"readonly="readonly" /></td>
					<td><a class="btnLook" width="300" maxable="false"resizable="true" title="选择部门" href="/selectDeptTree.do"
						lookupgroup="district"></a></td> --%>
					<%-- <td>
						<input type="checkbox" name="includeSub" <c:if test="${includeSub=='on'}">checked</c:if> />
						不包含下级部门
					</td> --%>
					<!-- <td width="70">操作日期：</td> -->
					<td width="70">消费日期：</td>
					<td width="80" align="left">
						<select class="combox" outerw="50" innerw="70" name="dateType">
							<option value="-1" width="70">全部</option>
							<option value="0" width="70" <c:if test="${dateType==0}">selected</c:if>>消费日期</option>
							<option value="1" width="70" <c:if test="${dateType==1}">selected</c:if>>采集日期</option>
						</select>
					</td>
					<td align="left"><input type="text" name="beginDate" id="beginDate" 
						value="${beginDate}" readonly="readonly"
						datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="18" /><a
						class="inputDateButton" href="javascript:;">选择</a></td>
					<td align="left">&nbsp;- <input type="text" name="endDate" id="endDate" 
						value="${endDate}" readonly="readonly" class="date"
						datefmt="yyyy-MM-dd HH:mm:ss" size="18" /><a
						class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<!-- <td><p>  &nbsp  按部门查询</p></td>
					<td><a class="btnLook" id="odp" width="300" href="javascript:void(0)"></a></td> -->
					<!-- </tr><tr> -->
					<td>
						<input type="checkbox" name="includeSub" <c:if test="${includeSub=='on'}">checked</c:if> />
						不包含下级部门
					</td>
					<td>
						<input type="checkbox" name="includeSubdp" <c:if test="${includeSubdp=='on'}">checked</c:if> />
						显示子部门
					</td>
					<td width="70">图形显示：</td>
					<td width="80" align="left">
						<select class="" outerw="50" innerw="70" name="chartType" id="chartType">
							<option value="-1" width="70">柱饼混合</option>
							<option value="0" width="70" <c:if test="${chartType==0}">selected</c:if>>柱状图</option>
							<option value="1" width="70" <c:if test="${chartType==1}">selected</c:if>>饼图</option>
						</select>
					</td>
					<td colspan="">
						<div class="buttonActive" style="margin: 0 10px;">
							<div class="buttonContent">
								<button type="button" class="search">点击查询</button>
							</div>
						</div> <!-- <div class="buttonActive">
						<div class="buttonContent">
							<button type="button" class="exportExcel" exportType="1">导出</button>
						</div>
					</div> -->
					</td>
				</tr>
			</table>
		</div>
</div>
<div class="pageContent"style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid">
	
	<script src="${chartJs}"></script>
	
	<input type="hidden" id="onedp" value="${onedp}" /> <input type="hidden" id="onefare" value="${onefare}" />
	<input type="hidden" id="twodp" value="${twodp}" /> <input type="hidden" id="twofare" value="${twofare}" />
	<input type="hidden" id="chartWidth" value="${dataWidth}" />
	<%-- <input type="show" id="onedp" value="${onedp}" /> <input type="show" id="onefare" value="${onefare}" />
	<input type="show" id="twodp" value="${twodp}" /> <input type="show" id="twofare" value="${twofare}" /> --%>
	<div id="main"></div> <%-- ${chartWidth}px --%>
</div>
</form>