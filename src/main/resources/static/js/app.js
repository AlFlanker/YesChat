function connect() {
    // ws = new WebSocket('ws://localhost:8080/test');
    ws = new WebSocket('ws://188.225.32.238:9000/test');
    ws.onmessage = function(data){
        showGreeting(data.data);
    }
    // setConnected(true);
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    ws.send($("#name").val());

}

function showGreeting(message) {
    $("#greeting").append("<tr><td>" + message + "</td></tr>");
}
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});