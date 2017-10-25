/**
 * 捕鱼消息返回脚本
 * <p>
 *     需要引用webscoket-client.js
 *
 * </p>
 *
 * @author JiangZhiYong
 * @QQ 359135103
 * */


/**
 * 处理返回消息
 * @param mid 消息ID
 * @param buffer protobuf消息内容
 */
function handleMessage(mid, buffer) {
    switch (mid) {
        case 10002:     //登录 枚举是否支持
            loginResponse(buffer);
            break;
        case 10004:     //登录子游戏
            loginSubGameResponse(buffer);
            break;
        case 10006:     //系统错误
            systemErrorResponse(buffer);
            break;
        case 10012:     //心跳
            heartResponse(buffer);
            break;
        case 10016:     //聊天
            chatResponse(buffer);
            break;
        case 10018:     //背包列表
            packetItemsResponse(buffer);
            break;
        case 10022:     //邮件列表
            mailListResponse(buffer);
            break;
        case 20002:     //进入房间返回
            enterRoomResponse(buffer);
            break;
        case 20008:     //鱼群进入消息
            fishEnterRoomResponse(buffer);
            break;
        case 20016:     //开炮
            fireResponse(buffer);
            break;
        case 20018:     //开炮结果
            fireResultResponse(buffer);
            break;
        default:
            console.warn("未实现消息：" + mid);
    }
}

/**
 * 登录消息返回
 * @param buffer
 */
function loginResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/HallLoginMessage.proto", function (err, root) {
        if (err) throw err;
        var LoginResponse = root.lookup("LoginResponse");
        response = LoginResponse.decode(buffer);
        userId = response.uid;
        roleId = response.rid;
        $("#showLogPanel").append("登录返回：" + response.isOk + " 用户ID：" + response.uid + " 角色ID：" + response.rid + "\r\n");
        loginSubGameRequest();
    });
}

/**
 * 登录子游戏返回
 * @param buffer
 */
function loginSubGameResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/HallLoginMessage.proto", function (err, root) {
        if (err) throw err;
        var LoginSubGameResponse = root.lookup("LoginSubGameResponse");
        response = LoginSubGameResponse.decode(buffer);
        $("#showLogPanel").append("登录子游戏返回：" + response.result + "\r\n");
    });
}

/**
 * 系统错误消息返回
 * @param buffer
 */
function systemErrorResponse(buffer){
    var response;
    protobuf.load("../assets/proto/SystemMessage.proto", function (err, root) {
        if (err) throw err;
        var SystemErrorResponse = root.lookup("SystemErrorResponse");
        response = SystemErrorResponse.decode(buffer);
        $("#showLogPanel").append("系统错误返回：" + response.msg + "\r\n");
        alert(response.msg);
    });
}

/**
 * 心跳返回
 * @param buffer
 */
function heartResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/SystemMessage.proto", function (err, root) {
        if (err) throw err;
        var HeartResponse = root.lookup("HeartResponse");
        response = HeartResponse.decode(buffer);
        // $("#showLogPanel").append("心跳服务器时间：" + response.serverTime + "</br>");
        // console.debug("服务器时间：" + response.serverTime);
    });
}

/**
 * 心跳返回
 * @param buffer
 */
function chatResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/HallChatMessage.proto", function (err, root) {
        if (err) throw err;
        var ChatResponse = root.lookup("ChatResponse");
        response = ChatResponse.decode(buffer);
        $("#showLogPanel").append(response.senderNick + " 说：" + response.msg + "\r\n");
    });
}

/**
 * 背包列表
 * @param buffer
 */
function packetItemsResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/HallPacketMessage.proto", function (err, root) {
        if (err) throw err;
        var PacketItemsResponse = root.lookup("PacketItemsResponse");
        response = PacketItemsResponse.decode(buffer);
        packetItems = response.items;
        for (var i = 0; i < packetItems.length; i++) {
            $("#showLogPanel").append("物品：" + packetItems[i].configId + " 数量：" + packetItems[i].num + "\r\n");
        }

    });
}

/**
 * 邮件列表
 * @param buffer
 */
function mailListResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/HallChatMessage.proto", function (err, root) {
        if (err) throw err;
        var MailListResponse = root.lookup("MailListResponse");
        response = MailListResponse.decode(buffer);
        mailList = response.mails;
        for (var i = 0; i < mailList.length; i++) {
            $("#showLogPanel").append("邮件：" + mailList[i].id + " 标题：" + mailList[i].title + "内容：" + mailList[i].content + "\r\n");
        }

    });
}

/**
 * 登录房间返回
 * @param buffer
 */
function enterRoomResponse(buffer) {
    var response;
    protobuf.load("../assets/proto/BydrRoomMessage.proto", function (err, root) {
        if (err) throw err;
        var EnterRoomResponse = root.lookup("EnterRoomResponse");
        response = EnterRoomResponse.decode(buffer);
        var result = response.result;
        var roles=response.roleInfo;
        if(result==0){  //成功
            g.sence.changeSense(1); //切换场景
        }else{
            alert("进入房间失败");
        }
        for(var i=0;i<roles.length;i++){
            if(roles[i].rid==roleId){   //更新显示金币
                g.score=roles[i].gold;
            }
        }

        $("#showLogPanel").append("进入房间结果：" + result + "\r\n");

    });
}

/**
 * 鱼群进入房间
 * @param buffer
 */
function fishEnterRoomResponse(buffer){
    var response;
    protobuf.load("../assets/proto/BydrRoomMessage.proto", function (err, root) {
        if (err) throw err;
        var FishEnterRoomResponse = root.lookup("FishEnterRoomResponse");
        response = FishEnterRoomResponse.decode(buffer);
        var fishs = response.fishInfo;
        for(var i=0;i<fishs.length;i++){        //刷新鱼 TODO 目前未随机游，未按给定路线显示
            //new出鱼的实例 根据decoration的值判断鱼的初始方向，decoration的范围为（-0.5,0.5）
            var decoration=Math.random()-0.5;
            var standard=0.1;
            if(standard>Math.random()){
                if(decoration<0){
                    var fish=new Fish(fishs[i].configId, g.canvas.width+50,rnd(0,g.canvas.height),rnd(91,269),fishs[i].id);
                }else{
                    var fish=new Fish(fishs[i].configId, -50,rnd(0,g.canvas.height),rnd(-89,89),fishs[i].id);
                }
                g.fishArr.push(fish);
                console.debug("刷新鱼："+fishs[i].configId);
            }
        }
    });
}

/**
 * 开炮
 * @param buffer
 */
function fireResponse(buffer){
    var response;
    protobuf.load("../assets/proto/BydrFightMessage.proto", function (err, root) {
        if (err) throw err;
        var FireResponse = root.lookup("FireResponse");
        response = FireResponse.decode(buffer);
        g.score-=response.gold;
        console.debug("开炮返回：",response);
    });
}

/**
 * 开炮结果
 * @param buffer
 */
function fireResultResponse(buffer){
    var response;
    protobuf.load("../assets/proto/BydrFightMessage.proto", function (err, root) {
        if (err) throw err;
        var FireResultResponse = root.lookup("FireResultResponse");
        response = FireResultResponse.decode(buffer);
        if(roleId==response.rid){
            g.score=response.gold;
        }
        console.debug("开炮结果返回：",response);
    });
}