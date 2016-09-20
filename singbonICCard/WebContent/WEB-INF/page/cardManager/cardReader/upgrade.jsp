<!-- 读卡机升级 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="/js/jquery.timers.js" type="text/javascript"></script>
<script src="/js/comet4j.js" type="text/javascript"></script>
<script src="/js/map.js" type="text/javascript"></script>
<script type="text/javascript">
	var map = new Map();
	
	$(function() {
		<c:forEach items="${list }" var="d">
			<c:if test="${d.isOnline==1}">
				map.put('${d.sn}',new Date());
			</c:if>
		</c:forEach>
		init();
		heart();
		
		$('.device input').click(function(){
			var parent=$(this).parent();
			var sn=parent.attr('id');
			var status=parent.find('img').attr('src');
			if(status.indexOf('online')!=-1){
				$.post('${base}/command.do?comm=upgrade&opSn='+sn,null,function(e){
					if(e==1){
						parent.find('.status').html('发送进入底层命令');
					}else if(e==2){
						parent.find('.status').html('不存在升级文件');						
					}
				});
			}else{
				alertMsg.warn('读卡机离线暂不能升级！');
			}
		});
	});
	
	function init() {
		JS.Engine.stop();
		JS.Engine.start('/conn');
		JS.Engine.on({
			'CR${company.id}' : function(e) {//侦听一个channel
				var e2 = eval('(' + e + ')');
				var sn=e2.sn;
				map.put(sn,new Date());
				$('.device[id='+sn+'] img').attr('alt','在线').attr('src','/img/online.png');
				//升级进度
				if(e2.f1==0x21){
					$('.device[id='+sn+'] .s').html(e2.s+'%');					
				//进入底层完成
				}else if(e2.f1==0x20){
					$('.device[id='+sn+'] .status').html('进入底层成功');					
				//返回应用程序完成
				}else if(e2.f1==0x22){
					$('.device[id='+sn+'] .status').html('返回应用程序完成');					
				}
			}
		});
	}
	
	function heart(){
		$('body').everyTime('5s','getCardReaderStatus', function() {
			var d=new Date();
			var array = map.keySet();
			for(var i in array) {
				var t=(d.getTime()-map.get(array[i]).getTime())/1000;
				if(t>12){
					$('#deviceList .device[id='+array[i]+'] img').attr('alt','离线').attr('src','/img/offline.png');
					$.post('${base }/closeSocketChannel.do?sn='+array[i]);
					map.remove(array[i]);
				}
			}
		},0,true);
	}
</script>
<style type="text/css">
	.dialog .dialogContent{
		padding: 1px;
	}
	.dialog .pageFormContent{
		border-style: none;
	}
	.device{
		float: left;
		height: 120px;
		width: 150px;
		text-align:center;
	}
	.s{
		position:relative; 
		left:2px; 
		color: red;
		font-size: 14px;
		font-weight: bold;
		margin: 10px 0 3px;
	}
	.name{
		margin: 5px 0;
	}
</style>
<div id="deviceList">
	<c:forEach items="${list }" var="d">
		<div class="device" id="${d.sn}">
			<div class="s">
				0.00%
			</div>
			<c:if test="${d.isOnline==1}">
				<img alt="在线" src="/img/online.png" />
			</c:if>
			<c:if test="${d.isOnline==0}">
				<img alt="离线" src="/img/offline.png" />
			</c:if>
			<div class="name">${d.deviceName}</div>
			<div class="status"></div>
			<input type="button" value="升级"/>
		</div>
	</c:forEach>
</div>
