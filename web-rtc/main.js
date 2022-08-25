// element
let display = document.getElementById("display")
let remote = document.getElementById("remote")
let btn = document.getElementById("btn")
let ws = false
let localStream


// webrtc
let rtc = new RTCPeerConnection()
navigator.mediaDevices.getDisplayMedia({ video: { width: 1920, height: 1080, frameRate: 75 }, audio: false }).then(stream => {
    display.srcObject = stream
    localStream = stream
    localStream.getTracks().forEach(track => {
        rtc.addTrack(track, localStream)
    })
})

// websocket
let uid = Math.floor(Math.random() * 1000) + 1
let websocket = new WebSocket("ws://localhost:8080/websocket", [`${uid}-654321`])
btn.onclick = getConnect

// Listen for connectionstatechange on the local RTCPeerConnection
rtc.addEventListener('connectionstatechange', event => {
    if (rtc.connectionState === 'connected') {
        // Peers connected!
        console.log("链接成功！");
    }
});
rtc.addEventListener("icecandidate", event => {
    if (event.candidate) {
        sendMessage({ 'icecandidate': event.candidate });
    }
})
rtc.addEventListener('track', async (event) => {
    const [remoteStream] = event.streams;
    remote.srcObject = remoteStream;
});

async function getConnect() {
    if (!ws) {
        return
    }
    let offer = await rtc.createOffer()
    await rtc.setLocalDescription(offer)
    sendMessage({ "offer": offer })
}
websocket.onopen = open
websocket.onerror = error
websocket.onclose = close
websocket.onmessage = message
async function open(event) {
    console.log(`${uid}：链接成功`);
    ws = true
}
async function error(event) {
    console.log(`${uid}：error`);
}
async function close(event) {
    console.log(`${uid}：close`);
}
async function message(event) {
    let data = JSON.parse(event.data)
    if (data.offer) {
        console.log("接收到offer", data.offer);
        if (!window.confirm("接收会话？")) {
            return
        }
        await rtc.setRemoteDescription(new RTCSessionDescription(data.offer))
        const answer = await rtc.createAnswer();
        await rtc.setLocalDescription(answer);
        sendMessage({ "answer": answer })

    }
    if (data.answer) {
        console.log("接收到answer", data.answer);
        await rtc.setRemoteDescription(new RTCSessionDescription(data.answer))
        rtc.addEventListener("icecandidate", event => {
            if (event.candidate) {
                sendMessage({ 'icecandidate': event.candidate });
            }
        })
    }
    if (data.icecandidate) {
        console.log("接收到icecandidate", data.icecandidate);
        await rtc.addIceCandidate(data.icecandidate)
    }
}

function sendMessage(data) {
    websocket.send(JSON.stringify(data))
}