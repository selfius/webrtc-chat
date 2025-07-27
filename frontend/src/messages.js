export function add_incoming_message(text) {
    add_message("incoming-message-template", text);
}

export function add_outgoing_message(text) {
    console.log(text);
    add_message("outgoing-message-template", text);
}

function add_message(template_id, text) {
    let newMessage = document.getElementById(template_id).cloneNode(/*deep= */true);
    newMessage.id = undefined;

    newMessage.textContent = newMessage.textContent.replace("%message%", text);

    document.getElementById("message-log").appendChild(newMessage);
    newMessage.scrollIntoView();
}

