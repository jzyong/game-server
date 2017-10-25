/**
 * 捕鱼发送消息脚本
 * <p>
 *     需要引用webscoket-client.js
 *
 * </p>
 *
 * @author JiangZhiYong
 * @QQ 359135103
 * */

/**
 * 登录
 */
function loginRequest(url) {
    connectServer(url);
    var _account=$("#account").val();
    var _passwrod=$("#password").val();
    protobuf.load("../assets/proto/HallLoginMessage.proto", function (err, root) {
        if (err) throw err;
        var LoginRequest = root.lookup("LoginRequest");
        loginRequest = LoginRequest.create({account: _account, password: _passwrod, loginType: "ACCOUNT"});
        var buffer = LoginRequest.encode(loginRequest).finish();
        sendMsg(10001,buffer);
    });
}

/**
 * 登录子游戏
 */
function  loginSubGameRequest() {
    if(roleId==null||roleId<1){
        alert("角色未登陆");
        return;
    }
    protobuf.load("../assets/proto/HallLoginMessage.proto", function (err, root) {
        if (err) throw err;
        var LoginSubGameRequest = root.lookup("LoginSubGameRequest");
        loginSubGameRequest = LoginSubGameRequest.create({rid: roleId, type: 0, gameType: 101});
        var buffer = LoginSubGameRequest.encode(loginSubGameRequest).finish();
        sendMsg(10003,buffer);
    });
}

/**
 * 客户端心跳
 */
function heartRequest() {
    if(roleId==null||roleId<1){
        return;
    }
    protobuf.load("../assets/proto/SystemMessage.proto", function (err, root) {
        if (err) throw err;
        var HeartRequest = root.lookup("HeartRequest");
        var heartRequest = HeartRequest.create({rid: roleId, type: 0, gameType: 101});
        var buffer = HeartRequest.encode(heartRequest).finish();
        sendMsg(10011,buffer);
    });
}

/**
 * 聊天
 * @param url
 */
function bydrChatRequest() {
    var chatReceiverId=$("#chatReceiverId").val();
    var chatMsg=$("#chatMsg").val();
    protobuf.load("../assets/proto/HallChatMessage.proto", function (err, root) {
        if (err) throw err;
        var ChatRequest = root.lookup("ChatRequest");
        var chatRequest = ChatRequest.create({receiverId: chatReceiverId, msg: chatMsg});
        var buffer = ChatRequest.encode(chatRequest).finish();
        sendMsg(10015,buffer);
    });
}

/**
 * 背包列表请求
 */
function bydrPacketItemsRequest() {
    if(roleId==null||roleId<1){
        alert("角色未登陆");
        return;
    }
    protobuf.load("../assets/proto/HallPacketMessage.proto", function (err, root) {
        if (err) throw err;
        var PacketItemsRequest = root.lookup("PacketItemsRequest");
        var packetItemsRequest = PacketItemsRequest.create({rid: roleId, type: 0, gameType: 101});
        var buffer = PacketItemsRequest.encode(packetItemsRequest).finish();
        sendMsg(10017,buffer);
    });
}

/**
 * 请求邮件列表
 */
function bydrMailListRequest() {
   if(!isLogin()){
       return;
   }
    protobuf.load("../assets/proto/HallChatMessage.proto", function (err, root) {
        if (err) throw err;
        var MailListRequest = root.lookup("MailListRequest");
        var mailListRequest = MailListRequest.create({rid: roleId, type: 0, gameType: 101});
        var buffer = MailListRequest.encode(mailListRequest).finish();
        sendMsg(10021,buffer);
    });
}

/**
 * 进入房间
 */
function bydrEnterRoomRequest() {
    if(!isLogin()){
        return;
    }
    protobuf.load("../assets/proto/BydrRoomMessage.proto", function (err, root) {
        if (err) throw err;
        var EnterRoomRequest = root.lookup("EnterRoomRequest");
        var enterRoomRequest = EnterRoomRequest.create({rank: 10});
        var buffer = EnterRoomRequest.encode(enterRoomRequest).finish();
        sendMsg(20001,buffer);
    });
}

/**
 * 开炮
 */
function bydrFireRequest(_gold) {
    if(!isLogin()){
        return;
    }
    protobuf.load("../assets/proto/BydrFightMessage.proto", function (err, root) {
        if (err) throw err;
        var FireRequest = root.lookup("FireRequest");
        var fireRequest = FireRequest.create({gold: _gold});
        var buffer = FireRequest.encode(fireRequest).finish();
        sendMsg(20015,buffer);
    });
}

/**
 * 开炮结果
 */
function bydrFireResultRequest(_targetFishId,_fireGold) {
    if(!isLogin()||_targetFishId.length<1){
        return;
    }
    protobuf.load("../assets/proto/BydrFightMessage.proto", function (err, root) {
        if (err) throw err;
        var FireResultRequest = root.lookup("FireResultRequest");
        var fireResultRequest = FireResultRequest.create({targetFishId: _targetFishId,fireGold:_fireGold});
        var buffer = FireResultRequest.encode(fireResultRequest).finish();
        sendMsg(20017,buffer);
    });
}