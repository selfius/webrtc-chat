export function add_incoming_message(text) {
    add_message("incoming-message-template", text);
}

export function add_outgoing_message(text) {
    console.log(text);
    add_message("outgoing-message-template", text);
}

function add_message(template_id, text) {
    let newMessageContainer = document.getElementById(template_id).cloneNode(/*deep= */true);
    newMessageContainer.removeAttribute("id");

    const newMessage = newMessageContainer.querySelector('.message');
    newMessage.textContent = text;

    document.getElementById("message-log").appendChild(newMessageContainer);
    newMessageContainer.scrollIntoView();
}

