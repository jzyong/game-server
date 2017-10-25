
//添加金币
function gmAddGold(gmUrl) {
    var gold = $("#addGold").val();
    var roleId = $("#roleId").val();
    var url = "&cmd=addGold&gold=" + gold + "&roleId=" + roleId;
    $.post(ctx+"/gm/execute",{queryString:url},function(data){
        if(data&&data.status==10000){
            alert(data.message);
        }
    });

}

