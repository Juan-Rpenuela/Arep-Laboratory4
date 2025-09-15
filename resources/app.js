function loadGetMsg() {
    let nameVar = document.getElementById("name").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        document.getElementById("getrespmsg").innerHTML = this.responseText;
    }
    xhttp.open("GET", "/app/greeting?name=" + nameVar);
    xhttp.send();
}