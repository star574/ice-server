let display = document.getElementById("display")
const mediaDevices = navigator.mediaDevices
const peerConfig = {
    'iceServers': [
        { 'urls': 'localhost:8080' }
    ]
}
function getDisplay() {
    mediaDevices.getDisplayMedia({ video: { frameRate: 75, height: 1080, width: 1920 }, audio: false }).then(res => {
        if ("srcObject" in display) {
            display.srcObject = res
        } else {
            display.src = window.URL.createObjectURL(res)
        }
    })
}
// getDisplay();
function getWebSocket() {
    let webSocket = new WebSocket("ws://localhost:8080/websocket", ["1-654321"])
    webSocket.onopen = function (e) {
        console.log("ws连接成功!");
        console.log(e);
    }
}
getWebSocket();
