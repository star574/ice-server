let display = document.getElementById("display")
let btn = document.getElementById("btn")
const mediaDevices = navigator.mediaDevices
var webSocket = null;
const peerConfig = {
    'iceServers': [
        { 'urls': 'localhost:8080/websocket' }
    ]
}
let rtc1 = new RTCPeerConnection()
let rtc2 = new RTCPeerConnection()

btn.onclick = function () {
    console.log("链接");
    mediaDevices.getDisplayMedia({ video: { frameRate: 75, height: 1080, width: 1920 }, audio: false }).then(localStream => {
        // if ("srcObject" in display) {
        //     display.srcObject = localStream
        // } else {
        //     display.src = window.URL.createObjectURL(localStream)
        // }
        localStream.getTracks().forEach(track => {
            rtc1.addTrack(track, localStream)
        })
        // 进行webrtc链接
        rtc1.createOffer().then(
            offer => {
                rtc1.setLocalDescription(offer)
                console.log("发送offer");
                sendMessage({ "offer": offer })
            }
        )
    }
    )
}
rtc2.addEventListener('connectionstatechange', event => {
    if (rtc2.connectionState == 'connected') {
        // Peers connected!
        console.log("Peers链接成功");
    }
});
rtc1.addEventListener('connectionstatechange', event => {
    if (rtc1.connectionState == 'connected') {
        // Peers connected!
        console.log("Peers链接成功");
    }
});
rtc2.addEventListener('track', async (event) => {
    console.log(event);
    const remoteSteam = event.streams[0];
    console.log("收到远程流", remoteSteam);
    if ("srcObject" in display) {
        display.srcObject = remoteSteam
    } else {
        display.src = window.URL.createObjectURL(remoteSteam)
    }
});


let uid = Math.floor(Math.random() * 10 + 1)
webSocket = new WebSocket("ws://localhost:8080/websocket", [`${uid}-654321`])
webSocket.onopen = onopen
webSocket.onclose = onclose
webSocket.onerror = onerror
webSocket.onmessage = onmessage

async function onopen(e) {
    console.log("ws连接成功!");
}
function onclose(e) {
    console.log("ws连接关闭!");
}
function onerror(e) {
    console.log("ws连接错误!");
}
async function onmessage(e) {
    let data = JSON.parse(e.data)
    if (data.offer) {
        console.log("收到offer 设置远程描述，设置答案(answer)", data.offer);
        await rtc2.setRemoteDescription(new RTCSessionDescription(data.offer));
        const answer = await rtc2.createAnswer()
        await rtc2.setLocalDescription(answer)
        sendMessage({ 'answer': answer })
    }
    if (data.answer) {
        console.log("收到回答 建立链接==", data.answer);
        // 发送候选对象
        // 设置远程描述
        await rtc1.setRemoteDescription(new RTCSessionDescription(data.answer));
        await rtc1.addEventListener("icecandidate", event => {
            if (event.candidate) {
                sendMessage({ 'icecandidate': event.candidate })
            }
        })
    }
    if (data.icecandidate) {
        // 收到候选对象
        console.log("候选对象:", data.icecandidate);
        await rtc2.addIceCandidate(data.icecandidate)
    }
}

function sendMessage(data) {
    let ws = webSocket
    if (ws.readyState == WebSocket.OPEN) {
        ws.send(JSON.stringify(data))
    } else {
        setTimeout(() => {
            sendMessage(JSON.stringify(data))
        }, 1000);
    }
}