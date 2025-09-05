class v {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    static random() {
        return new v(Math.random() - 0.5, Math.random() - 0.5);
    }

    length() {
        return Math.sqrt(this.x  *this.x + this.y * this.y);
    }

    mul(scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    mirror_x() {
        this.x *= -1;
        return this;
    }

    mirror_y() {
        this.y *= -1;
        return this;
    }

    norm() {
        return this.mul(1.0 / this.length());
    }

    add(another) {
        this.x += another.x;
        this.y += another.y;
        return this;
    }

    copy() {
        return new v(this.x, this.y);
    }

    toString() {
        return `Vector[${this.x} : ${this.y}]`
    }
}

let canvas;

let radius = 30;
let coords = new v(100, 200);

let canvas_dim = new v(200, 400);

let speed = 4;

let direction = v.random().norm();
console.log(`our random direction is ${direction}`);

let delta = direction.copy().mul(speed);
console.log(`out delta is ${delta}`);

function update_cicle_state() {
    setTimeout(() => {
        coords.add(delta);
        if ((coords.x < 0 + radius) || (coords.x > canvas_dim.x - radius)) {
            //bounce off of x walls
            delta.mirror_x();
        }
        if ((coords.y < 0 + radius) || (coords.y > canvas_dim.y - radius)) {
            //bounce off of y walls
            delta.mirror_y();
        }
        update_cicle_state();
    }, 30);
}

update_cicle_state();

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
    ctx.fillStyle = "lightBlue";
    ctx.ellipse(coords.x, coords.y, radius, radius, 0, 0 ,2 * Math.PI);
    ctx.fill();

    window.requestAnimationFrame(game_loop);
}

window.addEventListener("load", () => {
    game_loop();
});

