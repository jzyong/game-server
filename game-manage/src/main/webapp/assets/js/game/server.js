$(document).ready(function(){
	
    /* $("#loadScriptButton").click(function(){
     	$('#reloadScript').show();
     });
     $("#loadConfigButton").click(function(){
     	$('#reloadConfig').show();
     });*/
});	

//显示加载脚本面板
function showScriptPanel(_ip,_port) {
	$('#scriptIp').val(_ip);
	$('#scriptPort').val(_port);
	$('#scriptIp').attr('placeholder',_ip);
	$('#scriptPort').attr('placeholder',_port);
	
	$('#reloadScript').show();
//	alert(_ip);
} 

//加载脚本
function reloadScript() {
	var _ip = $("#scriptIp").val();
	var _port = $("#scriptPort").val();
	var _scriptPath = $("#scriptPath").val();
//	alert(ctx);
	$.post(ctx+"/server/reloadScript",{ip:_ip,port:_port,scriptPath:_scriptPath},function(data){
		if(data&&data.status==10000){
			alert(data.message);
		}
	});
}

//显示加载配置面板
function showConfigPanel(_ip,_port) {
    $('#scriptIp').val(_ip);
    $('#scriptPort').val(_port);
    // $('#scriptIp').attr('placeholder',_ip);
    // $('#scriptPort').attr('placeholder',_port);

    $('#reloadConfig').show();
//	alert(_ip);
}

//加载配置
function reloadConfig() {
	//使用脚本表单对象了
    var _ip = $("#scriptIp").val();
    var _port = $("#scriptPort").val();
    var _tableName = $("#tableName").val();
//	alert(ctx);
    $.post(ctx+"/server/reloadConfig",{ip:_ip,port:_port,tableName:_tableName},function(data){
        if(data&&data.status==10000){
            alert(data.message);
        }
    });
}

//关闭服务器
function exitServer(_ip,_port) {
	$.post(ctx+"/server/exitServer",{ip:_ip,port:_port},function(data){
		if(data&&data.status==10000){
			alert(data.message);
		}
	});
}

//设置服务器状态 0正常，1维护
function serverState(_id,_type,_state) {
	$.post(ctx+"/server/state",{id:_id,type:_type,state:_state},function(data){
		if(data&&data.status==10000){
			alert(data.message);
		}
	});
}

/**
 * 获取线程信息
 * @param _ip
 * @param _port
 */
function getThreadInfo(_ip,_port) {
    $.post(ctx+"/server/thread/info",{ip:_ip,port:_port},function(data){
        if(data&&data.status==10000){
        	$("#keyValueBody").empty();
            $('#keyValuePanel').show();
			var thread=data.result;
            $.each(thread,function(key,value){
                console.info("key:"+key+";value:"+value);
                $("#keyValueBody").append("<tr><td>"+key+"</td><td>"+value+"</td></tr>");
            });
			console.info(thread);
        }else{
            alert(data.message);
		}
    });
}

/**
 * 获取线程信息
 * @param _ip
 * @param _port
 */
function getJvmInfo(_ip,_port) {
    $.post(ctx+"/server/jvm/info",{ip:_ip,port:_port},function(data){
        if(data&&data.status==10000){
            $("#keyValueBody").empty();
            $('#keyValuePanel').show();
            var jvm=data.result;
            $.each(jvm,function(key,value){
                console.info("key:"+key+";value:"+value);
                $("#keyValueBody").append("<tr><td>"+key+"</td><td>"+value+"</td></tr>");
            });
            console.info(jvm);
        }else{
            alert(data.message);
        }
    });
}