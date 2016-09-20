<!-- 终端营业分析 柱状图输出     by梁敏-->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	$(function(){
		$('.exportExcel').click(function(){
			$('#deviceAnalysisList2 #pagerForm input[name=export]').val(1);
			$('#deviceAnalysisList2 #pagerForm input[name=exportType]').val($(this).attr('exportType'));
			$('#deviceAnalysisList2 #pagerForm').submit();
			$('#deviceAnalysisList2 #pagerForm input[name=export]').val('');
			$('#deviceAnalysisList2 #pagerForm input[name=exportType]').val('');
		});
	});
	
	$(function() {

		
	var obj = document.getElementById("main");
		var objWidth = document.getElementById("chartWidth").value;
		obj.style.height = window.screen.availHeight * 0.70 + 'px';
		if ($('#chartType').val() == 0) {
			obj.style.width = window.screen.availWidth * 0.75 + 'px';
		} else {
			if (objWidth < window.screen.availWidth * 0.75) {
				obj.style.width = window.screen.availWidth * 0.75 + 'px';
			} else {
				if (objWidth >= 32767)
					objWidth = 32767
				obj.style.width = objWidth + 'px';
			}
		}
		/* alert(window.screen.width); 
		alert(obj.style.width); */
		$('#deviceAnalysisList2 .search').click(
				function() {

					var beginDate = $('#beginDate').val();
					var endDate = $('#endDate').val();
					if (beginDate != "" && endDate != "") {
						if (beginDate >= endDate) {
							alertMsg.error('查询开始时间不能大于查询结束时间');
						} else {
							divSearch($('#deviceAnalysisList2 #pagerForm'),
									'deviceAnalysisList2');
						}
					} else {
						divSearch($('#deviceAnalysisList2 #pagerForm'),
								'deviceAnalysisList2');
					}
				});
	});
</script>
<style type="text/css">
	.inputDateButton{
		float: right;
	}
</style>
<div class="pageHeader" style="border: 1px #B8D0D6 solid">
	<form action="${base}/list.do" id="pagerForm">
		<div class="searchBar">
      <table border="0">
				<tr align="right">
					<td width="70">营业部门：</td>
					<td>
						<input type="hidden" name="deptId" value="${deptId}"/>
						<input type="text" name="deptName" value="${deptName}" size="10" readonly="readonly"/>
					</td>
					<td>
						<a class="btnLook" width="300" maxable="false" resizable="true" title="请选择营业部门" href="/selectDeptTree.do" lookupgroup="district"></a>					
					</td>
					<td >
						<input type="checkbox" name="includeSub" <c:if test="${includeSub=='on'}">checked</c:if> />
						不包含下级部门
					</td>
					<td width="70">消费时间：</td>
					<td align="left">
						<input type="text" name="beginDate" id="beginDate" value="${beginDate}" readonly="readonly" datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="18"/><a class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left">
						&nbsp;- 
						<input type="text" name="endDate" id="endDate" value="${endDate}" readonly="readonly" class="date" datefmt="yyyy-MM-dd HH:mm:ss"  size="18"/><a class="inputDateButton" href="javascript:;">选择</a>
					</td>
<!-- 					<td align="left"> -->
<%-- 						<input type="checkbox" name="includeOff" <c:if test="${includeOff=='on'}">checked</c:if> /> --%>
<!-- 						排除注销用户 -->
<!-- 					</td> -->
					<td width="70">图形显示：</td>
					<td width="80" align="left">
						<select class="" outerw="50" innerw="70" name="chartType" id="chartType">
							<option value="-1" width="70">柱形图</option>
							<option value="0" width="70" <c:if test="${chartType==0}">selected</c:if>>折线图</option>
							<%-- <option value="1" width="70" <c:if test="${chartType==1}">selected</c:if>>饼图</option> --%>
						</select>
					</td>
					<td colspan="12">
						<div class="buttonActive">
							<div class="buttonContent">
								<button type="button" class="search">&nbsp;&nbsp;查询&nbsp;&nbsp;</button>
							</div>
						</div>
<!-- 						<div class="buttonActive" style="margin: 0 10px;"> -->
<!-- 							<div class="buttonContent"> -->
<!-- 								<button type="button" class="exportExcel" exportType="0">导出当前</button> -->
<!-- 							</div> -->
<!-- 						</div> -->
<!-- 						<div class="buttonActive"> -->
<!-- 							<div class="buttonContent"> -->
<!-- 								<button type="button" class="exportExcel" exportType="1">导出全部</button> -->
<!-- 							</div> -->
<!-- 						</div> -->
					</td>
				</tr>
			</table>
			</div>
	</form>
</div>
<div class="pageContent" 
	style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid ; margin:0 auto;">
<%-- <center> --%>
<%-- <img src="${graphURL}" width="1000" height="650" border="50" usemap="${filename}"> --%>
<%-- 	</center> --%>
<div id="main"></div>
    <script src="${chartJs}"></script>
	<input type="hidden" id=dp value="${dp}" /> 
	<input type="hidden" id="fare"value="${fare}" />
	<input type="hidden" id="chartWidth" value="${dataWidth}" />
</div>




