<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	$(function() {
		$('#companyList .search').click(function() {
			divSearch($('#companyList #pagerForm'), 'companyList');
		});
	});
</script>

<div class="pageHeader" style="border: 1px #B8D0D6 solid">
	<form action="${base}/list.do" id="pagerForm">
		<input type="hidden" name="pageNum" value="${pageNum}" /> 
		<input type="hidden" name="numPerPage" value="${numPerPage}" /> 
		<input type="hidden" name="totalCount" value="${totalCount}" />
		<div class="searchBar">
			<table border="0">
				<tr align="right">
					<td>单位名称：</td>
					<td>
						<input type="text" name="nameStr" size="10" value="${nameStr }" />
					</td>
					<td style="padding-left: 10px;">包含禁用：</td>
					<td>
						<input type="checkbox" name="includeAll" size="10" <c:if test="${includeAll=='on'}">checked="checked"</c:if>/>
					</td>
					<td style="padding-left: 10px;">
						<div class="buttonActive">
							<div class="buttonContent">
								<button type="button" class="search">&nbsp;&nbsp;查询&nbsp;&nbsp;</button>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</form>
</div>
<div class="pageContent"
	style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid">
	<table class="table" width="99%" layoutH="125">
		<thead>
			<tr>
				<th width="30">序号</th>
				<th width="100">单位名称</th>
				<th width="100">序列号</th>
				<th width="100">授权号</th>
				<th width="100">基本扇区号</th>
				<th width="100">试用日期</th>
				<th width="100">状态</th>
			</tr>
		</thead>
		<tbody class="companyList">
			<c:forEach var="c" items="${list}" varStatus="status">
				<tr target="company" id="${c.id }" companyName="${c.companyName}" serialNumber="${c.serialNumber}" authNumber="${c.authNumber}" baseSection="${c.baseSection}" invalidDate="${c.invalidDate}" appId="${c.appId}" mchId="${c.mchId}" mchKey="${c.mchKey}" 
					weixinLimit="${c.weixinLimit}" remainingWeixin="${c.remainingWeixin}" shortLimit="${c.shortLimit}" remainingShort="${c.remainingShort}" allowEntranceMessage="${c.allowEntranceMessage}" allowPCSavingMessage="${c.allowPCSavingMessage}" allowWeixinSavingMessage="${c.allowWeixinSavingMessage}" enable="${c.enable }">
					<td>${status.index+1}</td>
					<td>${c.companyName}</td>
					<td>${c.serialNumber}</td>
					<td>${c.authNumber}</td>
					<td>${c.baseSection}</td>
					<td>${c.invalidDate}</td>
					<td>${c.enable==true?"启用":"禁用" }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'companyList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="companyList" totalCount="${totalCount }"
			numPerPage="${numPerPage }" pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>
