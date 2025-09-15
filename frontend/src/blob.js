let canvas;
let data = {
    x: 100,
    y: 200,
    radius: 60,
};

function game_loop(timestamp) {
    if (!canvas) {
        canvas = document.getElementById("blob_canvas");
    }
    if (!canvas.getContext) {
        console.log("I'm outta here");
        return;
    }


    let ctx = canvas.getContext("2d");
    ctx.reset();
    ctx.beginPath();
    ctx.fillStyle = "red";
    ctx.ellipse(data.x, data.y, data.radius, data.radius, 0, 0, 2 * Math.PI);
    ctx.fill();

    window.requestAnimationFrame(game_loop);
}

window.addEventListener("load", () => {
    game_loop();
    if (window.Worker) {
        const myWorker = new Worker(new URL("physics.js", import.meta.url));
        console.log('Worker created');

        myWorker.onmessage = (e) => {
            data = e.data;
        }
    } else {
        console.log('Workers not supported')
    }
});
