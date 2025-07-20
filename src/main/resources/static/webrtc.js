const config = {
    iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
}

let connection = undefined;

function initRTCConnection() {
    let connection = new RTCPeerConnection(config);
    connection.addEventListener("icecandidate", async (event) => {
        console.log("sending ice cadidate");
        await signalChannel.send(JSON.stringify({
            type: "ice_candidate",
            sdp: JSON.stringify(event.candidate)
        }));
    });

    connection.addEventListener("datachannel", (event) => {
        wireInputsToChannel(event.channel);
    });
    return connection;
}


const signalChannel = new WebSocket('/signal');
signalChannel.addEventListener("open", async (event) => {
    const uidCookie = await cookieStore.get("uid");
    const uid = uidCookie.value;
    const roomId = document.URL.match("room/([\\w-]*)$")[1];

    signalChannel.send(JSON.stringify({
        type: "initiate",
        sdp: `${uid},${roomId}`
    }));
});

signalChannel.addEventListener("message", async (event) => {
    console.log(" >> " + event.data);
    let data = JSON.parse(event.data);
    if (data.type === "start_sync") {
        await sendOffer();
    }

    if (data.type === "answer") {
        await receiveAnswer(data);
    }

    if (data.type === "offer") {
        await createAnswer(data);
    }
    if (data.type === "ice_candidate") {
        await setIceCandidate(JSON.parse(data.sdp));
    }
});

function wireInputsToChannel(channel) {
    const messageInput = document.getElementById("message_input");
    const chatBox = document.getElementById("chatbox");

    messageInput.onkeydown = 
        (event) => {
            if (event.key == "Enter") {
                try{
                    channel.send(messageInput.value);
                    chatBox.value += ">> " + messageInput.value + "\n";
                    messageInput.value = "";
                } catch (err) {
                    console.log(err);
                }
            }
        };

    channel.addEventListener("message", (event) => { 
        console.log(event);
        chatBox.value += "<< " + event.data + "\n"; 
    });
}

async function sendOffer() {
    connection = initRTCConnection(connection);

    const channel = connection.createDataChannel("chat");
    channel.addEventListener("open", (event) => {
        console.log("opened the channel");
        wireInputsToChannel(channel);
    });

    const offer = await connection.createOffer();
    await connection.setLocalDescription(offer);
    await signalChannel.send(JSON.stringify(offer));
    console.log("offer sent");
}

async function receiveAnswer(answer) {
    await connection.setRemoteDescription(answer);
    console.log("answer set");
    console.log("connection statis is " + connection.connectionState);
}

async function createAnswer(offer) {
    connection = initRTCConnection(connection);
    console.log("setting offer " + JSON.stringify(offer));
    await connection.setRemoteDescription(offer);
    console.log("remote description is " + connection.remoteDescription);
    const answer = await connection.createAnswer();
    await connection.setLocalDescription(answer);
    await signalChannel.send(JSON.stringify(answer));
    console.log("answer sent");
    console.log("remote description is " + connection.remoteDescription);
}

async function setIceCandidate(candidate) {
    await connection.addIceCandidate(candidate);
}

