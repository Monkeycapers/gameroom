
var connection;

var renderList;

var timer;

var sleeptime = 0;

var wallHeight = 10;

var wallWidth = 10;

var mapWidth = 100;

var mapHeight = 100;

window.onload = function () {
	//hideall();
	
	renderList = [100,100,100,10,10];
	
	
	//Detect if the browser has websocket support
	try{
    connection = new WebSocket('ws:184.145.105.100:8080');
	connection.onopen = function () {
		timer = setInterval(snakeTimer, sleeptime);
	};
	connection.onerror = function (e) {
		connection.close();
	};
	connection.onmessage = function (e) {
		console.log(e.data);
		
		var input = JSON.parse(e.data);
		
		var argument = input['argument'];
		
		mapWidth = input['mapwidth'] + 2;
		
		mapHeight = input['mapheight'] + 2;
		
		
		console.log(argument);
		
		console.log("input :" + input);
		
		console.log(argument == 'lobbydraw');
		
		if (argument == 'lobbydraw') {
			renderList = JSON.parse((JSON.stringify(input['render'])).replace(/['"]+/g, ''));
		}
		
		
	}
  }catch(e){
	console.log(e);
  }

}

function snakeTimer() {
	try {
		var c = document.getElementById("draw");
		var ctx = c.getContext("2d");
		
		ctx.fillStyle = "black";
		
		ctx.fillRect(0, 0, 1920, 1080);
		
		for (var i = 0; i < renderList.length; i += 5) {
			ctx.fillStyle = 'rgb(' + renderList[i] + ',' + renderList [i + 1] + ',' + renderList[i + 2] + ')';
			ctx.fillRect(renderList[i + 3] * wallWidth , renderList[i + 4] * wallHeight, wallWidth, wallHeight);
		}
	}
	catch (e) {
		console.log(e);
	}
}

function consoleinput(input) {
	connection.send(input);
}