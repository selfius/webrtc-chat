import {add_incoming_message, add_outgoing_message} from './messages.js';

let connection = undefined;
const signalChannel = new WebSocket('/signal');

const roomId = document.URL.match("room/([\\w-]*)$")[1];
const uidCookie = await cookieStore.get("uid");
const userId = uidCookie.value;
const init_request = JSON.stringify({
    type: "initiate",
    senderUserId: userId,
    roomId: roomId
});

signalChannel.addEventListener("message", async (event) => {
    console.log(" >> " + event.data);
    let data = JSON.parse(event.data);
    switch(data.type) {
        case "start_sync" :
            await sendOffer(data);
            break;
        case "stun_credentials" :
            connection = initRTCConnection(data.sdp);
            break;
        case "answer":
            await receiveAnswer(data);
            break;
        case "offer":
            await createAnswer(data);
            break;
        case  "ice_candidate":
            await setIceCandidate(JSON.parse(data.sdp));
            break;
    };
});

let sending_init = false;

signalChannel.addEventListener("open", async (event) => {
    sending_init = true;
    signalChannel.send(init_request);
});

if (!sending_init && signalChannel.readyState == /*OPEN*/ 1) {
    sending_init = true;
    signalChannel.send(init_request);
}

function initRTCConnection(credentials) {
    let connection = undefined;
    if (credentials) {
        const username = credentials.split(":")[0];
        const password = credentials.split(":")[1];
        const config = {
            iceServers : [
                {urls : "turn:turn.sviri.dev:3478",
                    username : username,
                    credential: password
                }
            ]
        }
        connection = new RTCPeerConnection(config);
        console.log(" initializing rtc with " + credentials);
    } else {
        connection = new RTCPeerConnection();
    }
    connection.addEventListener("icecandidate", async (event) => {
        console.log("sending ice cadidate");
        await signalChannel.send(JSON.stringify({
            type: "ice_candidate",
            sdp: JSON.stringify(event.candidate),
            senderUserId: userId,
            roomId: roomId
        }));
    });

    connection.addEventListener("datachannel", (event) => {
        wireInputsToChannel(event.channel);
    });
    return connection;
}

function wireInputsToChannel(channel) {
    const messageInput = document.getElementById("message_input");
    messageInput.onkeydown =
        (event) => {
            if (event.key == "Enter" && messageInput.value.trim()) {
                try{
                    channel.send(messageInput.value);
                    add_outgoing_message(messageInput.value);
                    messageInput.value = "";
                } catch (err) {
                    console.log(err);
                }
            }
        };

    channel.addEventListener("message", (event) => { 
        console.log(event);
        add_incoming_message(event.data);
    });
}

async function sendOffer(data) {
    connection = initRTCConnection(data.sdp);

    const channel = connection.createDataChannel("chat");
    channel.addEventListener("open", (event) => {
        console.log("opened the channel");
        wireInputsToChannel(channel);
    });

    const offer = await connection.createOffer();
    offer.senderUserId = userId;
    offer.roomId = roomId;
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
    console.log("setting offer " + JSON.stringify(offer));
    await connection.setRemoteDescription(offer);
    console.log("remote description is " + connection.remoteDescription);
    const answer = await connection.createAnswer();
    answer.senderUserId = userId;
    answer.roomId = roomId;
    await connection.setLocalDescription(answer);
    await signalChannel.send(JSON.stringify(answer));
    console.log("answer sent");
    console.log("remote description is " + connection.remoteDescription);
}

async function setIceCandidate(candidate) {
    await connection.addIceCandidate(candidate);
}

