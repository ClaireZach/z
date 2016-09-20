<!-- 进出门时间段设置 -->
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
	$(function() {
		$('#entranceTimeForm .save').click(function() {
			validateCallback($(this).parents('form'), function(e) {
				if (e == 1) {
					alertMsg.correct('保存成功！');
					$('#entranceTime input').eq(0).val('');
					refreshentranceTimeList();
					$('#entranceTime input').each(function() {
						$(this).val('');
					});
				} else {
					alertMsg.error('保存失败！');
				}
			}, null);
		});
	});
	function refreshentranceTimeList() {
		$('#entranceTimeList').loadUrl('${base}/list.do', {}, function() {
			$('#entranceTimeList').find('[layoutH]').layoutH();
		});
	}
</script>
<link href="themes/css/custom.css" rel="stylesheet" type="text/css" />
<style type="text/css">
</style>

<div class="form" style="float: left; display: block; overflow: auto; width: 240px; border: solid 1px #CCC; line-height: 21px; background: #fff">
	<div layoutH="383"></div>
	<form id="entranceTimeForm" method="post" action="${base}/save.do" class="pageForm required-validate">
		<input type="hidden" name="id" />
		<div class="pageFormContent">
			<dl style="margin: 10px 0; width: 300px;">
				<dt>开始时间：</dt>
				<dd>
					<input type="text" name="beginTime" maxlength="20" class="date required" datefmt="HH:mm" />
				</dd>
			</dl>
			<dl style="margin: 10px 0; width: 300px;">
				<dt>结束时间：</dt>
				<dd>
					<input type="text" name="endTime" maxlength="20" class="date required" datefmt="HH:mm" />
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>备注：</dt>
				<dd>
					<input type="text" name="remark" maxlength="20" class="" />
				</dd>
			</dl>
			<dl style="margin: 10px 0 50px;">
				<dt>&nbsp;</dt>
				<dd>
					<input type="radio" name="status" value="1" style="width: 15px;"/>进门
					<input type="radio" name="status" value="2" style="width: 15px;"/>出门
				</dd>
			</dl>
		</div>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ENTRANCE_TIME_SAVE">
			<div class="formBar">
				<div class="panelBar" style="border-style: none;">
					<ul class="toolBar">
						<li><a class="save" href="javascript:;"><span>保存</span></a></li>
					</ul>
				</div>
			</div>
		</security:authorize>
	</form>
</div>
<div id="entranceTimeList" class="unitBox" style="margin-left: 246px;">
	<jsp:include page="${base}/list.do" />
</div>