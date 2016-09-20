<!-- 门禁时间段设置 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="/js/comet4j.js" type="text/javascript"></script>
<script type="text/javascript">
function refreshTimeTab() {
	$('#entranceTimeList1').loadUrl('${base}/timelist1.do',{}, function() {
		$('#entranceTimeList1').find('[layoutH]').layoutH();
	});
}
</script>
<style type="text/css"></style>
<div class="tabs" currentIndex="0" loadIndex="0" eventType="click" style="margin: 1px;" >
	<%-- <div class="tabsHeader">
		<div class="tabsHeaderContent">
			<ul>
				<li><a href="${base}/time1.do"class="j-ajax refresh"><span>周一</span></a></li>
				<li><a href="${base}/time2.do"class="j-ajax refresh"><span>周二</span></a></li>
				<li><a href="${base}/time3.do"class="j-ajax refresh"><span>周三</span></a></li>
				<li><a href="${base}/time4.do"class="j-ajax refresh"><span>周四</span></a></li>
				<li><a href="${base}/time5.do"class="j-ajax refresh"><span>周五</span></a></li>
				<li><a href="${base}/time6.do"class="j-ajax refresh"><span>周六</span></a></li>
				<li><a href="${base}/time7.do"class="j-ajax refresh"><span>周日</span></a></li>
				<li><span>周二</span></li>
				<li><span>周二</span></li>
				<li><span>周二</span></li>
				<li><span>周二</span></li>
				<li><span>周二</span></li>
				<li><span>周二</span></li>
				<li><span>周二</span></li>
			</ul>
		</div>
	</div>
	<div class="tabsContent" style="padding:0;" layoutH="0">
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
	</div> --%>
	
</div>
