var websocket; //客户端

/**
 * 连接服务器
 * @param wsUrl 服务器地址
 */
function connectServer(wsUrl) {
    websocket = new WebSocket(wsUrl);
    websocket.onopen = function (event) {
        onOpen(event);
    };
    websocket.onclose = function (event) {
        onClose(event);
    };
    websocket.onmessage = function (event) {
        onMessage(event);
    };
    websocket.onerror = function (event) {
        onError(event);
    }

}

/**
 * 连接创建
 * @param event
 */
function onOpen(event) {
    console.log("连接服务器：" + event.toString());
}

/**
 * 连接关闭
 * @param event
 */
function onClose(event) {
    alert("服务器关闭：" + event.toString());
    console.log("服务器关闭：" + event.toString());
}

/**
 * 收到消息
 * @param event
 */
function onMessage(event) {
    var reader = new FileReader();
    reader.readAsArrayBuffer(event.data);
    reader.onload = function (e) {
        var dataView = new DataView(reader.result, 0, 4);
        var mid = dataView.getInt32(0);
        var buf = new Uint8Array(reader.result, 4);
        handleMessage(mid, buf);
    }
}

/**
 * 异常消息
 * @param event
 */
function onError(event) {
    alert("网络异常：" + event.toString());
    console.log("网络异常：" + event.toString());
}

/**
 * 发送消息
 * @param mid 消息ID
 * @param buffer protobuf转换后的UIntArray 数组
 */
function sendMsg(mid,buffer) {
    var arrayBuffer=new ArrayBuffer(4+buffer.length);
    var dataView=new DataView(arrayBuffer);
    dataView.setInt32(0,mid);
    for (var i = 0; i < buffer.length; i++) {
        dataView.setUint8(i + 4, buffer[i]);
    }
    if(websocket!=null){
        websocket.send(arrayBuffer);
    }else{
        alert("未连接服务器，消息发送失败："+mid);
    }
}

/**
 * 处理返回消息
 * @param mid 消息ID
 * @param buffer protobuf消息内容
 */
function handleMessage(mid, buffer) {

}